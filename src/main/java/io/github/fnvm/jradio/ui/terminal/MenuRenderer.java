package io.github.fnvm.jradio.ui.terminal;

import java.util.Arrays;
import java.util.Comparator;

public class MenuRenderer {
	private final TerminalManager terminal;
	private final String title;
	private final String[] menuItems;
	private final String[] inactiveItems;

	public MenuRenderer(TerminalManager terminal, String title, String[] inactiveItems, String[] menuItems) {
		this.terminal = terminal;
		this.title = title;
		this.menuItems = menuItems;
		this.inactiveItems = inactiveItems;
	}

	public void render(int currentSelection) {
		terminal.clearScreen();
		terminal.println("=".repeat(16) + " " + title + " " + "=".repeat(16) + System.lineSeparator());

		for (int i = 0; i < Math.max(inactiveItems.length, menuItems.length); i++) {
			String[] items = buildLine(i);
			if (i == currentSelection) {
				terminal.print("\u001B[1;32m> ");
			} else {
				terminal.print("  \u001B[0m");
			}
			terminal.print(items[0]);
			terminal.print("\u001B[0m");
			terminal.print("\u001B[90m");
			terminal.print(items[1]);
			terminal.println("\u001B[0m");
		}

		terminal.print("\u001B[0m");
		terminal.flush();
	}

	private String[] buildLine(int i) {
		String longest = Arrays.stream(menuItems).max(Comparator.comparingInt(String::length)).orElse(" ".repeat(11));

		String[] r = new String[2];
		int lineSize = (longest.length() > 11) ? longest.length() + 5 : 16;

		if (i < inactiveItems.length && i < menuItems.length) {
			int ost = lineSize - menuItems[i].length();
			r[0] = menuItems[i] + " ".repeat(ost);
			r[1] = inactiveItems[i];
			return r;
		} else if (i < inactiveItems.length) {
			r[0] = " ".repeat(lineSize);
			r[1] = inactiveItems[i];
			return r;
		} else if (i < menuItems.length) {
			r[0] = menuItems[i];
			r[1] = "";
			return r;
		}

		r[0] = "";
		r[1] = "";
		return r;
	}
}
