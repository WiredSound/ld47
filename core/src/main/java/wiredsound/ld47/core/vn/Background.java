package wiredsound.ld47.core.vn;

import playn.core.Color;
import playn.core.Image;
import playn.core.Platform;
import playn.scene.ImageLayer;
import react.Slot;
import wiredsound.ld47.core.UpdatableLayer;

class Background extends UpdatableLayer {
	private ImageLayer imgLayer;

	private final float speed;

	private final int startR, startG, startB;
	private final int endR, endG, endB;

	float r, g, b;

	private boolean increasing = true;

	protected Background(Platform plat, String path, final int startColour, final int endColour, float speed) {
		super(plat);

		this.speed = speed;

		r = startR = Color.red(startColour);
		g = startG = Color.green(startColour);
		b = startB = Color.blue(startColour);

		endR = Color.red(endColour);
		endG = Color.green(endColour);
		endB = Color.blue(endColour);

		plat.assets().getImage(path).state.onSuccess(new Slot<Image>() {
			@Override
			public void onEmit(Image img) {
				img.setConfig(TEXTURE_CONFIG);
				imgLayer = new ImageLayer(img.texture());
				imgLayer.setScale(4).setTint(startColour);
				add(imgLayer);
			}});
	}

	@Override
	public UpdatableLayer update(int time) {
		if(imgLayer != null) {
			final float changeR = (endR - startR) * time * speed,
					 	changeG = (endG - startG) * time * speed,
					 	changeB = (endB - startB) * time * speed;

			if(increasing) {
				if(r < endR || g < endG || b < endB) {
					r += changeR;
					g += changeG;
					b += changeB;
				}
				else increasing = false;
			}
			else {
				if(r > startR || g > startG || b > startB) {
					r -= changeR;
					g -= changeG;
					b -= changeB;
				}
				else increasing = true;
			}

			imgLayer.setTint(
				Color.argb(255, Math.min(Math.round(r), 255), Math.min(Math.round(g), 255), Math.min(Math.round(b), 255))
			);
		}

		return null;
	}
}
