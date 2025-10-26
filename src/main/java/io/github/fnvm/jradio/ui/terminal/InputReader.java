package io.github.fnvm.jradio.ui.terminal;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

public class InputReader {
	public static String readUserInput(TerminalManager terminal) {
		LineReader lr = LineReaderBuilder.builder().terminal(terminal.getTerminal()).build();
		return lr.readLine(" ");
	}
}
