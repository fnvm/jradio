package io.github.fnvm.jradio;

import java.io.IOException;

import io.github.fnvm.jradio.ui.menu.MainMenu;
import io.github.fnvm.jradio.ui.terminal.TerminalManager;

public class App {
	public static void main(String[] args) {
		try (TerminalManager terminal = new TerminalManager()) {
			MainMenu menu = new MainMenu(terminal);
			menu.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
