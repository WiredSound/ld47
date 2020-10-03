package wiredsound.ld47.core.world;

import java.util.HashMap;

import playn.core.Surface;
import playn.core.Tile;

class WorldLayer {
	private WorldTile[][] tiles;
	private final int width, height;

	WorldLayer(int width, int height, String data) {
		tiles = new WorldTile[width][height];
		this.width = width;
		this.height = height;

		String[] lines = data.split("\\n");

		for(int y = 0; y < height; y++) {
			String[] values = lines[y].split(",");

			for(int x = 0; x < width; x++) {
				int id = Integer.parseInt(values[x]);
				tiles[x][y] = WorldTile.fromId(id);
			}
		}
	}

	void draw(Surface surf, int tileSize, HashMap<WorldTile, Tile> worldTileTextures) {
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				WorldTile tile = tiles[x][y];

				Tile texture = worldTileTextures.get(tile);
				if(texture != null) surf.draw(texture, tile.colour, x * tileSize, y * tileSize, tileSize, tileSize);
			}
		}
	}

	WorldTile getTileAt(float x, float y, int tileSize) {
		int gridX = (int) Math.floor(x / tileSize);
		int gridY = (int) Math.floor(y / tileSize);

		return tiles[gridX][gridY];
	}
}
