package io.github.fnvm.jradio.ui.menu.actions;

import java.io.IOException;
import java.util.Objects;

import io.github.fnvm.jradio.core.model.RadioStation;
import io.github.fnvm.jradio.core.service.RadioStationsService;
import io.github.fnvm.jradio.ui.terminal.InputReader;
import io.github.fnvm.jradio.ui.terminal.TerminalManager;

public class AddStationAction implements MenuAction {

	@Override
	public void execute(TerminalManager terminal, RadioStationsService service) throws IOException {
		terminal.clearScreen();
		System.out.println("=".repeat(16) + " Add new station " + "=".repeat(16));
		System.out.print("Name: ");
		String name = InputReader.readUserInput(terminal);
		if (Objects.isNull(name))
			return;
		if (name.isBlank())
			name = "Unknown";

		System.out.print("URL: ");
		String url = InputReader.readUserInput(terminal);
		if (Objects.isNull(url) || url.isBlank())
			return;
		service.addStation(new RadioStation(name.trim(), url.trim()));
	}

}
