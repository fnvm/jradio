package io.github.fnvm.jradio.ui.menu;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import io.github.fnvm.jradio.core.service.HistoryService;
import io.github.fnvm.jradio.ui.terminal.MenuController;
import io.github.fnvm.jradio.ui.terminal.TerminalManager;

public class RecentlyPlayed {
	private int currentSelection;

	private HistoryService historyService;

	public RecentlyPlayed(HistoryService historyService) {
		currentSelection = 0;
		this.historyService = historyService;
	}

	public void showRecentlyPlayed(TerminalManager terminal) throws IOException {

		currentSelection = 0;
        Map<Character, Consumer<Integer>> options = new HashMap<>();
        boolean[] back = { false };
        options.put('b', (c) -> back[0] = true);
        options.put('d', (c) -> historyService.clear());
        
        while (!back[0]) {
            String[] items = historyService.getAll().toArray(String[]::new);
            MenuController menu = new MenuController(terminal, "Recently Played",
                currentSelection,
                new String[]{"Clear history (D)", "↩ Back (B)"},
                options,
                items);

            currentSelection = menu.show() - 10_000;
        }
	}
}
