package io.github.haykam821.cornmaze.game.map;

public class MazeCoordinate {
	private int x;
	private int z;

	public MazeCoordinate(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public int getX() {
		return this.x;
	}

	public int getZ() {
		return this.z;
	}
}
