package io.github.fnvm.jradio.ui.terminal;

import org.jline.keymap.KeyMap;
import org.jline.utils.InfoCmp.Capability;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class MenuController {

	private final TerminalManager terminal;
	private final MenuRenderer renderer;
	private final Map<String, Consumer<Integer>> hotkeys;
	private final String[] menuItems;
	private final KeyMap<String> keyMap;

	private int currentSelection;

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

		this.keyMap = buildKeyMap();
	}

	public int show() {
		renderer.render(currentSelection);

		while (true) {
			String key = terminal.getBindingReader().readBinding(keyMap, null, true);

			if (Objects.isNull(key))
				continue;

			switch (key) {
			case "up" -> moveUp();
			case "down" -> moveDown();
			case "enter" -> {
				return currentSelection + 10_000;
			}
			default -> {
				boolean handled = handleHotkey(key);
				if (handled) {
					return currentSelection;
				}
			}
			}
		}
	}

	private void moveUp() {
		if (currentSelection > 0) {
			currentSelection--;
			renderer.render(currentSelection);
		}
	}

	private void moveDown() {
		if (currentSelection < menuItems.length - 1) {
			currentSelection++;
			renderer.render(currentSelection);
		}
	}

	private boolean handleHotkey(String key) {
		if (!key.isBlank()) {
		
			Consumer<Integer> action = hotkeys.get(key.toLowerCase());
			if (action != null) {
				action.accept(currentSelection);
				return true;
			}
		}
		return false;
	}

	private KeyMap<String> buildKeyMap() {
		KeyMap<String> map = new KeyMap<>();

		map.bind("up", KeyMap.key(terminal.getTerminal(), Capability.key_up));
		map.bind("down", KeyMap.key(terminal.getTerminal(), Capability.key_down));

		map.bind("up", "\033[A"); // ESC [ A
		map.bind("down", "\033[B"); // ESC [ B
		map.bind("up", "\033OA"); // ESC O A
		map.bind("down", "\033OB"); // ESC O B

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
