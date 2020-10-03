package wiredsound.ld47.core.world;

import playn.core.Surface;
import playn.scene.Layer;
import wiredsound.ld47.core.world.entities.Entity;

class EntityLayer extends Layer {
	private final int tileSize;
	private Entity entity;

	EntityLayer(Entity entity, int tileSize) {
		this.entity = entity;
		this.tileSize = tileSize;
	}

	@Override
	protected void paintImpl(Surface surf) {
		if(entity != null) entity.draw(surf, tileSize);
	}
}
