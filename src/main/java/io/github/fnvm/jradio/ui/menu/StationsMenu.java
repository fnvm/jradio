package io.github.fnvm.jradio.ui.menu;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import io.github.fnvm.jradio.core.model.RadioStation;
import io.github.fnvm.jradio.core.service.RadioStationsService;
import io.github.fnvm.jradio.player.Player;
import io.github.fnvm.jradio.ui.menu.actions.*;
import io.github.fnvm.jradio.ui.terminal.MenuController;
import io.github.fnvm.jradio.ui.terminal.TerminalManager;

public class StationsMenu {

	private final RadioStationsService stationService;
	private final Player player;
	private int selection;
	
	private static final Logger LOGGER = System.getLogger(StationsMenu.class.getName());

	public StationsMenu() {
		this.stationService = new RadioStationsService();
		player = new Player();
		selection = 0;
	}

	public void showStationsMenu(TerminalManager terminal) throws IOException {
		while (true) {
			List<RadioStation> stations = stationService.getAllStations();
			String[] stationNames = stations.stream().map(RadioStation::getName).toArray(String[]::new);

			Map<Character, Consumer<Integer>> options = new HashMap<>();

			options.put('a', (currentSelection) -> runAction(new AddStationAction(), terminal));
			options.put('d', (currentSelection) -> runAction(new RemoveStationAction(currentSelection), terminal));
			options.put('e', (currentSelection) -> runAction(new EditStationAction(currentSelection), terminal));

			options.put('p', (currentSelection) -> {
				RadioStation station = stations.get(currentSelection);
				if (player.isPlaying() && station.equals(player.getCurrentStation())) {
					player.stop();
				} else {
					player.stop();
					player.play(station);
				}
			});
			options.put('b', (currentSelection) -> {
				throw new ExitMenuException();
			});

			String[] inactiveItems = new String[] { "Play / Pause (P)", "Add Station (A)", "Remove Station (D)",
					"Edit Station (E)", "", "↩  Back (B)", "",
					(player.isPlaying()) ? "Playing: " + player.getCurrentStation().getName() : "" };

			MenuController stationsMenu = new MenuController(terminal, "Stations List", selection, inactiveItems,
					options, stationNames);

			try {
				selection = stationsMenu.show();
				if (selection >= 10_000) {
					selection -= 10_000;
					new EditStationAction(selection).execute(terminal, stationService);
				}
			} catch (ExitMenuException ex) {
				break;
			}
		}
	}

	private void runAction(MenuAction action, TerminalManager terminal) {
		try {
			action.execute(terminal, stationService);
		} catch (IOException e) {
			LOGGER.log(Level.ERROR, () -> "Failed action " + action.getClass().getName() + " " + e.getMessage());
		}
	}
}
