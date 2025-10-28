package io.github.fnvm.jradio;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.fnvm.jradio.ui.menu.MainMenu;
import io.github.fnvm.jradio.ui.terminal.TerminalManager;

public class App {
	public static void main(String[] args) {
		setupLogging();
		try (TerminalManager terminal = new TerminalManager()) {
			MainMenu menu = new MainMenu(terminal);
			menu.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setupLogging() {
		Logger rootLogger = Logger.getLogger("");
		rootLogger.setLevel(Level.OFF);
	}
}
