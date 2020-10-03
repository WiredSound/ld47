package wiredsound.ld47.core.world;

import java.util.HashMap;

import playn.core.Assets;
import playn.core.GL20;
import playn.core.Image;
import playn.core.Keyboard;
import playn.core.Keyboard.KeyEvent;
import playn.core.Platform;
import playn.core.Surface;
import playn.core.Texture;
import playn.core.Texture.Config;
import playn.core.Tile;
import playn.scene.Layer;
import react.Slot;
import wiredsound.ld47.core.world.entities.Direction;
import wiredsound.ld47.core.world.entities.Human;

public class World extends Layer {
	// Size in pixels of each tile:
	public static final int TILE_SIZE = 16;

	public static final int LAYER_WIDTH = 16;
	public static final int LAYER_HEIGHT = 12;

	public static final String TILESET_PATH = "images/tileset.png";
	public static final String MAP_DIRECTORY_PATH = "maps/";

	private Human player;

	// Layers of the currently loaded world area/map:
	private WorldLayer topLayer, blockingLayer, bottomLayer;

	// Map world map tiles to tileset texture tiles:
	private HashMap<WorldTile, Tile> worldTileTextures = new HashMap<WorldTile, Tile>();

	public World(final Platform plat) {
		super.setScale(4);
		System.out.println("(" + super.tx() + ", " + super.ty() + ")");

		plat.assets().getImage(TILESET_PATH).state.onSuccess(new Slot<Image>() {
			@Override
			public void onEmit(Image image) {
				// Ensure pixel art isn't blurry when scaled:
				image.setConfig(new Config(true, false, false, GL20.GL_NEAREST, GL20.GL_NEAREST, false));

				Texture tileset = image.texture();
				System.out.println("Loaded tilset texture successfully");

				player = new Human("Player", 80, 80,
					0xFFFFDBAC, 120, 0.06f,
					tileset.tile(0, 0, TILE_SIZE, TILE_SIZE),
					new Tile[] {
						tileset.tile(TILE_SIZE, 0, TILE_SIZE, TILE_SIZE),
						tileset.tile(2 * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE)
					},
					tileset.tile(0, TILE_SIZE, TILE_SIZE, TILE_SIZE),
					new Tile[] {
						tileset.tile(TILE_SIZE, TILE_SIZE, TILE_SIZE, TILE_SIZE),
						tileset.tile(2 * TILE_SIZE, TILE_SIZE, TILE_SIZE, TILE_SIZE)
					},
					tileset.tile(4 * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE),
					new Tile[] {
						tileset.tile(3 * TILE_SIZE, TILE_SIZE, TILE_SIZE, TILE_SIZE),
						tileset.tile(4 * TILE_SIZE, TILE_SIZE, TILE_SIZE, TILE_SIZE)
					}
				);
				System.out.println("Created player character");

				worldTileTextures.put(WorldTile.WOOD_FLOORING, tileset.tile(2 * TILE_SIZE, 2 * TILE_SIZE, TILE_SIZE, TILE_SIZE));
				worldTileTextures.put(WorldTile.BRICKS, tileset.tile(2 * TILE_SIZE, 4 * TILE_SIZE, TILE_SIZE, TILE_SIZE));
				worldTileTextures.put(WorldTile.CRACKED_BRICKS, tileset.tile(3 * TILE_SIZE, 2 * TILE_SIZE, TILE_SIZE, TILE_SIZE));
				worldTileTextures.put(WorldTile.FANCY_BRICKS, tileset.tile(2 * TILE_SIZE, 3 * TILE_SIZE, TILE_SIZE, TILE_SIZE));
				worldTileTextures.put(WorldTile.VENT_BRICKS, tileset.tile(0, 3 * TILE_SIZE, TILE_SIZE, TILE_SIZE));
				worldTileTextures.put(WorldTile.BRIDGE_PARAPET, tileset.tile(4 * TILE_SIZE, 2 * TILE_SIZE, TILE_SIZE, TILE_SIZE));
			}
		});

		plat.input().keyboardEvents.connect(new Keyboard.KeySlot() {
			@Override
			public void onEmit(KeyEvent event) {
				if(player != null) {
					Direction d = null;
					switch(event.key) {
					case LEFT: case A: d = Direction.LEFT; break;
					case RIGHT: case D: d = Direction.RIGHT; break;
					case UP: case W: d = Direction.UP; break;
					case DOWN: case S: d = Direction.DOWN; break;
					default: break;
					}

					if(d != null) {
						if(event.down) {
							player.move(d);
						}
						else { // Key released:
							player.halt(d);
						}
					}
				}
			}
		});

		loadMap(plat.assets(), "bridge");
	}

	public boolean allowMovementTo(float x, float y, Direction d) {
		float yFraction = TILE_SIZE / 2.5f;
		float xFraction = TILE_SIZE / 4.5f;

		switch(d) {
		case RIGHT:
			return blockingLayer.getTileAt(x + TILE_SIZE - xFraction, y + yFraction, TILE_SIZE) == WorldTile.NOTHING &&
				   blockingLayer.getTileAt(x + TILE_SIZE - xFraction, y + TILE_SIZE, TILE_SIZE) == WorldTile.NOTHING;

		case LEFT:
			return blockingLayer.getTileAt(x + xFraction, y + yFraction, TILE_SIZE) == WorldTile.NOTHING &&
				   blockingLayer.getTileAt(x + xFraction, y + TILE_SIZE, TILE_SIZE) == WorldTile.NOTHING;

		case DOWN:
			return blockingLayer.getTileAt(x + xFraction, y + TILE_SIZE, TILE_SIZE) == WorldTile.NOTHING &&
				   blockingLayer.getTileAt(x + TILE_SIZE - xFraction, y + TILE_SIZE, TILE_SIZE) == WorldTile.NOTHING;

		case UP:
			return blockingLayer.getTileAt(x + xFraction, y + yFraction, TILE_SIZE) == WorldTile.NOTHING &&
				   blockingLayer.getTileAt(x + TILE_SIZE - xFraction, y + yFraction, TILE_SIZE) == WorldTile.NOTHING;
		}
		return true;
	}

	public void update(int time) {
		if(player != null) player.update(this, time);
	}

	@Override
	protected void paintImpl(Surface surf) {
		surf.clear(0.08f, 0.08f, 0.08f, 1.0f);

		if(bottomLayer != null) bottomLayer.draw(surf, TILE_SIZE, worldTileTextures);
		if(blockingLayer != null) blockingLayer.draw(surf, TILE_SIZE, worldTileTextures);

		if(player != null) player.draw(surf, TILE_SIZE);

		if(topLayer != null) topLayer.draw(surf, TILE_SIZE, worldTileTextures);
	}

	private void loadMap(Assets assets, String mapName) {
		System.out.println("Loading map: " + mapName);

		assets.getText(MAP_DIRECTORY_PATH + mapName + "_top.csv").onSuccess(new Slot<String>() {
			@Override
			public void onEmit(String data) { topLayer = new WorldLayer(LAYER_WIDTH, LAYER_HEIGHT, data); }
		});
		assets.getText(MAP_DIRECTORY_PATH + mapName + "_blocking.csv").onSuccess(new Slot<String>() {
			@Override
			public void onEmit(String data) { blockingLayer = new WorldLayer(LAYER_WIDTH, LAYER_HEIGHT, data); }
		});
		assets.getText(MAP_DIRECTORY_PATH + mapName + "_bottom.csv").onSuccess(new Slot<String>() {
			@Override
			public void onEmit(String data) { bottomLayer = new WorldLayer(LAYER_WIDTH, LAYER_HEIGHT, data); }
		});
	}
}
