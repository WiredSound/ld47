package wiredsound.ld47.core;

import playn.core.Clock;
import playn.core.Platform;
import playn.scene.SceneGame;

public class Game extends SceneGame {
	private UpdatableLayer current;

	public Game(Platform plat) {
		// Update every 17ms (approx. 60 FPS):
		super(plat, 17);

		current = new MainMenu(plat);
		//current = new VisualNovel(plat, "opening");
		rootLayer.add(current);
	}

	@Override
	public void update(Clock clock) {
		super.update(clock);

		UpdatableLayer newLayer = current.update(clock.dt);

		if(newLayer != null && newLayer != current) {
			System.out.println("Changing layer: " + newLayer.name());

			rootLayer.remove(current);
			rootLayer.add(newLayer);
			current = newLayer;
		}
	}
}
