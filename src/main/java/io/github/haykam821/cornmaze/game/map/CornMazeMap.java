package io.github.haykam821.cornmaze.game.map;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import xyz.nucleoid.map_templates.BlockBounds;
import xyz.nucleoid.map_templates.MapTemplate;
import xyz.nucleoid.plasmid.game.world.generator.TemplateChunkGenerator;

public class CornMazeMap {
	private final MapTemplate template;
	private final Box box;
	private final Box startBox;
	private final Box endBox;
	private final BlockBounds barrierBounds;
	private final Vec3d spawn;

	public CornMazeMap(MapTemplate template, BlockBounds bounds, BlockBounds startBounds, BlockBounds endBounds, BlockBounds barrierBounds) {
		this.template = template;
		this.box = bounds.asBox();
		this.startBox = startBounds.asBox();
		this.endBox = endBounds.asBox();
		this.barrierBounds = barrierBounds;

		Vec3d center = this.startBox.getCenter();
		this.spawn = new Vec3d(center.getX(), this.box.minY + 1, center.getZ());
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

	public BlockBounds getBarrierBounds() {
		return this.barrierBounds;
	}

	public Vec3d getSpawn() {
		return this.spawn;
	}

	public ChunkGenerator createGenerator(MinecraftServer server) {
		return new TemplateChunkGenerator(server, this.template);
	}
}