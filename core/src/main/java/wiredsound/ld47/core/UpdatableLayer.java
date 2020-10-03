package wiredsound.ld47.core;

import playn.core.Platform;
import playn.scene.GroupLayer;

public abstract class UpdatableLayer extends GroupLayer {
	protected final Platform plat;

	protected UpdatableLayer(Platform plat) { this.plat = plat; }

	public abstract UpdatableLayer update(int time);
}
