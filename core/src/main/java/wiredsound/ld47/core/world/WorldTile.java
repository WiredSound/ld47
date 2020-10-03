package wiredsound.ld47.core.world;

public enum WorldTile {
	NOTHING(-1, 0),
	WOOD_FLOORING(12, Colours.BROWN),
	BRICKS(22, Colours.DARK_GREY),
	CRACKED_BRICKS(13, Colours.DARK_GREY),
	FANCY_BRICKS(17, Colours.DARK_GREY),
	VENT_BRICKS(15, Colours.DARK_GREY),
	BRIDGE_PARAPET(14, Colours.DARK_GREY),
	BRIDGE_ARCHWAY_TOP_LEFT(18, Colours.DARK_GREY),
	BRIDGE_ARCHWAY_TOP_RIGHT(19, Colours.DARK_GREY),
	BRIDGE_ARCHWAY_LEFT(23, Colours.DARK_GREY),
	BRIDGE_ARCHWAY_RIGHT(24, Colours.DARK_GREY),
	BRIDGE_LIGHT_HOLDER_TOP(16, Colours.DARK_GREY),
	BRIDGE_LIGHT_HOLDER_BOTTOM(21, Colours.DARK_GREY),
	BRIDGE_LIGHT_TOP(26, Colours.LAMP_COLOURS),
	BRIDGE_LIGHT_BOTTOM(31, Colours.LAMP_COLOURS);

	final int id;
	int[] colours;
	private int colourIndex = 0;

	private WorldTile(int id, int colour) { this.id = id; this.colours = new int[] { colour }; }
	private WorldTile(int id, int[] colours) { this.id = id; this.colours = colours; }

	private static WorldTile[] tiles;

	int getColour() { return colours[colourIndex]; }

	void nextColour() {
		colourIndex++;
		if(colourIndex >= colours.length) colourIndex = 0;
	}

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
	public static final int DARK_GREY = 0xFFABABAB;
	public static final int LIGHT_YELLOW = 0xFFFFFF66;
	public static final int YELLOW = 0xFFFFFF00;
	public static final int DARK_YELLOW = 0xFFE5E500;
	public static final int[] LAMP_COLOURS = new int[] { DARK_YELLOW, YELLOW, LIGHT_YELLOW, YELLOW };
}
