package io.github.fnvm.jradio.ui.menu;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import io.github.fnvm.jradio.App;
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
	private int pageSelection;
	private MenuController stationsMenu;
	private String heartSymbol;

	private static final Logger LOGGER = System.getLogger(StationsMenu.class.getName());

	public StationsMenu(Player player) {
		this.stationService = new RadioStationsService();
		this.player = player;
		selection = 0;
		pageSelection = 1;
		heartSymbol = App.OS.contains("windows") ? " ♥" : " ⭐";
	}

	public void showStationsMenu(TerminalManager terminal) throws IOException {
		while (true) {
			List<RadioStation> stations = stationService.getAllStations();

			String[] stationNames = stations.stream().map((station) -> {
				return station.getName() + (station.isFavorite() ? heartSymbol : "");
			}).toArray(String[]::new);

			Map<String, Consumer<Integer>> options = new HashMap<>();

			options.put("a", (currentSelection) -> runAction(new AddStationAction(), terminal));
			options.put("d", (currentSelection) -> runAction(new RemoveStationAction(currentSelection), terminal));
			options.put("e", (currentSelection) -> runAction(new EditStationAction(currentSelection), terminal));

			options.put("p", (currentSelection) -> {
				if (currentSelection < stations.size()) {
					RadioStation station = stations.get(currentSelection);
					if (player.isPlaying() && station.equals(player.getCurrentStation())) {
						player.stop();
					} else {
						player.stop();
						player.play(station);
					}
				}
			});
			options.put("b", (currentSelection) -> {
				throw new ExitMenuException();
			});

			String[] inactiveItems = new String[] { "Play / Pause (P)", "Add Station (A)", "Remove Station (D)",
					"Edit Station (E)", "", "← Back (B)", "" };

			stationsMenu = new MenuController(terminal, "Stations List", selection, pageSelection, inactiveItems,
					options, stationNames);
			stationsMenu.setPlayer(player);

			try {
				int[] groupSel = stationsMenu.show();
				pageSelection = groupSel[1];
				int globalInd = groupSel[0];

				if (globalInd >= 10_000) {
					globalInd -= 10_000;
					new EditStationAction(globalInd).execute(terminal, stationService);
				}
				selection = globalInd % MenuController.ITEMS_PER_PAGE;
			} catch (ExitMenuException ex) {
				break;
			}
		}
	}

	private void runAction(MenuAction action, TerminalManager terminal) {
		try {
			stationsMenu.setSuspendMetadataUpdates(true);
			action.execute(terminal, stationService);
			stationsMenu.setSuspendMetadataUpdates(false);
		} catch (IOException e) {
			LOGGER.log(Level.ERROR, () -> "Failed action " + action.getClass().getName() + " " + e.getMessage());
		}
	}
}
