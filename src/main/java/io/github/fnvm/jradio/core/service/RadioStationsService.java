package io.github.fnvm.jradio.core.service;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import io.github.fnvm.jradio.core.model.RadioStation;
import io.github.fnvm.jradio.core.model.RadioStationManager;
import io.github.fnvm.jradio.data.StorageManager;

public class RadioStationsService {
	private RadioStationManager stationManager;
	private StorageManager storageManager;

	private static final Logger LOGGER = System.getLogger(RadioStationsService.class.getName());

	public RadioStationsService() {
		stationManager = new RadioStationManager();
		storageManager = new StorageManager();
		loadData();
	}

	public void addStation(RadioStation station) {
		stationManager.addStation(station);
		saveData();
	}

	public void removeStation(String id) {
		stationManager.removeStation(id);
		saveData();
	}

	public void saveData() {
		try {
			storageManager.saveStations(stationManager.getAllStations());
		} catch (IOException e) {
			LOGGER.log(Level.ERROR, () -> "Save Data Error: " + e.getMessage());
		}
	}

	public void loadData() {
		try {
			List<RadioStation> stations = storageManager.loadStations();
			stations.forEach(stationManager::addStation);
		} catch (IOException e) {
			LOGGER.log(Level.ERROR, () -> "Failed to load stations" + e.getMessage());
		}
	}

	public List<RadioStation> getAllStations() {
		return stationManager.getAllStations();
	}

	public void toggleFavorite(String id) {
		stationManager.toggleFavorite(id);
	}
}
