package io.github.fnvm.jradio.ui.terminal;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.keymap.BindingReader;
import org.jline.utils.InfoCmp.Capability;

import org.jline.utils.NonBlockingReader;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

public class TerminalManager implements AutoCloseable {

	private static final Logger LOGGER = System.getLogger(TerminalManager.class.getName());
	private final Terminal terminal;
	private final NonBlockingReader reader;
	private final BindingReader bindingReader;

	public TerminalManager() {
		try {
			terminal = TerminalBuilder.builder().system(true).jansi(true).build();

			terminal.enterRawMode();

			reader = terminal.reader();
			bindingReader = new BindingReader(reader);

		} catch (IOException e) {
			LOGGER.log(Level.ERROR, "Failed to initialize terminal", e);
			throw new RuntimeException("Terminal initialization error", e);
		}
	}

	public void clearScreen() {
		terminal.puts(Capability.clear_screen);
		flush();
	}

	public void print(String text) {
		terminal.writer().print(text);
	}

	public void println(String text) {
		terminal.writer().println(text);
	}

	public void flush() {
		terminal.writer().flush();
	}

	public Terminal getTerminal() {
		return terminal;
	}

	public BindingReader getBindingReader() {
		return bindingReader;
	}

	@Override
	public void close() throws IOException {
		terminal.close();
	}
}
