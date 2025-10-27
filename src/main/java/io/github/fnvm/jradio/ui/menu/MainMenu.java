package io.github.fnvm.jradio.ui.menu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import io.github.fnvm.jradio.data.StorageManager;
import io.github.fnvm.jradio.player.Player;
import io.github.fnvm.jradio.ui.terminal.MenuController;
import io.github.fnvm.jradio.ui.terminal.TerminalManager;

public class MainMenu {
	private final TerminalManager terminal;
	private int currentSelection;
	private Player player;

	public MainMenu(TerminalManager terminal) {
		this.terminal = terminal;
		currentSelection = 0;
		player = null;
	}

	public void run() throws IOException {

		// TODO отображение текущего трека
		// TODO история треков
		List<String> recentlyPlayedTitles = new StorageManager().loadHistory();

		player = new Player(recentlyPlayedTitles);
		StationsMenu stationsMenu = new StationsMenu(player);
		RecentlyPlayed recentlyPlayed = new RecentlyPlayed(recentlyPlayedTitles);

		while (true) {
			final boolean[] back = { false };
			Map<Character, Consumer<Integer>> hotkeys = new HashMap<>();
			hotkeys.put('b', (c) -> back[0] = true);

			MenuController mainMenu = new MenuController(terminal, "Jradio", currentSelection, new String[] {}, hotkeys,
					new String[] { "All Stations", "Recently Played", "Exit" });

			currentSelection = mainMenu.show() - 10_000;

			if (back[0] == true)
				exit();
			switch (currentSelection) {
				case -1 -> exit();
				case 0 -> stationsMenu.showStationsMenu(terminal);
				case 1 -> recentlyPlayed.showRecentlyPlayed(terminal);
				case 2 -> exit();
			}
		}
	}

	private void exit() {
		if (player.isPlaying())
			player.stop();
		System.exit(0);
	}

}
