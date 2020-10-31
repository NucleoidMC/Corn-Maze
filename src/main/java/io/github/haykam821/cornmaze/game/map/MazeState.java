package io.github.haykam821.cornmaze.game.map;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public enum MazeState {
	PATH(Blocks.BLACK_TERRACOTTA.getDefaultState(), false),
	START(Blocks.RED_TERRACOTTA.getDefaultState(), false),
	END(Blocks.LIME_TERRACOTTA.getDefaultState(), false),
	WALL(Blocks.HAY_BLOCK.getDefaultState(), true);

	private final BlockState state;
	private final boolean tall;

	private MazeState(BlockState state, boolean tall) {
		this.state = state;
		this.tall = tall;
	}

	public BlockState getState() {
		return this.state;
	}

	public boolean isTall() {
		return this.tall;
	}
}
