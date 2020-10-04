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
	private WorldLayer toppestLayer, topLayer, blockingLayer, bottomishLayer, bottomLayer;

	private EntityLayer entitiesLayer;

	// Map world map tiles to tileset texture tiles:
	private HashMap<WorldTile, Tile> worldTileTextures = new HashMap<WorldTile, Tile>();

	// For animating tiles through colour changes:
	private int tileColourTimer = 0;

	private TextBox textBox;
	private TypedArray<String> signTextLines;

	// Text displayed when moving off screen in a given direction:
	private HashMap<Direction, TypedArray<String>> offScreenTextLines = new HashMap<Direction, TypedArray<String>>();

	// Name of the map to transition to when the player moves off the screen in a given direction:
	private HashMap<Direction, String> offScreenMapNames = new HashMap<Direction, String>();

	// Story objectives:
	private boolean obtainedGoldCoin = false, obtainedScroll = false;

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
				addTile(WorldTile.DAMAGED_WOOD_FLOOR, tileset, 3, 2);
				addTile(WorldTile.BRICKS, tileset, 2, 4);
				addTile(WorldTile.BRICKS_END_LEFT, tileset, 4, 7);
				addTile(WorldTile.CRACKED_BRICKS, tileset, 0, 4);
				addTile(WorldTile.FANCY_BRICKS, tileset, 2, 3);
				addTile(WorldTile.FANCY_BRICKS_END_LEFT, tileset, 4, 6);
				addTile(WorldTile.VENT_BRICKS, tileset, 0, 3);
				addTile(WorldTile.BRIDGE_PARAPET, tileset, 4, 2);
				addTile(WorldTile.BRIDGE_PARAPET_END_LEFT, tileset, 4, 5);
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
				addTile(WorldTile.TREE_BOTTOM, tileset, 3, 7);
				addTile(WorldTile.TREE_TOP, tileset, 3, 6);
				addTile(WorldTile.TREE_CANOPY, tileset, 2, 6);
				addTile(WorldTile.EXTENDED_FLOORING_ONE, tileset, 0, 7);
				addTile(WorldTile.EXTENDED_FLOORING_TWO, tileset, 1, 7);
				addTile(WorldTile.EXTENDED_FLOORING_THREE, tileset, 2, 7);
				addTile(WorldTile.EXTENDED_FLOORING_FOUR, tileset, 0, 8);
				addTile(WorldTile.WELL_TOP_LEFT, tileset, 3, 8);
				addTile(WorldTile.WELL_TOP_RIGHT, tileset, 4, 8);
				addTile(WorldTile.WELL_BOTTOM_LEFT, tileset, 3, 9);
				addTile(WorldTile.WELL_BOTTOM_RIGHT, tileset, 4, 9);
				addTile(WorldTile.NPC, tileset, 3, 0);

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
						float lookingAtX = player.x + TILE_SIZE / 2;
						float lookingAtY = player.y + TILE_SIZE / 2;

						switch(player.getFacingDirection()) {
						case UP: lookingAtY -= TILE_SIZE * 0.6; break;
						case DOWN: lookingAtY += TILE_SIZE * 0.6; break;
						case LEFT: lookingAtX -= TILE_SIZE * 0.6; break;
						case RIGHT: lookingAtX += TILE_SIZE * 0.6; break;
						}

						final WorldTile blockingLookingAt = blockingLayer.getTileAt(lookingAtX, lookingAtY, TILE_SIZE, LAYER_WIDTH, LAYER_HEIGHT);
						final WorldTile topLookingAt = topLayer.getTileAt(lookingAtX, lookingAtY, TILE_SIZE, LAYER_WIDTH, LAYER_HEIGHT);

						System.out.println("Interacting with " + blockingLookingAt + " or " + topLookingAt);

						if(textBox.isComplete()) {
							if(signTextLines != null && (blockingLookingAt == WorldTile.SIGN_BOTTOM || topLookingAt == WorldTile.SIGN_TOP) ){
								textBox.reset();
								for(String line : signTextLines) textBox.addPart(line);
							}

							if(blockingLookingAt == WorldTile.BUSH) {
								textBox.reset();
								textBox.addPart("I don't think rummaging around in a bush would be a good use of time right now.");
							}
							else if(blockingLookingAt == WorldTile.TREE_BOTTOM || topLookingAt == WorldTile.TREE_TOP) {
								textBox.reset();
								textBox.addPart("I doubt I could climb this tree.");
							}
							else if(blockingLookingAt == WorldTile.WELL_BOTTOM_LEFT || blockingLookingAt == WorldTile.WELL_BOTTOM_RIGHT ||
									topLookingAt == WorldTile.WELL_TOP_LEFT || topLookingAt == WorldTile.WELL_TOP_RIGHT) {
								textBox.reset();

								textBox.addPart("It's a well.");
								textBox.addPart("Unsuprisingly, it appears to have dried up long ago.");
								textBox.addPart("Looks like there might be something at the bottom...");
								textBox.addPart("(Antique gold coin found!)");
								textBox.addPart("The marking of this coin are quite unlike anything I've ever seen before.");
								textBox.addPart("It does not look like something of this world.");

								obtainedGoldCoin = true;
							}
							else if(blockingLookingAt == WorldTile.NPC) {
								textBox.reset();

								textBox.addPart("You do know you shouldn't be here, correct? What do you want?");
								textBox.addPart("...");

								if(obtainedGoldCoin) {
									textBox.addPart("Where did you find this? This coin?");
									textBox.addPart("In exchange for such an artifact, I suppose I could assist you...");
									textBox.addPart("(Obtained a scroll!)");
									textBox.addPart("You see the tower at the end of that bridge? Read this scroll aloud at the very top of it.");
									textBox.addPart("A word of advice however... Breaking free of this cycle will certainly not be as easy as you may hope.");
									textBox.addPart("Good luck.");

									obtainedScroll = true;
								}
								else {
									textBox.addPart("How do you know of them? Who told you?");
									textBox.addPart("... Perhaps I can help you, but it won't be out of the goodness of my heart.");
									textBox.addPart("If you haven't got anything valuable to offer me in return then I'm not interested I'm afraid.");
								}
							}
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
		if(blockingLayer == null || x < 0 || x > LAYER_WIDTH * TILE_SIZE || y < 0 || y > LAYER_HEIGHT * TILE_SIZE) return false;

		float yFraction = TILE_SIZE / 2.5f;
		float xFraction = TILE_SIZE / 4.5f;

		switch(d) {
		case RIGHT:
			return blockingLayer.getTileAt(x + TILE_SIZE - xFraction, y + yFraction, TILE_SIZE, LAYER_WIDTH, LAYER_HEIGHT) == WorldTile.NOTHING &&
				   blockingLayer.getTileAt(x + TILE_SIZE - xFraction, y + TILE_SIZE, TILE_SIZE, LAYER_WIDTH, LAYER_HEIGHT) == WorldTile.NOTHING &&
				   x <= (LAYER_WIDTH - 1) * TILE_SIZE;

		case LEFT:
			return blockingLayer.getTileAt(x + xFraction, y + yFraction, TILE_SIZE, LAYER_WIDTH, LAYER_HEIGHT) == WorldTile.NOTHING &&
				   blockingLayer.getTileAt(x + xFraction, y + TILE_SIZE, TILE_SIZE, LAYER_WIDTH, LAYER_HEIGHT) == WorldTile.NOTHING &&
				   x >= 0;

		case DOWN:
			return blockingLayer.getTileAt(x + xFraction, y + TILE_SIZE, TILE_SIZE, LAYER_WIDTH, LAYER_HEIGHT) == WorldTile.NOTHING &&
				   blockingLayer.getTileAt(x + TILE_SIZE - xFraction, y + TILE_SIZE, TILE_SIZE, LAYER_WIDTH, LAYER_HEIGHT) == WorldTile.NOTHING &&
				   y <= (LAYER_HEIGHT - 1) * TILE_SIZE;

		case UP:
			return blockingLayer.getTileAt(x + xFraction, y + yFraction, TILE_SIZE, LAYER_WIDTH, LAYER_HEIGHT) == WorldTile.NOTHING &&
				   blockingLayer.getTileAt(x + TILE_SIZE - xFraction, y + yFraction, TILE_SIZE, LAYER_WIDTH, LAYER_HEIGHT) == WorldTile.NOTHING &&
				   y >= 0;
		}
		return true;
	}

	@Override
	public UpdatableLayer update(int time) {
		textBox.update(time);

		if(player != null) {
			player.update(this, time);

			if(player.x <= 1) handlePlayerOffScreen(Direction.LEFT);
			else if(player.x >= (LAYER_WIDTH - 1) * TILE_SIZE - 1) handlePlayerOffScreen(Direction.RIGHT);
			if(player.y <= 1) handlePlayerOffScreen(Direction.UP);
			else if(player.y >= (LAYER_HEIGHT - 1) * TILE_SIZE - 1) handlePlayerOffScreen(Direction.DOWN);
		}

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

		offScreenTextLines.clear();
		offScreenMapNames.clear();

		assets.getText(MAP_DIRECTORY_PATH + mapName + ".json").onSuccess(new Slot<String>() {
			@Override
			public void onEmit(String data) {
				Json.Object obj = plat.json().parse(data);

				signTextLines = obj.getArray("sign text", String.class, null);

				offScreenTextLines.put(Direction.LEFT, obj.getArray("left text", String.class, null));
				offScreenTextLines.put(Direction.RIGHT, obj.getArray("right text", String.class, null));
				offScreenTextLines.put(Direction.UP, obj.getArray("up text", String.class, null));
				offScreenTextLines.put(Direction.DOWN, obj.getArray("down text", String.class, null));

				offScreenMapNames.put(Direction.LEFT, obj.getString("left map", ""));
				offScreenMapNames.put(Direction.RIGHT, obj.getString("right map", ""));
				offScreenMapNames.put(Direction.UP, obj.getString("up map", ""));
				offScreenMapNames.put(Direction.DOWN, obj.getString("down map", ""));
			}
		});

		assets.getText(MAP_DIRECTORY_PATH + mapName + "_bottom.csv").onSuccess(new Slot<String>() {
			@Override
			public void onEmit(String data) {
				bottomLayer = newLayer(bottomLayer, data);

				assets.getText(MAP_DIRECTORY_PATH + mapName + "_bottomish.csv").onSuccess(new Slot<String>() {
					@Override
					public void onEmit(String data) {
						bottomishLayer = newLayer(bottomishLayer, data);

						loadRemainingLayers(assets, mapName);
					}
				}).onFailure(new Slot<Throwable>() {
					@Override
					public void onEmit(Throwable event) {
						System.out.println("This map does not have a 'bottomish' layer");

						if(bottomishLayer != null) {
							remove(bottomishLayer);
							bottomishLayer = null;
						}

						loadRemainingLayers(assets, mapName);
					}
				});
			}
		});
	}

	private void loadRemainingLayers(final Assets assets, final String mapName) {
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
						}).onFailure(new Slot<Throwable>() {
							@Override
							public void onEmit(Throwable e) {
								System.out.println("This map does not have a 'toppest' layer");

								if(toppestLayer != null) {
									remove(toppestLayer);
									toppestLayer = null;
								}
							}});
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

	private void handlePlayerOffScreen(Direction d) {
		TypedArray<String> lines = offScreenTextLines.getOrDefault(d, null);

		if(lines != null && textBox.isComplete()) {
			textBox.reset();

			for(String line : lines) {
				System.out.println("Player moved off screen so displaying via text box UI: " + line);

				textBox.addPart(line);
			}
		}

		String mapName = offScreenMapNames.getOrDefault(d, "");

		if(!mapName.isEmpty()) {
			System.out.println("Player moved off screen so transitioning to specified level: " + mapName);

			switch(d) {
			case RIGHT: player.x = TILE_SIZE; break;
			case LEFT: player.x  = (LAYER_WIDTH - 2) * TILE_SIZE; break;
			case DOWN: player.y = TILE_SIZE; break;
			case UP: player.y = (LAYER_HEIGHT - 2) * TILE_SIZE; break;
			}

			loadMap(plat.assets(), mapName);
		}
	}
}
