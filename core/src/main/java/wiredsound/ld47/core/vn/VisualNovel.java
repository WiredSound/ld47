package wiredsound.ld47.core.vn;

import playn.core.Keyboard;
import playn.core.Keyboard.KeyEvent;
import playn.core.Platform;
import react.Slot;
import wiredsound.ld47.core.UpdatableLayer;
import wiredsound.ld47.core.ui.TextBox;

public class VisualNovel extends UpdatableLayer {
	private static final String SCRIPT_DIRECTORY_PATH = "script/";

	private TextBox textBox;

	public VisualNovel(Platform plat, String name) {
		super(plat);
		textBox = new TextBox(plat);
		add(textBox);

		loadScript(name);

		plat.input().keyboardEvents.connect(new Keyboard.KeySlot() {
			@Override
			public void onEmit(KeyEvent e) {
				if(e.down) { textBox.nextPart(); }
			}
		});
	}

	@Override
	public UpdatableLayer update(int time) {
		textBox.update(time);
		return this;
	}

	public void loadScript(String name) {
		System.out.println("Loading script: " + name);

		plat.assets().getText(SCRIPT_DIRECTORY_PATH + name + ".txt").onSuccess(new Slot<String>() {
			@Override
			public void onEmit(String data) {
				for(String line : data.split("\\n")) {
					System.out.println("Loaded line of script: " + line);

					textBox.addPart(line);
				}
			}
		});
	}
}
