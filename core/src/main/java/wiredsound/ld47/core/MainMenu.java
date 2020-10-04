package wiredsound.ld47.core;

import playn.core.Canvas;
import playn.core.Font;
import playn.core.Graphics;
import playn.core.Platform;
import playn.core.TextFormat;
import playn.core.TextLayout;
import playn.scene.ImageLayer;
import wiredsound.ld47.core.vn.VisualNovel;

public class MainMenu extends UpdatableLayer {
	private static final float FADE_SPEED = 0.0006f;

	private boolean fadingIn = true;

	MainMenu(Platform plat) {
		super(plat);

		setAlpha(0);

		Graphics gfx = plat.graphics();

		Font font = new Font("Courier", Font.Style.PLAIN, 30);
		TextLayout layout = gfx.layoutText("Ludum Dare 47 Entry by WiredSound", new TextFormat(font));

		Canvas textCanvas = gfx.createCanvas(layout.size);
		textCanvas.setFillColor(0xFFCCCCCC);
		textCanvas.fillText(layout, 0, 0);

		ImageLayer imgLayer = new ImageLayer(textCanvas.toTexture());
		addCenterAt(imgLayer, gfx.viewSize.width() / 2, gfx.viewSize.height() / 2);
	}

	@Override
	public UpdatableLayer update(int time) {
		float change = FADE_SPEED * time;

		if(fadingIn) {
			if(alpha < 1) setAlpha(alpha + change);
			else fadingIn = false;
		}
		else { // Fading out:
			if(alpha > 0) setAlpha(alpha - change);
			else {
				setAlpha(1);
				return new VisualNovel(plat, "opening");
			}
		}

		return this;
	}
}
