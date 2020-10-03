package wiredsound.ld47.core;

import playn.core.GL20;
import playn.core.Platform;
import playn.core.Texture.Config;
import playn.scene.GroupLayer;

public abstract class UpdatableLayer extends GroupLayer {
	// For ensuring pixel art isn't blurry when scaled:
	public static final Config TEXTURE_CONFIG = new Config(true, false, false, GL20.GL_NEAREST, GL20.GL_NEAREST, false);

	protected final Platform plat;

	protected UpdatableLayer(Platform plat) { this.plat = plat; }

	public abstract UpdatableLayer update(int time);
}
