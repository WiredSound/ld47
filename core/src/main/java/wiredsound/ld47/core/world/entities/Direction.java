package wiredsound.ld47.core.world.entities;

public enum Direction {
	LEFT, RIGHT, UP, DOWN;

	@Override
	public String toString() {
		switch(this) {
		case LEFT: return "left";
		case RIGHT: return "right";
		case UP: return "up";
		default: return "down";
		}
	}

}
