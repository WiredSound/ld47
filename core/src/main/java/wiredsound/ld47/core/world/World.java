package wiredsound.ld47.core.world;

import java.util.HashMap;

import playn.core.Assets;
import playn.core.Image;
import playn.core.Json;
import playn.core.Json.TypedArray;
import playn.core.Keyboard;
import playn.core.Keyboard.KeyEvent;
import playn.core.Platform;
import playn.core.Texture;
import playn.core.Tile;
import react.Slot;
import wiredsound.ld47.core.UpdatableLayer;
import wiredsound.ld47.core.ui.TextBox;
import wiredsound.ld47.core.world.entities.Direction;
import wiredsound.ld47.core.world.entities.Human;

public class World extends UpdatableLayer {
	// Size in pixels of each tile:
	public static final int TILE_SIZE = 16;

	public static final int LAYER_WIDTH = 16;
	public static final int LAYER_HEIGHT = 12;

	public static final String TILESET_PATH = "images/tileset.png";
	public static final String MAP_DIRECTORY_PATH = "maps/";

	private Human player;

	// Layers of the currently loaded world area/map:
	private WorldLayer toppestLayer, topLayer, blockingLayer, bottomLayer;

	private EntityLayer entitiesLayer;

	// Map world map tiles to tileset texture tiles:
	private HashMap<WorldTile, Tile> worldTileTextures = new HashMap<WorldTile, Tile>();

	// For animating tiles through colour changes:
	private int tileColourTimer = 0;

	private TextBox textBox;
	private TypedArray<String> signTextLines;

