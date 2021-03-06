package wiredsound.ld47.core.world;

enum WorldTile {
	NOTHING(-1, 0),
	WOOD_FLOORING(12, Colours.BROWN),
	DAMAGED_WOOD_FLOOR(13, Colours.BROWN),
	BRICKS(22, Colours.DARK_GREY),
	BRICKS_END_LEFT(39, Colours.DARK_GREY),
	CRACKED_BRICKS(20, Colours.DARK_GREY),
	FANCY_BRICKS(17, Colours.DARK_GREY),
	FANCY_BRICKS_END_LEFT(34, Colours.DARK_GREY),
	FANCY_BRICKS_END_RIGHT(41, Colours.DARK_GREY),
	VENT_BRICKS(15, Colours.DARK_GREY),
	BRIDGE_PARAPET(14, Colours.DARK_GREY),
	BRIDGE_PARAPET_END_LEFT(29, Colours.DARK_GREY),
	BRIDGE_ARCHWAY_TOP_LEFT(18, Colours.DARK_GREY),
	BRIDGE_ARCHWAY_TOP_RIGHT(19, Colours.DARK_GREY),
	BRIDGE_ARCHWAY_LEFT(23, Colours.DARK_GREY),
	BRIDGE_ARCHWAY_RIGHT(24, Colours.DARK_GREY),
	BRIDGE_LIGHT_HOLDER_TOP(16, Colours.DARK_GREY),
	BRIDGE_LIGHT_HOLDER_BOTTOM(21, Colours.DARK_GREY),
	BRIDGE_LIGHT_TOP(26, Colours.LAMP_COLOURS),
	BRIDGE_LIGHT_BOTTOM(31, Colours.LAMP_COLOURS),
	GRASS(11, Colours.DARK_GREEN),
	DIRT(10, Colours.BROWN),
	SIGN_TOP(25, Colours.LIGHT_BROWN),
	SIGN_BOTTOM(30, Colours.LIGHT_BROWN),
	MESSY_GRASS(28, Colours.GREEN),
	BUSH(27, Colours.LIGHT_GREEN),
	TREE_TOP(33, Colours.LIGHT_BROWN),
	TREE_BOTTOM(38, Colours.LIGHT_BROWN),
	TREE_CANOPY(32, Colours.LIGHT_GREEN),
	EXTENDED_FLOORING_ONE(35, Colours.BROWN),
	EXTENDED_FLOORING_TWO(36, Colours.BROWN),
	EXTENDED_FLOORING_THREE(37, Colours.BROWN),
	EXTENDED_FLOORING_FOUR(40, Colours.BROWN),
	WELL_TOP_LEFT(43, Colours.DARK_GREY),
	WELL_TOP_RIGHT(44, Colours.DARK_GREY),
	WELL_BOTTOM_LEFT(48, Colours.DARK_GREY),
	WELL_BOTTOM_RIGHT(49, Colours.DARK_GREY),
	NPC(3, 0xFFAF8865),
	VERTICAL_PARAPET_LEFT(42, Colours.DARK_GREY),
	VERTICAL_PARAPET_CONNECTING_LEFT(46, Colours.DARK_GREY),
	VERTICAL_PARAPET_CONNECTING_LEFT_OTHER(47, Colours.DARK_GREY),
	VERTICAL_PARAPET_RIGHT(45, Colours.DARK_GREY),
	VERTICAL_PARAPET_CONNECTING_RIGHT(50, Colours.DARK_GREY);

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
	public static final int DARK_GREEN = 0xFF013208;
	public static final int GREEN = 0xFF014F0C;
	public static final int LIGHT_GREEN = 0xFF00A000;
	public static final int LIGHT_BROWN = 0xFFB5651D;
}
