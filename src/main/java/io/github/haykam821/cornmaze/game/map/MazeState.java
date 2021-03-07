package io.github.haykam821.cornmaze.game.map;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public enum MazeState {
	PATH(Blocks.BLACK_TERRACOTTA.getDefaultState(), false, true),
	START(Blocks.RED_TERRACOTTA.getDefaultState()),
	END(Blocks.LIME_TERRACOTTA.getDefaultState()),
	WALL(Blocks.HAY_BLOCK.getDefaultState(), true, false);

	private final BlockState state;
	private final boolean tall;
	private final boolean decayable;

	private MazeState(BlockState state, boolean tall, boolean decayable) {
		this.state = state;
		this.tall = tall;
		this.decayable = decayable;
	}

	private MazeState(BlockState state) {
		this(state, false, false);
	}

	public BlockState getState() {
		return this.state;
	}

	public boolean isTall() {
		return this.tall;
	}

	public boolean isDecayable() {
		return this.decayable;
	}
}
