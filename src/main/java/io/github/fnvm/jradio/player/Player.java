package io.github.fnvm.jradio.player;

import java.io.*;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
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
	private volatile String streamTitle = "";
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
	}

	public void play(RadioStation station) {
		stop();
		currentStation = station;
		String url = station.getUrl();

		if (ffplayAvailable == false) {
			LOGGER.log(Level.ERROR, () -> "Ffplay not found");
			return;
		}

		try {
			ProcessBuilder builder = new ProcessBuilder(ffplayCommand, "-nodisp", "-autoexit", "-loglevel", "info",
					url);
			builder.redirectErrorStream(true);
			process = builder.start();
			LOGGER.log(Level.INFO, () -> "Playing station: " + station.getName());


			historyService.add(station.getName());

			new Thread(this::readMetadata).start();

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

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.toLowerCase().contains("ffplay")) {
						return true;
					}
				}
			}

			int exitCode = process.waitFor();
			return exitCode == 0;
		} catch (IOException | InterruptedException e) {
			return false;
		}
	}

	public void readMetadata() {
		if (process.isAlive()) {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.contains(":")) {
						String[] parts = line.split(":", 2);
						if (parts.length == 2) {
							String key = parts[0].trim();
							String value = parts[1].trim();
							if (!key.equalsIgnoreCase("Metadata")) {
								metadata.put(key, value);

								if (key.equalsIgnoreCase("StreamTitle")) {
									streamTitle = value;
								}
							}
						}
					}

				}
			} catch (IOException e) {
				LOGGER.log(Level.ERROR, () -> "Error reading ffplay output: " + e.getMessage());
			}
		}
	}
}
