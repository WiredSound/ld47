package wiredsound.ld47.core.vn;

import java.util.ArrayList;

import playn.core.Color;
import playn.core.Keyboard;
import playn.core.Keyboard.KeyEvent;
import playn.core.Platform;
import react.Slot;
import wiredsound.ld47.core.UpdatableLayer;
import wiredsound.ld47.core.ui.TextBox;

public class VisualNovel extends UpdatableLayer {
	private static final String SCRIPT_DIRECTORY_PATH = "script/";
	private static final String BACKGROUNDS_DIRECTORY_PATH = "images/backgrounds/";

	private TextBox textBox;

	private ArrayList<Background> backgrounds = new ArrayList<Background>();

	public VisualNovel(Platform plat, String scriptName) {
		super(plat);

		textBox = new TextBox(plat);
		add(textBox);

		loadScript(scriptName);

		plat.input().keyboardEvents.connect(new Keyboard.KeySlot() {
			@Override
			public void onEmit(KeyEvent e) {
				if(e.down) { textBox.nextPart(); }
			}
		});
	}

	@Override
	public UpdatableLayer update(int time) {
		for(Background bg : backgrounds) bg.update(time);
		if(textBox != null) textBox.update(time);
		return this;
	}

	public void loadScript(String name) {
		String path = SCRIPT_DIRECTORY_PATH + name + ".txt";

		System.out.println("Loading script: " + path);

		plat.assets().getText(path).onSuccess(new Slot<String>() {
			@Override
			public void onEmit(String data) {
				for(String line : data.split("\\n")) {
					if(line.startsWith("background:")) {
						String[] backgroundInfo = line.split(":")[1].split(",");

						String backgroundName = backgroundInfo[0].trim();
						String path = BACKGROUNDS_DIRECTORY_PATH + backgroundName + ".png";
						int startColour = Color.withAlpha(Integer.parseInt(backgroundInfo[1].trim(), 16), 255);
						int endColour = Color.withAlpha(Integer.parseInt(backgroundInfo[2].trim(), 16), 255);
						float speed = Float.parseFloat(backgroundInfo[3].trim());

						System.out.println("Using background: " + path);
						System.out.println(Color.red(startColour) + ", " + Color.red(endColour));

						Background bg = new Background(plat, path, startColour, endColour, speed);
						backgrounds.add(bg);
						add(bg);
					}
					else {
						System.out.println("Loaded line of script: " + line);

						textBox.addPart(line);
					}
				}

				if(textBox != null) remove(textBox);
				add(textBox);
			}
		});
	}
}
