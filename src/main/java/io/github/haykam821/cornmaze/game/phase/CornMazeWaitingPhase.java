package io.github.haykam821.cornmaze.game.phase;

import io.github.haykam821.cornmaze.game.CornMazeConfig;
import io.github.haykam821.cornmaze.game.map.CornMazeMap;
import io.github.haykam821.cornmaze.game.map.CornMazeMapBuilder;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.world.GameMode;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.plasmid.game.GameOpenContext;
import xyz.nucleoid.plasmid.game.GameOpenProcedure;
import xyz.nucleoid.plasmid.game.GameResult;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.common.GameWaitingLobby;
import xyz.nucleoid.plasmid.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.game.player.PlayerOffer;
import xyz.nucleoid.plasmid.game.player.PlayerOfferResult;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;

public class CornMazeWaitingPhase {
	private final GameSpace gameSpace;
	private final ServerWorld world;
	private final CornMazeMap map;
	private final CornMazeConfig config;

	public CornMazeWaitingPhase(GameSpace gameSpace, ServerWorld world, CornMazeMap map, CornMazeConfig config) {
		this.gameSpace = gameSpace;
		this.world = world;
		this.map = map;
		this.config = config;
	}

	public static GameOpenProcedure open(GameOpenContext<CornMazeConfig> context) {
		CornMazeMapBuilder mapBuilder = new CornMazeMapBuilder(context.config());
		CornMazeMap map = mapBuilder.create();

		RuntimeWorldConfig worldConfig = new RuntimeWorldConfig()
			.setGenerator(map.createGenerator(context.server()));

		return context.openWithWorld(worldConfig, (activity, world) -> {
			CornMazeWaitingPhase phase = new CornMazeWaitingPhase(activity.getGameSpace(), world, map, context.config());

			GameWaitingLobby.addTo(activity, context.config().getPlayerConfig());
			CornMazeActivePhase.setRules(activity);

			// Listeners
			activity.listen(PlayerDeathEvent.EVENT, phase::onPlayerDeath);
			activity.listen(GameActivityEvents.TICK, phase::tick);
			activity.listen(GamePlayerEvents.OFFER, phase::offerPlayer);
			activity.listen(GameActivityEvents.REQUEST_START, phase::requestStart);
		});
	}

	private void tick() {
		for (ServerPlayerEntity player : this.gameSpace.getPlayers()) {
			if (!this.map.getStartBox().contains(player.getPos())) {
				this.map.spawn(player, this.world);
			}
		}
	}

	private PlayerOfferResult offerPlayer(PlayerOffer offer) {
		return this.map.acceptOffer(offer, this.world, GameMode.ADVENTURE);
	}

	private GameResult requestStart() {
		CornMazeActivePhase.open(this.gameSpace, this.world, this.map, this.config);
		return GameResult.ok();
	}

	private ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
		this.map.spawn(player, this.world);
		return ActionResult.FAIL;
	}
}