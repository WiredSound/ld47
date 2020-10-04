package wiredsound.ld47.core.world.entities;

import java.util.ArrayList;

import playn.core.Surface;
import playn.core.Tile;
import wiredsound.ld47.core.world.World;

public class Human extends Entity {
	private int colour;

	private Direction facingDirection = Direction.RIGHT;
	private ArrayList<Direction> travellingDirections = new ArrayList<Direction>();

	private Tile facingRightTile;
	private Tile[] movingRightTiles;
	private Tile facingForwardTile;
	private Tile[] movingForwardTiles;
	private Tile facingBackwardTile;
	private Tile[] movingBackwardTiles;

	private int movementFrame = 0;
	private float movementAnimationTimer = 0;

	private final int movementAnimationSpeed;
	private final float movementSpeed;

	public Human(String name, float x, float y,
				 int colour, int animationSpeed, float movementSpeed,
				 Tile facingRightTile, Tile[] movingRightTiles,
				 Tile facingForwardTile, Tile[] movingForwardTiles,
				 Tile facingBackwardTile, Tile[] movingBackwardTiles) {
		super(name, x, y);
		this.colour = colour;
		this.movementAnimationSpeed = animationSpeed;
		this.movementSpeed = movementSpeed;
		this.facingRightTile = facingRightTile;
		this.movingRightTiles = movingRightTiles;
		this.facingForwardTile = facingForwardTile;
		this.movingForwardTiles = movingForwardTiles;
		this.facingBackwardTile = facingBackwardTile;
		this.movingBackwardTiles = movingBackwardTiles;
	}

	public void move(Direction d) {
		if(!travellingDirections.contains(d)) {
			System.out.println(name + " will begin moving " + d + " from (" + x + ", " + y + ")");

			travellingDirections.add(d);
			facingDirection = d;
		}
	}

	public void halt(Direction d) {
		if(travellingDirections.contains(d)) {
			System.out.println(name + " will halt in direction " + d + " at (" + x + ", " + y + ")");

			travellingDirections.remove(d);

			if(isMoving()) {
				int index = travellingDirections.size() - 1;
				facingDirection = travellingDirections.get(index);
			}
		}
	}

	public boolean isMoving() {
		return !travellingDirections.isEmpty();
	}

	@Override
	public void update(World world, int time) {
		if(isMoving()) {
			// Animation:
			movementAnimationTimer += time;

			if(movementAnimationTimer >= movementAnimationSpeed) {
				movementAnimationTimer = 0;
				movementFrame = movementFrame == 1 ? 0 : 1;
			}

			float newX, newY;
			float movement = movementSpeed * time;
			for(Direction d : travellingDirections) {
				newX = x;
				newY = y;

				switch(d) {
				case UP: newY -= movement; break;
				case DOWN: newY += movement; break;
				case LEFT: newX -= movement; break;
				default: newX += movement;
				}

				if(world.allowMovementTo(newX, newY, d)) {
					x = newX;
					y = newY;
				}
				else {
					System.out.println("Movement of " + name + " from (" + x + ", " + x + ") in direction " + d + " was not allowed");
				}
			}
		}
	}

	@Override
	public void draw(Surface surf, int tileSize) {
		Tile toDraw;

		if(isMoving()) {
			switch(facingDirection) {
			case UP: toDraw = movingBackwardTiles[movementFrame]; break;
			case DOWN: toDraw = movingForwardTiles[movementFrame]; break;
			default: toDraw = movingRightTiles[movementFrame];
			}
		}
		else { // Not moving:
			switch(facingDirection) {
			case UP: toDraw = facingBackwardTile; break;
			case DOWN: toDraw = facingForwardTile; break;
			default: toDraw = facingRightTile;
			}
		}

		surf.draw(toDraw, colour,
			(facingDirection == Direction.LEFT ? x + tileSize : x), y,
			(facingDirection == Direction.LEFT ? -tileSize : tileSize), tileSize
		);
	}

	public Direction getFacingDirection() { return facingDirection; }
}
