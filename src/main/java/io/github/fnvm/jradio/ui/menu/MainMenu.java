package io.github.fnvm.jradio.ui.menu;

import java.io.IOException;

import io.github.fnvm.jradio.ui.terminal.MenuController;
import io.github.fnvm.jradio.ui.terminal.TerminalManager;

public class MainMenu {
	private final TerminalManager terminal;

	public MainMenu(TerminalManager terminal) {
		this.terminal = terminal;
	}

	public void run() throws IOException {
		MenuController mainMenu = new MenuController(terminal, "Jradio", 0,
				new String[] { "All Stations", "Recently Played", "Exit" });

		// TODO выход из меню на кнопку B
		// TODO отображение текущего трека
		// TODO история треков

		StationsMenu stationsMenu = new StationsMenu();
		RecentlyPlayed recentlyPlayed = new RecentlyPlayed();

		while (true) {
			int choice = mainMenu.show();

			switch (choice) {
			case -1 -> exit();
			case 0 -> stationsMenu.showStationsMenu(terminal);
			case 1 -> recentlyPlayed.showRecentlyPlayed(terminal);
			case 2 -> exit();
			}
		}
	}

	private void exit() {
		System.exit(0);
	}
}
