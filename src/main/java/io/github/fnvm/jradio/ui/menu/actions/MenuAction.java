package io.github.fnvm.jradio.ui.menu.actions;

import java.io.IOException;

import io.github.fnvm.jradio.core.service.RadioStationsService;
import io.github.fnvm.jradio.ui.terminal.TerminalManager;

@FunctionalInterface
public interface MenuAction {
	void execute(TerminalManager terminal, RadioStationsService service) throws IOException;
}
