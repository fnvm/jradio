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
	private final String[] inactiveItems;
	private Player player;
	private String metadataLine = "";
	private List<List<String>> pagesContent;
	private int currentPageSelection = 1;

	private final int metadataRow = 3;

	public MenuRenderer(TerminalManager terminal, String title, String[] inactiveItems, String[] menuItems) {
		this.terminal = terminal;
		this.title = title;
		this.menuItems = menuItems;
		this.inactiveItems = inactiveItems;
		this.pagesContent = new ArrayList<List<String>>();
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

	public void setPage(int currentPageSelection) {
		this.currentPageSelection = currentPageSelection;
	}

	public void render(int currentSelection) {
		updateMetadata();

		terminal.clearScreen();
		terminal.println("=".repeat(16) + " " + title + " " + "=".repeat(16));
		terminal.print(System.lineSeparator());

		if (!metadataLine.isEmpty()) {
			terminal.println(metadataLine);
		} else {
			terminal.print("");
		}

		int pages = 1;
		if (menuItems.length > 15) {
			pages = (int) Math.ceil((double) menuItems.length / 15);
			for (int i = 0; i < pages; i++) {
				List<String> currentPage = Arrays.asList(menuItems);
				currentPage = currentPage.subList(i * 15, (i + 1) * 15 - 1);
				pagesContent.add(currentPage);
			}

		} else {
			pagesContent.add(Arrays.asList(menuItems));
		}

		List<String> currentPageMenuItems = pagesContent.get(currentPageSelection - 1);

		for (int j = 0; j < Math.max(inactiveItems.length, currentPageMenuItems.size()); j++) {
			if (j == 0)
				terminal.print(System.lineSeparator());
			String[] items = buildLine(j, currentPageMenuItems);

			if (j == currentSelection)
				terminal.print("\u001B[1;32m> ");
			else
				terminal.print("  ");

			terminal.print(items[0]);
			terminal.print("\u001B[0m\u001B[90m");
			terminal.println(items[1] + "\u001B[0m");
		}

		terminal.flush();
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

	private String[] buildLine(int i, List<String> currentPageMenuItems) {
		String longest = currentPageMenuItems.stream().max(Comparator.comparingInt(String::length)).orElse(" ".repeat(11));
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
