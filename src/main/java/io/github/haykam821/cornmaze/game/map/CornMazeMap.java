package io.github.haykam821.cornmaze.game.map;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import xyz.nucleoid.plasmid.game.map.template.MapTemplate;
import xyz.nucleoid.plasmid.game.map.template.TemplateChunkGenerator;
import xyz.nucleoid.plasmid.util.BlockBounds;

public class CornMazeMap {
	private final MapTemplate template;
	private final Box box;
	private final Box startBox;
	private final Box endBox;

	public CornMazeMap(MapTemplate template, BlockBounds bounds, BlockBounds startBounds, BlockBounds endBounds) {
		this.template = template;
		this.box = bounds.toBox();
		this.startBox = startBounds.toBox();
		this.endBox = endBounds.toBox();
	}

	public Box getBox() {
		return this.box;
	}

	public Box getStartBox() {
		return this.startBox;
	}

	public Box getEndBox() {
		return this.endBox;
	}

	public ChunkGenerator createGenerator(MinecraftServer server) {
		return new TemplateChunkGenerator(server, this.template, BlockPos.ORIGIN);
	}
}