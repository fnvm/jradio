package io.github.fnvm.jradio;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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
        try {
            Logger rootLogger = Logger.getLogger("");

            Handler[] handlers = rootLogger.getHandlers();
            for (Handler handler : handlers) {
                rootLogger.removeHandler(handler);
            }

            FileHandler fileHandler = new FileHandler("app.log", true);
            fileHandler.setFormatter(new SimpleFormatter());

            rootLogger.addHandler(fileHandler);

            rootLogger.setLevel(Level.SEVERE);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
