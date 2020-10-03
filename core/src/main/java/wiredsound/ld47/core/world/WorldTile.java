package wiredsound.ld47.core.world;

public enum WorldTile {
	NOTHING(-1),
	WOOD_FLOORING(12, Colours.BROWN),
	BRICKS(22, Colours.DARK_GREY),
	CRACKED_BRICKS(13, Colours.DARK_GREY),
	FANCY_BRICKS(17, Colours.DARK_GREY),
	VENT_BRICKS(15, Colours.DARK_GREY),
	BRIDGE_PARAPET(14, Colours.DARK_GREY);

	final int id;
	int colour = 0xFFFFFFFF;

	private WorldTile(int id) { this.id = id; }
	private WorldTile(int id, int colour) { this.id = id; this.colour = colour; }

	private static WorldTile[] tiles;

	public static final WorldTile fromId(int searchId) {
		if(tiles == null) tiles = values();

		for(WorldTile tile : tiles) {
			if(tile.id == searchId) return tile;
		}
		return NOTHING;
	}
}

final class Colours {
	public static final int BROWN = 0xFF2C1B0B;
	public static final int DARK_GREY = 0xFF929292;
}