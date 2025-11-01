package io.github.fnvm.jradio.ui.menu;

import java.awt.Desktop;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import io.github.fnvm.jradio.core.service.HistoryService;
import io.github.fnvm.jradio.player.Player;
import io.github.fnvm.jradio.ui.terminal.MenuController;
import io.github.fnvm.jradio.ui.terminal.TerminalManager;

public class RecentlyPlayed {
	private int currentSelection;
	private Player player;
	private HistoryService historyService;

	private static final Logger LOGGER = System.getLogger(RecentlyPlayed.class.getName());

	public RecentlyPlayed(HistoryService historyService, Player player) {
		currentSelection = 0;
		this.historyService = historyService;
		this.player = player;
	}

	public void showRecentlyPlayed(TerminalManager terminal) throws IOException {

		currentSelection = 0;
		Map<String, Consumer<Integer>> options = new HashMap<>();
		boolean[] back = { false };
		options.put("b", (c) -> back[0] = true);
		options.put("d", (c) -> historyService.clear());
		options.put("p", (c) -> {
			if (player.isPlaying()) {
				player.stop();
			}
		});

		while (!back[0]) {
			List<String> list = new ArrayList<>(historyService.getAll());
			Collections.reverse(list);

			if (list.size() > 12) {
				list = new ArrayList<>(list.subList(0, 12));
			}

			String[] items = list.toArray(String[]::new);

			MenuController menu = new MenuController(terminal, "Recently Played",
					currentSelection,
					new String[] { "Clear history (D)", "", "↩  Back (B)" },
					options,
					items);
			menu.setPlayer(player);

			int temp = menu.show();
			if (temp >= 10_000) {
				currentSelection = temp - 10_000;
				String songName = items[currentSelection];
				youtubeSearch(songName);
			} else {
				currentSelection = temp;
			}
		}
	}

	private static void youtubeSearch(String query) {
		try {
			String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
			String url = "https://www.youtube.com/results?search_query=" + encodedQuery;

			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				Desktop.getDesktop().browse(new URI(url));
			} else {
				LOGGER.log(Level.ERROR, () -> "Browser not supported");
			}

		} catch (IOException | URISyntaxException e) {
			LOGGER.log(Level.ERROR, () -> "Unsupported Encoding Exception: " + e.getMessage());
		}
	}
}
