package io.github.fnvm.jradio.ui.menu;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import io.github.fnvm.jradio.ui.terminal.MenuController;
import io.github.fnvm.jradio.ui.terminal.TerminalManager;

public class MainMenu {
	private final TerminalManager terminal;

	public MainMenu(TerminalManager terminal) {
		this.terminal = terminal;
	}

	public void run() throws IOException {
		
		// TODO отображение текущего трека
		// TODO история треков

		StationsMenu stationsMenu = new StationsMenu();
		RecentlyPlayed recentlyPlayed = new RecentlyPlayed();

		while (true) {
			final boolean[] back = { false };
			Map<Character, Consumer<Integer>> hotkeys = new HashMap<>();
			hotkeys.put('b', (c) -> back[0] = true);
			
			MenuController mainMenu = new MenuController(terminal, "Jradio", 0, new String[] {}, hotkeys,
					new String[] { "All Stations", "Recently Played", "Exit" });
			
			int choice = mainMenu.show();

			if (back[0] == true) System.exit(0);
			switch (choice) {
			case -1 -> System.exit(0);
			case 0 -> stationsMenu.showStationsMenu(terminal);
			case 1 -> recentlyPlayed.showRecentlyPlayed(terminal);
			case 2 -> System.exit(0);
			}
		}
	}

}
