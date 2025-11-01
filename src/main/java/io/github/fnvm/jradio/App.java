package io.github.fnvm.jradio;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.fnvm.jradio.ui.menu.MainMenu;
import io.github.fnvm.jradio.ui.terminal.TerminalManager;

public class App {
	private static final System.Logger LOGGER = System.getLogger(App.class.getName());
	public final static String OS = System.getProperty("os.name").toLowerCase();
	
	public static void main(String[] args) {
		setupLogging();

		TerminalManager terminal = null;
		MainMenu menu = null;

		try {
			terminal = new TerminalManager();
			menu = new MainMenu(terminal);

			final MainMenu finalMenu = menu;

			terminal.setOnShutdown(() -> {
				if (!Objects.isNull(finalMenu)) {
					finalMenu.cleanup();
				}
			});

			menu.run();

			menu.cleanup();
			terminal.clearScreen();
			terminal.close();

		} catch (IOException e) {
			e.printStackTrace();
			if (!Objects.isNull(menu)) {
				menu.cleanup();
			}
			if (!Objects.isNull(terminal)) {
				try {
					terminal.close();
				} catch (IOException ex) {
					LOGGER.log(System.Logger.Level.ERROR, () -> "Error during cleanup: " + e.getMessage());
				}
			}
		}
	}

	public static void setupLogging() {
		Logger rootLogger = Logger.getLogger("");
		rootLogger.setLevel(Level.OFF);
	}
}