package io.github.fnvm.jradio.ui.terminal;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;


public class InputReader {
    private static final Logger LOGGER = System.getLogger(InputReader.class.getName());

    public static String readUserInput(TerminalManager terminal, String prompt) {
        LineReader lr = LineReaderBuilder.builder()
                .terminal(terminal.getTerminal())
                .build();
        String r = null;
        try {
            r = lr.readLine(prompt); 
        } catch (UserInterruptException e) {
            LOGGER.log(Level.ERROR, () -> "User Interrupt Exception: " + e.getMessage());
        }
        return r;
    }
}
