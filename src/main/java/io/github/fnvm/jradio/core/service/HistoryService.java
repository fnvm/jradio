package io.github.fnvm.jradio.core.service;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;


import io.github.fnvm.jradio.data.StorageManager;

public class HistoryService {

    private final StorageManager storageManager;
    private final List<String> recentlyPlayed;
    
    private static final Logger LOGGER = System.getLogger(HistoryService.class.getName());

    public HistoryService(StorageManager storageManager) throws IOException {
        this.storageManager = storageManager;
        this.recentlyPlayed = new ArrayList<>(storageManager.loadHistory());
    }

    public void add(String streamTitle) {
    	if (streamTitle.isBlank()) return;
    	
        if (recentlyPlayed.isEmpty() || !recentlyPlayed.get(recentlyPlayed.size() - 1).equals(streamTitle)) {
            recentlyPlayed.add(streamTitle);
            save();
        }
    }

    public List<String> getAll() {
        return List.copyOf(recentlyPlayed);
    }

    public void clear() {
        recentlyPlayed.clear();
        save();
    }

    private void save() {
        try {
            storageManager.saveHistory(recentlyPlayed);
        } catch (IOException e) {
        	LOGGER.log(Level.ERROR, () -> "Failed to write history " + e.getMessage());
        }
    }
}
