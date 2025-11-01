package io.github.fnvm.jradio.core.model;

import java.util.*;
import java.util.stream.Collectors;

public class RadioStationManager {
	private Map<String, RadioStation> stations;

	public RadioStationManager() {
		this.stations = new LinkedHashMap<>();
	}

	public void addStation(RadioStation station) {
		stations.put(station.getId(), station);
	}

	public void removeStation(String id) {
		stations.remove(id);
	}

	public RadioStation getStation(String id) {
		return stations.get(id);
	}

	public List<RadioStation> getAllStations() {
		
		List<RadioStation> load = new ArrayList<>(stations.values());
		List<RadioStation> temp = new ArrayList<>();
		for (RadioStation station : load) {
			if (station.isFavorite()) {
				temp.add(station);
			}
		}
		for (RadioStation station : load) {
			if (!station.isFavorite()) {
				temp.add(station);
			}
		}
		return temp;
	}

	public List<RadioStation> getFavorites() {
		return stations.values().stream().filter(RadioStation::isFavorite).collect(Collectors.toList());
	}

	public List<RadioStation> searchStations(String query) {
		return stations.values().stream()
				.filter(station -> station.getName().toLowerCase().contains(query.toLowerCase())
						|| station.getNote().toLowerCase().contains(query.toLowerCase()))
				.collect(Collectors.toList());
	}

	public void toggleFavorite(String id) {
		RadioStation station = stations.get(id);
		if (station != null) {
			station.setFavorite(!station.isFavorite());
		}
	}

}
