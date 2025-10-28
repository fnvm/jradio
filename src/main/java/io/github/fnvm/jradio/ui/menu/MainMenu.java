package io.github.fnvm.jradio.ui.menu;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import io.github.fnvm.jradio.core.service.HistoryService;
import io.github.fnvm.jradio.data.StorageManager;
import io.github.fnvm.jradio.player.Player;
import io.github.fnvm.jradio.ui.terminal.MenuController;
import io.github.fnvm.jradio.ui.terminal.TerminalManager;

public class MainMenu {
	private final TerminalManager terminal;
	private int currentSelection;
	private final Player player;
	private final HistoryService historyService;
	private final StorageManager storage;

	public MainMenu(TerminalManager terminal) throws IOException {
		this.terminal = terminal;
		currentSelection = 0;
		storage = new StorageManager();
		this.historyService = new HistoryService(storage);
		this.player = new Player(historyService);
	}

	public void run() throws IOException {

		StationsMenu stationsMenu = new StationsMenu(player);
		RecentlyPlayed recentlyPlayed = new RecentlyPlayed(historyService, player);

		while (true) {
			final boolean[] back = { false };
			Map<String, Consumer<Integer>> hotkeys = new HashMap<>();
			hotkeys.put("b", (c) -> back[0] = true);
			hotkeys.put("p", (c) -> {
				if (player.isPlaying())
					player.stop();
			});

			MenuController mainMenu = new MenuController(terminal, "Jradio", currentSelection, new String[] {}, hotkeys,
					new String[] { "All Stations", "Recently Played", "Exit" });
			mainMenu.setPlayer(player);

			int temp = mainMenu.show();

			if (back[0] == true)
				exit();
			switch (temp) {
			case -1 -> exit();
			case 10_000 -> stationsMenu.showStationsMenu(terminal);
			case 10_001 -> recentlyPlayed.showRecentlyPlayed(terminal);
			case 10_002 -> exit();
			}

			currentSelection = (temp >= 10_000 ? temp - 10_000 : temp);

		}
	}

	private void exit() {
		if (player.isPlaying())
			player.stop();
		System.exit(0);
	}

}
