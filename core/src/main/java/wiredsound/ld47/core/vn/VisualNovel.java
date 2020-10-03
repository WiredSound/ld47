package wiredsound.ld47.core.vn;

import java.util.ArrayList;

import playn.core.Canvas;
import playn.core.Font;
import playn.core.Graphics;
import playn.core.Keyboard;
import playn.core.Keyboard.KeyEvent;
import playn.core.Platform;
import playn.core.TextFormat;
import playn.core.TextLayout;
import playn.core.TextWrap;
import playn.scene.CanvasLayer;
import playn.scene.ImageLayer;
import pythagoras.f.IDimension;
import react.Slot;
import wiredsound.ld47.core.UpdatableLayer;

public class VisualNovel extends UpdatableLayer {
	private static final String SCRIPT_DIRECTORY_PATH = "script/";
	private static final int TEXT_COLOUR = 0xFFCCCCCC;

	private TextFormat format;
	private TextWrap wrapping;

	private ArrayList<TextLayout[]> scriptParts = new ArrayList<TextLayout[]>();

	private ImageLayer textLayer;
	private CanvasLayer textBoxLayer;

	private final float textCentreHeight;

	private float textBoxWidth = 0;

	private boolean fadeOut = false;

	public VisualNovel(Platform plat, String name) {
		super(plat);

		IDimension size = plat.graphics().viewSize;

		textCentreHeight = size.height() * 0.9f;

		wrapping = new TextWrap(size.width() * 0.9f);
		format = new TextFormat(new Font("Courier", Font.Style.PLAIN, 18));

		textBoxLayer = new CanvasLayer(plat.graphics(), size.width() * 0.92f, size.height() * 0.1f);
		addCenterAt(textBoxLayer, size.width() / 2, textCentreHeight);

		loadScript(name);

		plat.input().keyboardEvents.connect(new Keyboard.KeySlot() {
			@Override
			public void onEmit(KeyEvent e) {
				if(textLayer != null && e.down) { nextScriptPart(); }
			}
		});
	}

	@Override
	public UpdatableLayer update(int time) {
		if(textLayer == null) {
			if(textBoxWidth < textBoxLayer.width() - 1) {
				textBoxWidth += time * 0.8f;
				if(textBoxWidth >= textBoxLayer.width()) textBoxWidth = textBoxLayer.width() - 1;

				drawTextBox();
			}
			else { nextScriptPart(); }
		}

		if(fadeOut) {
			textBoxWidth -= time * 0.8;

			if(textBoxWidth > 0) drawTextBox();
			else textBoxLayer.setVisible(false);
		}

		return this;
	}

	private void nextScriptPart() {
		Graphics gfx = plat.graphics();

		if(scriptParts.isEmpty()) {
			System.out.println("All script parts have already been displayed");

			textLayer.setVisible(false);
			fadeOut = true;
		}
		else {
			TextLayout[] lines = scriptParts.get(0);

			System.out.println("Transitioning to next script part");

			if(lines.length > 0) {
				Canvas canvas = gfx.createCanvas(lines[0].size.width(), lines[0].size.height() * lines.length);
				canvas.setFillColor(TEXT_COLOUR);

				float y = 0;

				for(TextLayout line : lines) {
					canvas.fillText(line, 0, y);
					y += line.size.height();
				}

				if(textLayer != null) remove(textLayer);

				textLayer = new ImageLayer(canvas.toTexture(TEXTURE_CONFIG));
				addCenterAt(textLayer, gfx.viewSize.width() / 2, textCentreHeight);
			}

			scriptParts.remove(0);
		}
	}

	private void loadScript(String name) {
		System.out.println("Loading script: " + name);

		plat.assets().getText(SCRIPT_DIRECTORY_PATH + name + ".txt").onSuccess(new Slot<String>() {
			@Override
			public void onEmit(String data) {
				for(String line : data.split("\\n")) {
					System.out.println("Loaded line of script: " + line);

					TextLayout[] layouts = plat.graphics().layoutText(line, format, wrapping);
					scriptParts.add(layouts);
				}
			}
		});
	}

	private void drawTextBox() {
		textBoxLayer.begin().clear().setStrokeColor(TEXT_COLOUR).strokeRect((textBoxLayer.width() - textBoxWidth) / 2, 0, textBoxWidth, textBoxLayer.height() - 1);
		textBoxLayer.end();
	}
}
