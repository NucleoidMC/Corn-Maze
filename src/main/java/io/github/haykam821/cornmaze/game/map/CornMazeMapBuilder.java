package io.github.haykam821.cornmaze.game.map;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import io.github.haykam821.cornmaze.game.CornMazeConfig;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import xyz.nucleoid.map_templates.BlockBounds;
import xyz.nucleoid.map_templates.MapTemplate;

public class CornMazeMapBuilder {
	private static final BlockState BARRIER_STATE = Blocks.BARRIER.getDefaultState();

	private final CornMazeConfig config;

	public CornMazeMapBuilder(CornMazeConfig config) {
		this.config = config;
	}

	public CornMazeMap create() {
		MapTemplate template = MapTemplate.createEmpty();
		CornMazeMapConfig mapConfig = this.config.getMapConfig();

		BlockBounds bounds = BlockBounds.of(BlockPos.ORIGIN, new BlockPos(mapConfig.getX() * mapConfig.getXScale() - 1, mapConfig.getHeight(), mapConfig.getZ() * mapConfig.getZScale() - 1));

		// Make maze 2D array with default walls
		MazeState[][] maze = new MazeState[mapConfig.getZ()][mapConfig.getX()];
		for (int z = 0; z < maze.length; z++) {
			for (int x = 0; x < maze[z].length; x++) {
				this.setMazeState(x, z, MazeState.WALL, maze);
			}
		}

		Random random = new Random();
		int startX = (random.nextInt((mapConfig.getX() - 1) / 2) + 1) * 2 - 1;
		int startZ = (random.nextInt((mapConfig.getZ() - 1) / 2) + 1) * 2 - 1;

		Object2IntOpenHashMap<MazeCoordinate> targets = new Object2IntOpenHashMap<MazeCoordinate>();
		this.formMaze(startX, startZ, maze, targets, 0);

		MazeCoordinate endCoordinate = this.getFurthest(targets);
		this.setMazeState(endCoordinate.getX(), endCoordinate.getZ(), MazeState.END, maze);

		this.build(bounds, template, mapConfig, maze, random);

		return new CornMazeMap(template, bounds, this.getBounds(startX, startZ, mapConfig), this.getBounds(endCoordinate.getX(), endCoordinate.getZ(), mapConfig));
	}

	private MazeCoordinate getFurthest(Object2IntOpenHashMap<MazeCoordinate> targets) {
		int furthest = Collections.max(targets.values());
		for (Object2IntMap.Entry<MazeCoordinate> entry : targets.object2IntEntrySet()) {
			if (furthest == entry.getIntValue()) {
				return entry.getKey();
			}
		}
		return null;
	}

	private void setMazeState(int x, int z, MazeState state, MazeState[][] maze) {
		maze[z][x] = state;
	}

	private MazeState getMazeState(int x, int z, MazeState[][] maze) {
		return maze[z][x];
	}

	private BlockBounds getBounds(int x, int z, CornMazeMapConfig mapConfig) {
		BlockPos origin = new BlockPos(x * mapConfig.getXScale(), 0, z * mapConfig.getZScale());
		return BlockBounds.of(origin, origin.add(mapConfig.getXScale() - 1, mapConfig.getHeight(), mapConfig.getZScale() - 1));
	}

	private boolean isWall(int x, int z, MazeState[][] maze) {
		if (x <= 0) return false;
		if (x >= maze[0].length) return false;

		if (z <= 0) return false;
		if (z >= maze.length) return false;

		return this.getMazeState(x, z, maze) == MazeState.WALL;
	}

	private void formMaze(int x, int z, MazeState[][] maze, Object2IntOpenHashMap<MazeCoordinate> targets, int distance) {
		this.setMazeState(x, z, targets.size() == 0 ? MazeState.START : MazeState.PATH, maze);
		targets.put(new MazeCoordinate(x, z), distance);

		List<Direction> shuffledDirections = Direction.Type.HORIZONTAL.stream().collect(Collectors.toList());
		Collections.shuffle(shuffledDirections);

		for (Direction direction : shuffledDirections) {
			int targetX = x + direction.getOffsetX() * 2;
			int targetZ = z + direction.getOffsetZ() * 2;

			if (this.isWall(targetX, targetZ, maze)) {
				int linkX = x + direction.getOffsetX();
				int linkZ = z + direction.getOffsetZ();
	
				this.setMazeState(linkX, linkZ, MazeState.PATH, maze);
				this.formMaze(targetX, targetZ, maze, targets, distance + 2);
			}
		}
	}

	private boolean isDecayed(MazeState state, CornMazeMapConfig mapConfig, Random random) {
		if (!state.isDecayable()) return false;

		if (mapConfig.getDecay() <= 0) return false;
		if (mapConfig.getDecay() >= 1) return true;

		return random.nextDouble() < mapConfig.getDecay();
	}

	private void build(BlockBounds bounds, MapTemplate template, CornMazeMapConfig mapConfig, MazeState[][] maze, Random random) {
		for (BlockPos pos : bounds) {
			MazeState state = this.getMazeState(pos.getX() / mapConfig.getXScale(), pos.getZ() / mapConfig.getZScale(), maze);
			
			if ((state.isTall() || pos.getY() == 0) && !this.isDecayed(state, mapConfig, random)) {
				template.setBlockState(pos, state.getState());
			} else if (pos.getY() == mapConfig.getHeight()) {
				template.setBlockState(pos, BARRIER_STATE);
			}
		}
	}
}