package wiredsound.ld47.core.ui;

import java.util.ArrayList;

import playn.core.Canvas;
import playn.core.Font;
import playn.core.Graphics;
import playn.core.Platform;
import playn.core.TextFormat;
import playn.core.TextLayout;
import playn.core.TextWrap;
import playn.scene.CanvasLayer;
import playn.scene.ImageLayer;
import pythagoras.f.IDimension;
import wiredsound.ld47.core.UpdatableLayer;

public class TextBox extends UpdatableLayer {
	private static final int TEXT_COLOUR = 0xFFCCCCCC;
	private static final int BACKGROUND_COLOUR = 0xAA000000;

	private TextFormat format;
	private TextWrap wrapping;

	private ImageLayer textLayer;
	private CanvasLayer textBoxLayer;

	private final float textCentreHeight;

	private float textBoxWidth = 0;

	private boolean fadeOut = false;

	private ArrayList<TextLayout[]> parts = new ArrayList<TextLayout[]>();

	public TextBox(Platform plat) {
		super(plat);

		IDimension size = plat.graphics().viewSize;

		textCentreHeight = size.height() * 0.9f;

		wrapping = new TextWrap(size.width() * 0.9f);
		format = new TextFormat(new Font("Courier", Font.Style.PLAIN, 18));

		textBoxLayer = new CanvasLayer(plat.graphics(), size.width() * 0.92f, size.height() * 0.1f);
		addCenterAt(textBoxLayer, size.width() / 2, textCentreHeight);
	}

	@Override
	public UpdatableLayer update(int time) {
		if(textLayer == null) {
			if(textBoxWidth < textBoxLayer.width() - 1) {
				textBoxWidth += time * 0.8f;
				if(textBoxWidth >= textBoxLayer.width()) textBoxWidth = textBoxLayer.width() - 1;

				drawTextBox();
			}
			else { nextPart(); }
		}

		if(fadeOut) {
			textBoxWidth -= time * 0.8;

			if(textBoxWidth > 0) drawTextBox();
			else textBoxLayer.setVisible(false);
		}

		return null;
	}

	public void nextPart() {
		Graphics gfx = plat.graphics();

		if(parts.isEmpty()) {
			System.out.println("All script parts have already been displayed");

			textLayer.setVisible(false);
			fadeOut = true;
		}
		else {
			TextLayout[] lines = parts.get(0);

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

			parts.remove(0);
		}
	}

	public void addPart(String line) {
		TextLayout[] layouts = plat.graphics().layoutText(line, format, wrapping);
		parts.add(layouts);
	}

	private void drawTextBox() {
		float x = (textBoxLayer.width() - textBoxWidth) / 2;
		float height = textBoxLayer.height() - 1;

		textBoxLayer.begin().clear().setStrokeColor(TEXT_COLOUR).setFillColor(BACKGROUND_COLOUR)
					.fillRect(x, 0, textBoxWidth, height)
					.strokeRect(x, 0, textBoxWidth, height);

		textBoxLayer.end();
	}
}
