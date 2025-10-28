package io.github.fnvm.jradio.player;

import java.io.*;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.fnvm.jradio.core.model.RadioStation;
import io.github.fnvm.jradio.core.service.HistoryService;
import io.github.fnvm.jradio.data.StorageManager;

public class Player {

	private Process process;
	private String ffplayCommand;
	private RadioStation currentStation;
	private boolean ffplayAvailable;
	private final Map<String, String> metadata;
	private volatile String streamTitle;
	private final HistoryService historyService;

	private static final Logger LOGGER = System.getLogger(Player.class.getName());

	public Player(HistoryService historyService) {
		ffplayAvailable = true;
		if (isFfplayInPath()) {
			ffplayCommand = "ffplay";
		} else {
			try {
				File ffplayBinary = new StorageManager().extractFfplay();
				ffplayCommand = ffplayBinary.getAbsolutePath();
				LOGGER.log(Level.INFO, () -> "Using ffplay binary: " + ffplayCommand);
			} catch (IOException e) {
				LOGGER.log(Level.ERROR, () -> "Failed to extract ffplay " + e.getMessage());
				ffplayAvailable = false;
			}
		}

		this.historyService = historyService;
		metadata = new ConcurrentHashMap<>();
		currentStation = new RadioStation("", "");
		streamTitle = "";
	}

	public void play(RadioStation station) {
		stop();
		currentStation = station;
		String url = station.getUrl();

		if (!ffplayAvailable) {
			LOGGER.log(Level.ERROR, () -> "Ffplay not found");
			return;
		}

		try {
			ProcessBuilder builder = new ProcessBuilder(ffplayCommand, "-nodisp", "-autoexit", "-loglevel", "quiet",
					url);
			builder.redirectErrorStream(true);
			process = builder.start();
			LOGGER.log(Level.INFO, () -> "Playing station: " + station.getName());

			new Thread(() -> {
				while (isPlaying()) {
					fetchMetadata();
					try {
						Thread.sleep(10000);
					} catch (InterruptedException ignored) {
					}
				}
			}).start();

		} catch (IOException e) {
			LOGGER.log(Level.ERROR, () -> "Error starting ffplay: " + e.getMessage());
		}
	}

	public void stop() {
		if (process != null && process.isAlive()) {
			process.destroy();
		}
	}

	public boolean isPlaying() {
		return process != null && process.isAlive();
	}

	public RadioStation getCurrentStation() {
		return currentStation;
	}

	public String getStreamTitle() {
		return streamTitle;
	}

	private boolean isFfplayInPath() {
		try {
			Process process = new ProcessBuilder("ffplay", "-version").redirectErrorStream(true).start();
			int exitCode = process.waitFor();
			return exitCode == 0;
		} catch (IOException | InterruptedException e) {
			return false;
		}
	}

	private void fetchMetadata() {
		if (currentStation == null || currentStation.getUrl().isEmpty())
			return;

		try {
			URL url = new URL(currentStation.getUrl());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Icy-MetaData", "1");
			conn.setRequestProperty("User-Agent", "jRadioPlayer");
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);

			conn.connect();

			int metaInt = 0;
			String metaIntHeader = conn.getHeaderField("icy-metaint");
			if (metaIntHeader != null) {
				metaInt = Integer.parseInt(metaIntHeader);
			}

			if (metaInt > 0) {
				try (InputStream in = conn.getInputStream()) {
					in.skip(metaInt);
					int metaLen = in.read() * 16;
					if (metaLen > 0) {
						byte[] metaData = new byte[metaLen];
						int read = in.read(metaData, 0, metaLen);
						if (read > 0) {
							String metaString = new String(metaData, 0, read, "UTF-8");
							parseIcyMetadata(metaString);
						}
					}
				}
			}

			conn.disconnect();
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, () -> "Failed to fetch metadata: " + e.getMessage());
		}
	}

	private void parseIcyMetadata(String meta) {
		String[] parts = meta.split(";");
		for (String part : parts) {
			if (part.contains("=")) {
				String[] kv = part.split("=", 2);
				String key = kv[0].trim();
				String value = kv[1].trim().replaceAll("(^'|'$)", "");
				metadata.put(key, value);
				if (key.equalsIgnoreCase("StreamTitle")) {
					streamTitle = value;
					historyService.add(streamTitle);
					LOGGER.log(Level.INFO, () -> "StreamTitle updated: " + streamTitle);

				}
			}
		}
	}

}