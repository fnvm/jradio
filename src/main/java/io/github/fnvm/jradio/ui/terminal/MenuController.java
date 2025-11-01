package io.github.fnvm.jradio.ui.terminal;

import org.jline.keymap.KeyMap;
import org.jline.utils.InfoCmp.Capability;

import io.github.fnvm.jradio.player.Player;

import java.util.Map;
import java.util.function.Consumer;

public class MenuController {

	private final TerminalManager terminal;
	private final MenuRenderer renderer;
	private final Map<String, Consumer<Integer>> hotkeys;
	private final String[] menuItems;
	private final KeyMap<String> keyMap;
	private Player player;
	private volatile boolean suspendMetadataUpdates = false;

	private int currentPageSelection;
	private int currentSelection;
	private volatile boolean running = false;
	private Thread metadataUpdateThread;
	
	private static final int ITEMS_PER_PAGE = 15;

	public MenuController(TerminalManager terminal, String title, int currentSelection, String... menuItems) {
		this(terminal, title, currentSelection, new String[] {}, Map.of(), menuItems);
	}

	public MenuController(TerminalManager terminal, String title, int currentSelection, String[] inactiveItems,
			Map<String, Consumer<Integer>> hotkeys, String... menuItems) {

		this.terminal = terminal;
		this.renderer = new MenuRenderer(terminal, title, inactiveItems, menuItems);
		this.menuItems = menuItems;
		this.currentSelection = currentSelection;
		this.hotkeys = hotkeys;
		this.currentPageSelection = 1;
		this.keyMap = buildKeyMap();
	}

	public void setPlayer(Player player) {
		this.player = player;
		this.renderer.setPlayer(player);
	}

	public int show() {
		startMetadataUpdater();
		renderer.render(currentSelection, currentPageSelection);

		try {
			while (true) {
				String key = terminal.getBindingReader().readBinding(keyMap, null, true);

				if (key == null)
					continue;

				switch (key) {
					case "up" -> {
						moveUp();
						renderer.render(currentSelection, currentPageSelection);
					}
					case "down" -> {
						moveDown();
						renderer.render(currentSelection, currentPageSelection);
					}
					case "right" -> {
						moveRight();
						renderer.render(currentSelection, currentPageSelection);
					}
					case "left" -> {
						moveLeft();
						renderer.render(currentSelection, currentPageSelection);
					}
					case "enter" -> {
						int globalIndex = getGlobalIndex();
						return globalIndex + 10_000;
					}
					default -> {
						boolean handled = handleHotkey(key);
						if (handled) {
							renderer.render(currentSelection, currentPageSelection);
							int globalIndex = getGlobalIndex();
							return globalIndex;
						}
					}
				}
			}
		} finally {
			stopMetadataUpdater();
		}
	}

	private void moveUp() {
		if (currentSelection > 0) {
			currentSelection--;
		}
	}

	private void moveDown() {
		int itemsOnCurrentPage = getItemsOnCurrentPage();
		if (currentSelection < itemsOnCurrentPage - 1) {
			currentSelection++;
		}
	}
	
	private void moveRight() {
		int totalPages = getTotalPages();
		if (currentPageSelection < totalPages) {
			currentPageSelection++;
			currentSelection = 0;
		}
	}
	
	private void moveLeft() {
		if (currentPageSelection > 1) {
			currentPageSelection--;
			currentSelection = 0;
		}
	}
	
	private int getTotalPages() {
		return (int) Math.ceil((double) menuItems.length / ITEMS_PER_PAGE);
	}
	
	private int getItemsOnCurrentPage() {
		int startIndex = (currentPageSelection - 1) * ITEMS_PER_PAGE;
		int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, menuItems.length);
		return endIndex - startIndex;
	}
	
	private int getGlobalIndex() {
		return (currentPageSelection - 1) * ITEMS_PER_PAGE + currentSelection;
	}

	public void setSuspendMetadataUpdates(boolean suspend) {
		this.suspendMetadataUpdates = suspend;
	}

	private boolean handleHotkey(String key) {
		if (!key.isBlank()) {
			Consumer<Integer> action = hotkeys.get(key.toLowerCase());
			if (action != null) {
				action.accept(getGlobalIndex());
				return true;
			}
		}
		return false;
	}

	private void startMetadataUpdater() {
		if (player == null)
			return;

		running = true;

		metadataUpdateThread = new Thread(() -> {
			while (running) {
				try {
					Thread.sleep(100);
					if (!suspendMetadataUpdates) {
						renderer.refreshMetadataLine();
					}
				} catch (InterruptedException ignored) {
					break;
				}
			}
		});

		metadataUpdateThread.setDaemon(true);
		metadataUpdateThread.start();
	}

	private void stopMetadataUpdater() {
		running = false;
		if (metadataUpdateThread != null) {
			metadataUpdateThread.interrupt();
		}
	}

	private KeyMap<String> buildKeyMap() {
		KeyMap<String> map = new KeyMap<>();

		map.bind("up", KeyMap.key(terminal.getTerminal(), Capability.key_up));
		map.bind("down", KeyMap.key(terminal.getTerminal(), Capability.key_down));
		map.bind("left", KeyMap.key(terminal.getTerminal(), Capability.key_left));
		map.bind("right", KeyMap.key(terminal.getTerminal(), Capability.key_right));

		map.bind("up", "\033[A");
		map.bind("down", "\033[B");
		map.bind("left", "\033[D");
		map.bind("right", "\033[C");

		map.bind("up", "\033OA");
		map.bind("down", "\033OB");
		map.bind("left", "\033OD");
		map.bind("right", "\033OC");

		map.bind("enter", "\r");
		map.bind("enter", "\n");

		hotkeys.keySet().forEach(c -> {
			String lower = c.toLowerCase();
			String upper = c.toUpperCase();
			map.bind(lower, lower);
			map.bind(upper, lower);
		});

		map.setUnicode("default");
		return map;
	}
}