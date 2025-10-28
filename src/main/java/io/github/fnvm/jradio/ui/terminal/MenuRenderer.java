package io.github.fnvm.jradio.ui.terminal;

import java.util.Arrays;
import java.util.Comparator;

import io.github.fnvm.jradio.player.Player;

public class MenuRenderer {
	private final TerminalManager terminal;
	private final String title;
	private final String[] menuItems;
	private final String[] inactiveItems;
	private Player player;
	private String metadataLine = "";

	private final int metadataRow = 2;

	public MenuRenderer(TerminalManager terminal, String title, String[] inactiveItems, String[] menuItems) {
		this.terminal = terminal;
		this.title = title;
		this.menuItems = menuItems;
		this.inactiveItems = inactiveItems;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	private void updateMetadata() {
		if (player != null && player.isPlaying() && player.getCurrentStation() != null) {
			String st = player.getStreamTitle();
			metadataLine = "\u001B[36m  >> [" + player.getCurrentStation().getName() + "] " + (st != null ? st : "")
					+ "\u001B[0m";
		} else {
			metadataLine = "";
		}
	}

	public void render(int currentSelection) {
		updateMetadata();

		terminal.clearScreen();
		terminal.println("=".repeat(16) + " " + title + " " + "=".repeat(16));

		if (!metadataLine.isEmpty()) {
			terminal.print(System.lineSeparator());
			terminal.println(metadataLine);
		} else {
			terminal.print("");
		}
		terminal.print(System.lineSeparator());

		for (int i = 0; i < Math.max(inactiveItems.length, menuItems.length); i++) {
			String[] items = buildLine(i);

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

	public void refreshMetadataLine() {
		updateMetadata();

		terminal.print("\u001B[" + metadataRow + ";1H");

		terminal.print("\u001B[K");

		if (!metadataLine.isEmpty()) {
			terminal.print(System.lineSeparator());
			terminal.print(metadataLine);
		}
		terminal.print(System.lineSeparator());

		terminal.flush();
	}

	private String[] buildLine(int i) {
		String longest = Arrays.stream(menuItems).max(Comparator.comparingInt(String::length)).orElse(" ".repeat(11));
		int lineSize = Math.max(longest.length() + 5, 16);

		String[] res = new String[2];
		if (i < inactiveItems.length && i < menuItems.length) {
			res[0] = menuItems[i] + " ".repeat(lineSize - menuItems[i].length());
			res[1] = inactiveItems[i];
		} else if (i < inactiveItems.length) {
			res[0] = " ".repeat(lineSize);
			res[1] = inactiveItems[i];
		} else if (i < menuItems.length) {
			res[0] = menuItems[i];
			res[1] = "";
		} else {
			res[0] = "";
			res[1] = "";
		}
		return res;
	}
}