	public World(final Platform plat, final String mapName) {
		super(plat);

		setScale(4);

		textBox = new TextBox(plat);
		textBox.setVisible(false);
		textBox.setScale(1.0f / 4.0f); // Compensate for resizing of parent layer.
		add(textBox);

		plat.assets().getImage(TILESET_PATH).state.onSuccess(new Slot<Image>() {
			@Override
			public void onEmit(Image image) {
				image.setConfig(TEXTURE_CONFIG);

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

				entitiesLayer = new EntityLayer(player, TILE_SIZE);
				add(entitiesLayer);

				System.out.println("Created player character");

				addTile(WorldTile.WOOD_FLOORING, tileset, 2, 2);
				addTile(WorldTile.BRICKS, tileset, 2, 4);
				addTile(WorldTile.CRACKED_BRICKS, tileset, 3, 2);
				addTile(WorldTile.FANCY_BRICKS, tileset, 2, 3);
				addTile(WorldTile.VENT_BRICKS, tileset, 0, 3);
				addTile(WorldTile.BRIDGE_PARAPET, tileset, 4, 2);
				addTile(WorldTile.BRIDGE_ARCHWAY_TOP_LEFT, tileset, 3, 3);
				addTile(WorldTile.BRIDGE_ARCHWAY_TOP_RIGHT, tileset, 4, 3);
				addTile(WorldTile.BRIDGE_ARCHWAY_LEFT, tileset, 3, 4);
				addTile(WorldTile.BRIDGE_ARCHWAY_RIGHT, tileset, 4, 4);
				addTile(WorldTile.BRIDGE_LIGHT_HOLDER_TOP, tileset, 1, 3);
				addTile(WorldTile.BRIDGE_LIGHT_HOLDER_BOTTOM, tileset, 1, 4);
				addTile(WorldTile.BRIDGE_LIGHT_TOP, tileset, 1, 5);
				addTile(WorldTile.BRIDGE_LIGHT_BOTTOM, tileset, 1, 6);
				addTile(WorldTile.GRASS, tileset, 1, 2);
				addTile(WorldTile.DIRT, tileset, 0, 2);
				addTile(WorldTile.SIGN_TOP, tileset, 0, 5);
				addTile(WorldTile.SIGN_BOTTOM, tileset, 0, 6);
				addTile(WorldTile.MESSY_GRASS, tileset, 3, 5);
				addTile(WorldTile.BUSH, tileset, 2, 5);
				addTile(WorldTile.TREE_BOTTOM, tileset, 2, 7);
				addTile(WorldTile.TREE_TOP, tileset, 2, 6);
				addTile(WorldTile.TREE_CANOPY, tileset, 3, 6);

				System.out.println("Prepared world tiles");

				loadMap(plat.assets(), mapName);
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

					case E: case ENTER:
						float lookingAtX = player.getX() + TILE_SIZE / 2;
						float lookingAtY = player.getY() + TILE_SIZE / 2;

						switch(player.getFacingDirection()) {
						case UP: lookingAtY -= TILE_SIZE * 0.6; break;
						case DOWN: lookingAtY += TILE_SIZE * 0.4; break;
						case LEFT: lookingAtX -= TILE_SIZE * 0.6; break;
						case RIGHT: lookingAtX += TILE_SIZE * 0.6; break;
						}

						if(textBox.isComplete() && signTextLines != null &&
						   (blockingLayer.getTileAt(lookingAtX, lookingAtY, TILE_SIZE, LAYER_WIDTH, LAYER_HEIGHT) == WorldTile.SIGN_BOTTOM ||
							topLayer.getTileAt(lookingAtX, lookingAtY, TILE_SIZE, LAYER_WIDTH, LAYER_HEIGHT) == WorldTile.SIGN_TOP)) {
							System.out.println("Reading sign");
							textBox.reset();
							for(String line : signTextLines) textBox.addPart(line);
						}

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
	}

	public boolean allowMovementTo(float x, float y, Direction d) {
		float yFraction = TILE_SIZE / 2.5f;
		float xFraction = TILE_SIZE / 4.5f;

		switch(d) {
		case RIGHT:
			return blockingLayer.getTileAt(x + TILE_SIZE - xFraction, y + yFraction, TILE_SIZE, LAYER_WIDTH, LAYER_HEIGHT) == WorldTile.NOTHING &&
				   blockingLayer.getTileAt(x + TILE_SIZE - xFraction, y + TILE_SIZE, TILE_SIZE, LAYER_WIDTH, LAYER_HEIGHT) == WorldTile.NOTHING;

		case LEFT:
			return blockingLayer.getTileAt(x + xFraction, y + yFraction, TILE_SIZE, LAYER_WIDTH, LAYER_HEIGHT) == WorldTile.NOTHING &&
				   blockingLayer.getTileAt(x + xFraction, y + TILE_SIZE, TILE_SIZE, LAYER_WIDTH, LAYER_HEIGHT) == WorldTile.NOTHING;

		case DOWN:
			return blockingLayer.getTileAt(x + xFraction, y + TILE_SIZE, TILE_SIZE, LAYER_WIDTH, LAYER_HEIGHT) == WorldTile.NOTHING &&
				   blockingLayer.getTileAt(x + TILE_SIZE - xFraction, y + TILE_SIZE, TILE_SIZE, LAYER_WIDTH, LAYER_HEIGHT) == WorldTile.NOTHING;

		case UP:
			return blockingLayer.getTileAt(x + xFraction, y + yFraction, TILE_SIZE, LAYER_WIDTH, LAYER_HEIGHT) == WorldTile.NOTHING &&
				   blockingLayer.getTileAt(x + TILE_SIZE - xFraction, y + yFraction, TILE_SIZE, LAYER_WIDTH, LAYER_HEIGHT) == WorldTile.NOTHING;
		}
		return true;
	}

	@Override
	public UpdatableLayer update(int time) {
		textBox.update(time);

		if(player != null) player.update(this, time);

		tileColourTimer += time;
		if(tileColourTimer > 300) { // Change lamp colours ever 300ms:
			tileColourTimer = 0;

			WorldTile.BRIDGE_LIGHT_TOP.nextColour();
			WorldTile.BRIDGE_LIGHT_BOTTOM.nextColour();
		}

		return this;
	}

	private void loadMap(final Assets assets, final String mapName) {
		System.out.println("Loading map: " + mapName);

		assets.getText(MAP_DIRECTORY_PATH + mapName + ".json").onSuccess(new Slot<String>() {
			@Override
			public void onEmit(String data) {
				Json.Object obj = plat.json().parse(data);

				if(obj.containsKey("sign text")) {
					signTextLines = obj.getArray("sign text", String.class);
				}
			}
		});

		assets.getText(MAP_DIRECTORY_PATH + mapName + "_bottom.csv").onSuccess(new Slot<String>() {
			@Override
			public void onEmit(String data) {
				bottomLayer = newLayer(bottomLayer, data);

				assets.getText(MAP_DIRECTORY_PATH + mapName + "_blocking.csv").onSuccess(new Slot<String>() {
					@Override
					public void onEmit(String data) {
						blockingLayer = newLayer(blockingLayer, data);

						// TODO: Load any entities specific to this map...

						remove(entitiesLayer);
						add(entitiesLayer);

						assets.getText(MAP_DIRECTORY_PATH + mapName + "_top.csv").onSuccess(new Slot<String>() {
							@Override
							public void onEmit(String data) {
								topLayer = newLayer(topLayer, data);

								assets.getText(MAP_DIRECTORY_PATH + mapName + "_toppest.csv").onSuccess(new Slot<String>() {
									@Override
									public void onEmit(String data) {
										toppestLayer = newLayer(toppestLayer, data);

										remove(textBox);
										add(textBox);
									}
								});
							}
						});
					}
				});
			}
		});
	}

	private WorldLayer newLayer(WorldLayer layer, String data) {
		if(layer != null) remove(layer);
		WorldLayer newLayer = new WorldLayer(TILE_SIZE, LAYER_WIDTH, LAYER_HEIGHT, worldTileTextures, data);
		add(newLayer);
		System.out.println("Loaded layer: " + newLayer.name());
		return newLayer;
	}

	private void addTile(WorldTile tile, Texture tileset, int x, int y) {
		Tile t = tileset.tile(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
		worldTileTextures.put(tile, t);
	}
}
