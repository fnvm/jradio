package io.github.fnvm.jradio.ui.menu.actions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import io.github.fnvm.jradio.core.model.RadioStation;
import io.github.fnvm.jradio.core.service.RadioStationsService;
import io.github.fnvm.jradio.ui.terminal.InputReader;
import io.github.fnvm.jradio.ui.terminal.MenuController;
import io.github.fnvm.jradio.ui.terminal.TerminalManager;

public final class EditStationAction implements MenuAction {
	private final Integer currentStationSelection;
	private int currentSelection;

	public EditStationAction(Integer currentSelection) {
		this.currentStationSelection = currentSelection;
		currentSelection = 0;
	}

	@Override
	public void execute(TerminalManager terminal, RadioStationsService service) throws IOException {
		while (true) {
			terminal.clearScreen();
			if (currentStationSelection >= service.getAllStations().size()) {
				return;
			}
			RadioStation currentStation = service.getAllStations().get(currentStationSelection);
			String[] inactiveItems = new String[5];
			inactiveItems[0] = currentStation.getName();
			inactiveItems[1] = currentStation.getUrl();
			inactiveItems[2] = currentStation.getNote();
			if (currentStation.isFavorite()) {
				inactiveItems[3] = "♡";
			} else {
				inactiveItems[3] = "No";
			}
			inactiveItems[4] = System.lineSeparator() + "↩  Back (B)";

			final boolean[] back = { false };
			Map<String, Consumer<Integer>> hotkeys = new HashMap<>();
			hotkeys.put("b", (c) -> back[0] = true);

			MenuController options = new MenuController(terminal, "Edit Station", currentSelection, inactiveItems,
					hotkeys, "Name", "URL", "Note", "Toggle favorites");
			currentSelection = options.show() - 10_000;

			if (back[0]) {
				return;
			}
			switch (currentSelection) {
			case 0 -> editName(currentStation, service, terminal);
			case 1 -> editUrl(currentStation, service, terminal);
			case 2 -> editNote(currentStation, service, terminal);
			case 3 -> toggleFaorites(currentStation, service);
			}

		}

	}

	private RadioStation editName(RadioStation currentStation, RadioStationsService service, TerminalManager terminal)
			throws IOException {
		terminal.clearScreen();
		String name = InputReader.readUserInput(terminal, "New name: ");
		if (Objects.isNull(name))
			return currentStation;

		currentStation.setName(name);
		service.saveData();
		return currentStation;
	}

	private RadioStation editUrl(RadioStation currentStation, RadioStationsService service, TerminalManager terminal)
			throws IOException {
		terminal.clearScreen();
		String url = InputReader.readUserInput(terminal, "New URL: ");
		if (Objects.isNull(url))
			return currentStation;

		currentStation.setUrl(url);
		service.saveData();
		return currentStation;
	}

	private RadioStation editNote(RadioStation currentStation, RadioStationsService service, TerminalManager terminal)
			throws IOException {
		terminal.clearScreen();
		String note = InputReader.readUserInput(terminal, "Note: ");
		if (Objects.isNull(note))
			return currentStation;

		currentStation.setNote(note);
		service.saveData();
		return currentStation;

	}

	private void toggleFaorites(RadioStation currentStation, RadioStationsService service) throws IOException {
		service.toggleFavorite(currentStation.getId());
		service.saveData();
	}

}
