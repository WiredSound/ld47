package wiredsound.ld47.core;

import playn.core.Clock;
import playn.core.Platform;
import playn.scene.SceneGame;
import wiredsound.ld47.core.world.World;

public class Game extends SceneGame {
	private World world;

	public Game (Platform plat) {
		// Update every 17ms (approx. 60 FPS):
		super(plat, 17);

		world = new World(plat);
		rootLayer.addCenterAt(world, 0, 0);
	}

	@Override
	public void update(Clock clock) {
		super.update(clock);
		world.update(clock.dt);
	}
}
