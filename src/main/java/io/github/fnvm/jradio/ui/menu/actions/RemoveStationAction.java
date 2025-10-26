package io.github.fnvm.jradio.ui.menu.actions;

import java.io.IOException;

import io.github.fnvm.jradio.core.model.RadioStation;
import io.github.fnvm.jradio.core.service.RadioStationsService;
import io.github.fnvm.jradio.ui.terminal.MenuController;
import io.github.fnvm.jradio.ui.terminal.TerminalManager;

public class RemoveStationAction implements MenuAction {
	private Integer currentSelection;

	public RemoveStationAction(Integer currentSelection) {
		this.currentSelection = currentSelection;
	}

	@Override
	public void execute(TerminalManager terminal, RadioStationsService service) throws IOException {
		terminal.clearScreen();

		MenuController yn = new MenuController(terminal, "Remove station?", 0, "yes", "no");

		int choise = yn.show();
		if (choise == 0) {
			RadioStation curr = service.getAllStations().get(currentSelection);
			service.removeStation(curr.getId());
		}
	}

}
