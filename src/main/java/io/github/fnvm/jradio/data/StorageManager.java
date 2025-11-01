package io.github.fnvm.jradio.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import io.github.fnvm.jradio.App;
import io.github.fnvm.jradio.core.model.RadioStation;

public class StorageManager {
	private static final Path CONFIG_DIR = getConfigDirectory();
	private static final Path DATA_STATIONS = CONFIG_DIR.resolve("data.json");
	private static final Path HISTORY_DATA = CONFIG_DIR.resolve("history.json");

	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private final TypeToken<List<RadioStation>> collectionType = new TypeToken<List<RadioStation>>() {
	};
	private final TypeToken<List<String>> historyColType = new TypeToken<List<String>>() {
	};
	private static final Logger LOGGER = System.getLogger(StorageManager.class.getName());

	public List<RadioStation> loadStations() throws IOException {
		if (!Files.exists(DATA_STATIONS)) {
			return new ArrayList<>();
		}

		String json = Files.readString(DATA_STATIONS);
		if (json.isBlank()) {
			return new ArrayList<>();
		}

		try {
			List<RadioStation> stations = gson.fromJson(json, collectionType);
			return stations != null ? stations : new ArrayList<>();
		} catch (JsonSyntaxException e) {
			LOGGER.log(Level.ERROR, "Failed to parse JSON from storage: {0}", e.getMessage());
			return new ArrayList<>();
		}
	}

	public void saveStations(List<RadioStation> stations) throws IOException {
		String json = gson.toJson(stations);
		Files.createDirectories(CONFIG_DIR);

		Files.writeString(DATA_STATIONS, json, StandardCharsets.UTF_8, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);
	}

	public void saveHistory(List<String> recentlyPlayed) throws IOException {
		if (recentlyPlayed.size() > 100) {
			recentlyPlayed = new ArrayList<>(
					recentlyPlayed.subList(recentlyPlayed.size() - 100, recentlyPlayed.size()));
		}
		String json = gson.toJson(recentlyPlayed);
		Files.createDirectories(CONFIG_DIR);

		Files.writeString(HISTORY_DATA, json, StandardCharsets.UTF_8, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);
	}

	public List<String> loadHistory() throws IOException {
		if (!Files.exists(HISTORY_DATA)) {
			return new ArrayList<>();
		}

		String json = Files.readString(HISTORY_DATA);
		if (json.isBlank()) {
			return new ArrayList<>();
		}

		try {
			List<String> history = gson.fromJson(json, historyColType);
			return history != null ? history : new ArrayList<>();
		} catch (JsonSyntaxException e) {
			LOGGER.log(Level.ERROR, () -> "Failed to parse JSON with playing history");
			return new ArrayList<>();
		}

	}

	public void removeHistory() throws IOException {
		saveHistory(new ArrayList<String>());
	}

	public static Path getPath() {
		return DATA_STATIONS;
	}

	public File extractFfplay() throws IOException {
		String resourcePath;
		if (App.OS.contains("win")) {
			resourcePath = "/ffmpeg/ffplay.exe";
		} else {
			resourcePath = "/ffmpeg/ffplay";
		}

		Path targetPath = CONFIG_DIR.resolve(resourcePath.substring(resourcePath.lastIndexOf('/') + 1));

		if (Files.exists(targetPath)) {
			File existingFile = targetPath.toFile();

			if (!App.OS.contains("win") && !existingFile.canExecute()) {
				existingFile.setExecutable(true);
			}

			return existingFile;
		}

		try (InputStream in = getClass().getResourceAsStream(resourcePath)) {
			if (in == null) {
				throw new IOException("Resource not found: " + resourcePath);
			}

			Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
		}

		if (!App.OS.contains("win")) {
			targetPath.toFile().setExecutable(true);
		}

		return targetPath.toFile();
	}

	private static Path getConfigDirectory() {

		if (App.OS.contains("win")) {
			return Path.of(System.getenv("APPDATA"), "jradio");
		} else {
			return Path.of(System.getProperty("user.home"), ".config", "jradio");

		}
	}

}
