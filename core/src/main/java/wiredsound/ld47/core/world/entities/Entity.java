package wiredsound.ld47.core.world.entities;

import playn.core.Surface;
import wiredsound.ld47.core.world.World;

public abstract class Entity {
	public final String name;

	// Position on screen:
	protected float x, y;

	Entity(String name, float x, float y) {
		this.name = name;
		this.x = x;
		this.y = y;
	}

	public abstract void update(World world, int time);

	public abstract void draw(Surface surf, int tileSize);

	public float getX() { return x; }
	public float getY() { return y; }
}
