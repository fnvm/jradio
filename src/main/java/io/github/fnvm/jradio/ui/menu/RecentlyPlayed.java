package io.github.fnvm.jradio.ui.menu;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import io.github.fnvm.jradio.ui.terminal.MenuController;
import io.github.fnvm.jradio.ui.terminal.TerminalManager;

public class RecentlyPlayed {
	private int currentSelection;

	private List<String> recentlyPlayed;

	public RecentlyPlayed(List<String> recentlyPlayed) {
		currentSelection = 0;
		this.recentlyPlayed = recentlyPlayed;
	}

	public void showRecentlyPlayed(TerminalManager terminal) throws IOException {

		Map<Character, Consumer<Integer>> options = new HashMap<>();
		boolean[] back = new boolean[] { false };
		options.put('b', (c) -> back[0] = true);

		String[] inactiveItems = new String[] { "Remove history (D)", "↩  Back (B)" };

		while (true) {
			String[] recentlyPlayedTitles = recentlyPlayed.stream().toArray(String[]::new);
			MenuController resentlyPlayedMenu = new MenuController(terminal, "Recently Played", currentSelection,
					inactiveItems, options,
					recentlyPlayedTitles);

			currentSelection = resentlyPlayedMenu.show() - 10_000;

			if (back[0] == true)
				return;
		}
	}
}
