package io.github.fnvm.jradio.ui.terminal;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.keymap.BindingReader;
import org.jline.utils.InfoCmp.Capability;
import org.jline.utils.NonBlockingReader;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Objects;

public class TerminalManager implements AutoCloseable {

    private static final Logger LOGGER = System.getLogger(TerminalManager.class.getName());
    private final Terminal terminal;
    private final NonBlockingReader reader;
    private final BindingReader bindingReader;
    private Runnable onShutdown;

    public TerminalManager() {
        try {
            terminal = TerminalBuilder.builder()
                    .system(true)
                    .jansi(true)
                    .build();

            terminal.handle(Terminal.Signal.INT, signal -> {
                LOGGER.log(Level.INFO, "Received SIGINT (Ctrl+C)");
                cleanup();
                System.exit(0);
            });

            terminal.enterRawMode();

            reader = terminal.reader();
            bindingReader = new BindingReader(reader);

        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Failed to initialize terminal", e);
            throw new RuntimeException("Terminal initialization error", e);
        }
    }

    public void setOnShutdown(Runnable onShutdown) {
        this.onShutdown = onShutdown;
    }

    private void cleanup() {
        try {
            if (!Objects.isNull(onShutdown)) {
                onShutdown.run();
            }
            
            clearScreen();
            terminal.close();
        } catch (Exception e) {
            LOGGER.log(Level.ERROR, "Cleanup error", e);
        }
    }

    public void clearScreen() {
        try {
            terminal.puts(Capability.clear_screen);
            flush();
        } catch (Exception e) {
        	LOGGER.log(Level.ERROR,() -> "Clear screen error");
        }
    }

    public void cursorHome() {
        terminal.puts(Capability.cursor_address, 0, 0);
        flush();
    }

    public void cursorDown(int lines) {
        for (int i = 0; i < lines; i++) {
            terminal.puts(Capability.cursor_down);
        }
        flush();
    }

    public void eraseLine() {
        terminal.puts(Capability.clr_eol);
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
        cleanup();
    }
}