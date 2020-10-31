package io.github.haykam821.cornmaze.game.phase;

import java.util.concurrent.CompletableFuture;

import io.github.haykam821.cornmaze.game.CornMazeConfig;
import io.github.haykam821.cornmaze.game.map.CornMazeMap;
import io.github.haykam821.cornmaze.game.map.CornMazeMapBuilder;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.GameMode;
import xyz.nucleoid.plasmid.game.GameOpenContext;
import xyz.nucleoid.plasmid.game.GameWaitingLobby;
import xyz.nucleoid.plasmid.game.GameWorld;
import xyz.nucleoid.plasmid.game.StartResult;
import xyz.nucleoid.plasmid.game.config.PlayerConfig;
import xyz.nucleoid.plasmid.game.event.GameTickListener;
import xyz.nucleoid.plasmid.game.event.OfferPlayerListener;
import xyz.nucleoid.plasmid.game.event.PlayerAddListener;
import xyz.nucleoid.plasmid.game.event.PlayerDeathListener;
import xyz.nucleoid.plasmid.game.event.RequestStartListener;
import xyz.nucleoid.plasmid.game.player.JoinResult;
import xyz.nucleoid.plasmid.world.bubble.BubbleWorldConfig;

public class CornMazeWaitingPhase {
	private final GameWorld gameWorld;
	private final CornMazeMap map;
	private final CornMazeConfig config;

	public CornMazeWaitingPhase(GameWorld gameWorld, CornMazeMap map, CornMazeConfig config) {
		this.gameWorld = gameWorld;
		this.map = map;
		this.config = config;
	}

	public static CompletableFuture<GameWorld> open(GameOpenContext<CornMazeConfig> context) {
		CornMazeMapBuilder mapBuilder = new CornMazeMapBuilder(context.getConfig());

		return mapBuilder.create().thenCompose(map -> {
			BubbleWorldConfig worldConfig = new BubbleWorldConfig()
				.setGenerator(map.createGenerator(context.getServer()))
				.setDefaultGameMode(GameMode.ADVENTURE);

			return context.openWorld(worldConfig).thenApply(gameWorld -> {
				CornMazeWaitingPhase phase = new CornMazeWaitingPhase(gameWorld, map, context.getConfig());

				return GameWaitingLobby.open(gameWorld, context.getConfig().getPlayerConfig(), game -> {
					CornMazeActivePhase.setRules(game);

					// Listeners
					game.on(PlayerAddListener.EVENT, phase::addPlayer);
					game.on(PlayerDeathListener.EVENT, phase::onPlayerDeath);
					game.on(GameTickListener.EVENT, phase::tick);
					game.on(OfferPlayerListener.EVENT, phase::offerPlayer);
					game.on(RequestStartListener.EVENT, phase::requestStart);
				});
			});
		});
	}

	private void tick() {
		for (ServerPlayerEntity player : this.gameWorld.getPlayers()) {
			if (!this.map.getStartBox().contains(player.getPos())) {
				CornMazeActivePhase.spawn(this.gameWorld.getWorld(), this.map, player);
			}
		}
	}

	private boolean isFull() {
		return this.gameWorld.getPlayerCount() >= this.config.getPlayerConfig().getMaxPlayers();
	}

	private JoinResult offerPlayer(ServerPlayerEntity player) {
		return this.isFull() ? JoinResult.gameFull() : JoinResult.ok();
	}

	private StartResult requestStart() {
		PlayerConfig playerConfig = this.config.getPlayerConfig();
		if (this.gameWorld.getPlayerCount() < playerConfig.getMinPlayers()) {
			return StartResult.NOT_ENOUGH_PLAYERS;
		}

		CornMazeActivePhase.open(this.gameWorld, this.map, this.config);
		return StartResult.OK;
	}

	private void addPlayer(ServerPlayerEntity player) {
		CornMazeActivePhase.spawn(this.gameWorld.getWorld(), this.map, player);
	}

	private ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
		CornMazeActivePhase.spawn(this.gameWorld.getWorld(), this.map, player);
		return ActionResult.FAIL;
	}
}