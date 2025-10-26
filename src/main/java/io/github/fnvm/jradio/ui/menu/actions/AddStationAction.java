package io.github.fnvm.jradio.ui.menu.actions;

import java.io.IOException;

import io.github.fnvm.jradio.core.model.RadioStation;
import io.github.fnvm.jradio.core.service.RadioStationsService;
import io.github.fnvm.jradio.ui.terminal.InputReader;
import io.github.fnvm.jradio.ui.terminal.TerminalManager;

public class AddStationAction implements MenuAction {

	@Override
	public void execute(TerminalManager terminal, RadioStationsService service) throws IOException {
		terminal.clearScreen();
		System.out.println("=== Add new station ===");
		System.out.print("Name: ");
		String name = InputReader.readUserInput(terminal);
		if (name == null)
			return;

		System.out.print("URL: ");
		String url = InputReader.readUserInput(terminal);
		if (url == null)
			return;
		service.addStation(new RadioStation(name.trim(), url.trim()));
	}

}
