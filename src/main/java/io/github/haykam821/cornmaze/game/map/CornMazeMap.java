package io.github.haykam821.cornmaze.game.map;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Box;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import xyz.nucleoid.map_templates.BlockBounds;
import xyz.nucleoid.map_templates.MapTemplate;
import xyz.nucleoid.plasmid.game.world.generator.TemplateChunkGenerator;

public class CornMazeMap {
	private final MapTemplate template;
	private final Box box;
	private final Box startBox;
	private final Box endBox;

	public CornMazeMap(MapTemplate template, BlockBounds bounds, BlockBounds startBounds, BlockBounds endBounds) {
		this.template = template;
		this.box = bounds.asBox();
		this.startBox = startBounds.asBox();
		this.endBox = endBounds.asBox();
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
		return new TemplateChunkGenerator(server, this.template);
	}
}