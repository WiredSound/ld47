package wiredsound.ld47.core.world;

import java.util.HashMap;

import playn.core.Surface;
import playn.core.Tile;
import playn.scene.Layer;

class WorldLayer extends Layer {
	private final int tileSize;
	private final int width, height;
	private final HashMap<WorldTile, Tile> worldTileTextures;

	private WorldTile[][] tiles;

	WorldLayer(int tileSize, int width, int height, HashMap<WorldTile, Tile> worldTileTextures, String data) {
		this.tileSize = tileSize;
		this.width = width;
		this.height = height;
		this.worldTileTextures = worldTileTextures;

		tiles = new WorldTile[width][height];

		String[] lines = data.split("\\n");

		for(int y = 0; y < height; y++) {
			String[] values = lines[y].split(",");

			for(int x = 0; x < width; x++) {
				int id = Integer.parseInt(values[x]);
				tiles[x][y] = WorldTile.fromId(id);
			}
		}
	}

	@Override
	protected void paintImpl(Surface surf) {
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				WorldTile tile = tiles[x][y];

				Tile texture = worldTileTextures.get(tile);
				if(texture != null) surf.draw(texture, tile.getColour(), x * tileSize, y * tileSize, tileSize, tileSize);
			}
		}
	}

	WorldTile getTileAt(float x, float y, int tileSize, int layerWidth, int layerHeight) {
		int gridX = (int) Math.floor(x / tileSize);
		int gridY = (int) Math.floor(y / tileSize);

		if(gridX >= 0 && gridY >= 0 && gridX < layerWidth && gridY < layerHeight) return tiles[gridX][gridY];
		else return WorldTile.NOTHING;
	}
}
