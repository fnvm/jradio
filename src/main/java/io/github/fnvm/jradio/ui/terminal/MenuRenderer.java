package io.github.fnvm.jradio.ui.terminal;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.lang.Math;
import io.github.fnvm.jradio.player.Player;

public class MenuRenderer {
	private final TerminalManager terminal;
	private final String title;
	private final String[] menuItems;
	private final String[] originalInactiveItems;
	private Player player;
	private String metadataLine = "";
	private List<List<String>> pagesContent;

	private final int metadataRow = 3;

	public MenuRenderer(TerminalManager terminal, String title, String[] inactiveItems, String[] menuItems) {
		this.terminal = terminal;
		this.title = title;
		this.menuItems = menuItems;
		this.originalInactiveItems = inactiveItems;
		this.pagesContent = new ArrayList<>();
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	private void updateMetadata() {
		if (player != null && player.isPlaying() && player.getCurrentStation() != null) {
			String st = player.getStreamTitle();
			metadataLine = "\u001B[36m >> [" + player.getCurrentStation().getName() + "] " + (st != null ? st : "")
					+ "\u001B[0m";
		} else {
			metadataLine = "";
		}
	}

	public void render(int currentSelection, int currentPageSelection) {
		updateMetadata();

		terminal.clearScreen();
		terminal.println("=".repeat(16) + " " + title + " " + "=".repeat(16));
		terminal.print(System.lineSeparator());

		if (!metadataLine.isEmpty()) {
			terminal.println(metadataLine);
		} else {
			terminal.print("");
		}

		pagesContent.clear();

		int totalPages = 1;
		if (menuItems.length > MenuController.ITEMS_PER_PAGE) {
			totalPages = (int) Math.ceil((double) menuItems.length / MenuController.ITEMS_PER_PAGE);

			for (int i = 0; i < totalPages; i++) {
				int fromIndex = i * MenuController.ITEMS_PER_PAGE;
				int toIndex = Math.min((i + 1) * MenuController.ITEMS_PER_PAGE, menuItems.length);

				List<String> currentPage = Arrays.asList(menuItems).subList(fromIndex, toIndex);
				pagesContent.add(currentPage);
			}
		} else {
			pagesContent.add(Arrays.asList(menuItems));
		}

		String[] inactiveItems = buildInactiveItems(totalPages, currentPageSelection);


		int pageIndex = Math.max(0, Math.min(currentPageSelection - 1, pagesContent.size() - 1));
		List<String> currentPageMenuItems = pagesContent.get(pageIndex);

		for (int i = 0; i < Math.max(inactiveItems.length, currentPageMenuItems.size()); i++) {
			if (i == 0)
				terminal.print(System.lineSeparator());
			String[] items = buildLine(i, currentPageMenuItems, inactiveItems);

			if (i == currentSelection)
				terminal.print("\u001B[1;32m> ");
			else
				terminal.print("  ");

			terminal.print(items[0]);
			terminal.print("\u001B[0m\u001B[90m");
			terminal.println(items[1] + "\u001B[0m");
		}

		terminal.flush();
	}

	private String[] buildInactiveItems(int totalPages, int currentPageSelection) {
		if (totalPages <= 1) {
			return originalInactiveItems;
		}

		String[] result = Arrays.copyOf(originalInactiveItems, originalInactiveItems.length + 1);
		result[originalInactiveItems.length] = "[" + currentPageSelection + " / " + totalPages + "]";
		return result;
	}

	public void refreshMetadataLine() {
		updateMetadata();

		terminal.print("\u001B[" + metadataRow + ";1H");
		terminal.print("\u001B[K");

		if (!metadataLine.isEmpty()) {
			terminal.print(metadataLine);
		}

		terminal.flush();
	}

	private String[] buildLine(int i, List<String> currentPageMenuItems, String[] inactiveItems) {
		String longest = currentPageMenuItems.stream().max(Comparator.comparingInt(String::length))
				.orElse(" ".repeat(11));
		int lineSize = Math.max(longest.length() + 5, 16);

		String[] res = new String[2];
		if (i < inactiveItems.length && i < currentPageMenuItems.size()) {
			res[0] = currentPageMenuItems.get(i) + " ".repeat(lineSize - currentPageMenuItems.get(i).length());
			res[1] = inactiveItems[i];
		} else if (i < inactiveItems.length) {
			res[0] = " ".repeat(lineSize);
			res[1] = inactiveItems[i];
		} else if (i < currentPageMenuItems.size()) {
			res[0] = currentPageMenuItems.get(i);
			res[1] = "";
		} else {
			res[0] = "";
			res[1] = "";
		}
		return res;
	}
}