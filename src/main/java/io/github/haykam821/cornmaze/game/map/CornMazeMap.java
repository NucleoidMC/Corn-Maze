package io.github.haykam821.cornmaze.game.map;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import xyz.nucleoid.map_templates.BlockBounds;
import xyz.nucleoid.map_templates.MapTemplate;
import xyz.nucleoid.plasmid.game.player.PlayerOffer;
import xyz.nucleoid.plasmid.game.player.PlayerOfferResult;
import xyz.nucleoid.plasmid.game.world.generator.TemplateChunkGenerator;

public class CornMazeMap {
	private final MapTemplate template;
	private final Box box;
	private final Box startBox;
	private final Box endBox;
	private final BlockBounds barrierBounds;
	private final Vec3d spawn;
	private final float spawnYaw;

	public CornMazeMap(MapTemplate template, BlockBounds bounds, BlockBounds startBounds, BlockBounds endBounds, BlockBounds barrierBounds, Direction startDirection) {
		this.template = template;
		this.box = bounds.asBox();
		this.startBox = startBounds.asBox();
		this.endBox = endBounds.asBox();
		this.barrierBounds = barrierBounds;

		Vec3d center = this.startBox.getCenter();
		this.spawn = new Vec3d(center.getX(), this.box.minY + 1, center.getZ());
		this.spawnYaw = startDirection.asRotation();
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

	public void spawn(ServerPlayerEntity player, ServerWorld world) {
		player.teleport(world, this.spawn.getX(), this.spawn.getY(), this.spawn.getZ(), this.spawnYaw, 0);
	}

	public PlayerOfferResult.Accept acceptOffer(PlayerOffer offer, ServerWorld world, GameMode gameMode) {
		return offer.accept(world, this.spawn).and(() -> {
			offer.player().changeGameMode(gameMode);
			offer.player().setYaw(this.spawnYaw);
		});
	}

	public ChunkGenerator createGenerator(MinecraftServer server) {
		return new TemplateChunkGenerator(server, this.template);
	}
}