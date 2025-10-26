package io.github.fnvm.jradio.data;

import java.io.File;
import java.io.FileNotFoundException;
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

import io.github.fnvm.jradio.core.model.RadioStation;

public class StorageManager {
	private static final Path CONFIG_DIR = getConfigDirectory();
	private static final Path DATA_FILE_PATH = CONFIG_DIR.resolve("data.json");

	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private final TypeToken<List<RadioStation>> collectionType = new TypeToken<List<RadioStation>>() {
	};
	private static final Logger LOGGER = System.getLogger(StorageManager.class.getName());

	public List<RadioStation> loadStations() throws IOException {
		if (!Files.exists(DATA_FILE_PATH)) {
			return new ArrayList<>();
		}

		String json = Files.readString(DATA_FILE_PATH);
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

		Files.writeString(DATA_FILE_PATH, json, StandardCharsets.UTF_8, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);
	}

	public static Path getPath() {
		return DATA_FILE_PATH;
	}

	public File extractFfplay() throws IOException {
		String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);

		String resourcePath = switch (os) {
		case "win" -> "/ffmpeg/windows/ffplay.exe";
		case "linux" -> "/ffmpeg/linux64/ffplay";
		default -> throw new FileNotFoundException("Binary for your system not found");
		};

		InputStream in = getClass().getResourceAsStream(resourcePath);
		Path targetPath = CONFIG_DIR.resolve(resourcePath.substring(resourcePath.lastIndexOf('/') + 1));

		Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
		in.close();

		if (!os.contains("win")) {
			targetPath.toFile().setExecutable(true);
		}

		return targetPath.toFile();
	}

	private static Path getConfigDirectory() {
		String os = System.getProperty("os.name").toLowerCase();

		if (os.contains("win")) {
			return Path.of(System.getenv("APPDATA"), "jradio");
		} else if (os.contains("mac")) {
			return Path.of(System.getProperty("user.home"), "Library", "Application Support", "jradio");
		} else {
			return Path.of(System.getProperty("user.home"), ".config", "jradio");

		}
	}

}
