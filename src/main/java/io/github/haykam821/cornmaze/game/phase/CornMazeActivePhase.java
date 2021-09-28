package io.github.haykam821.cornmaze.game.phase;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.haykam821.cornmaze.game.CornMazeConfig;
import io.github.haykam821.cornmaze.game.map.CornMazeMap;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import xyz.nucleoid.plasmid.game.GameCloseReason;
import xyz.nucleoid.plasmid.game.GameLogic;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.event.GameOpenListener;
import xyz.nucleoid.plasmid.game.event.GameTickListener;
import xyz.nucleoid.plasmid.game.event.PlayerAddListener;
import xyz.nucleoid.plasmid.game.event.PlayerDeathListener;
import xyz.nucleoid.plasmid.game.event.PlayerRemoveListener;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;
import xyz.nucleoid.plasmid.util.PlayerRef;

public class CornMazeActivePhase {
	private static final DecimalFormat MINUTE_FORMAT = new DecimalFormat("#.#", new DecimalFormatSymbols(Locale.ROOT));

	private final ServerWorld world;
	private final GameSpace gameSpace;
	private final CornMazeMap map;
	private final CornMazeConfig config;
	private final Set<PlayerRef> players;
	private int ticks = 0;

	public CornMazeActivePhase(GameSpace gameSpace, CornMazeMap map, CornMazeConfig config, Set<PlayerRef> players) {
		this.world = gameSpace.getWorld();
		this.gameSpace = gameSpace;
		this.map = map;
		this.config = config;
		this.players = players;
	}

	public static void setRules(GameLogic game) {
		game.setRule(GameRule.BLOCK_DROPS, RuleResult.DENY);
		game.setRule(GameRule.CRAFTING, RuleResult.DENY);
		game.setRule(GameRule.FALL_DAMAGE, RuleResult.DENY);
		game.setRule(GameRule.HUNGER, RuleResult.DENY);
		game.setRule(GameRule.INTERACTION, RuleResult.DENY);
		game.setRule(GameRule.PORTALS, RuleResult.DENY);
		game.setRule(GameRule.PVP, RuleResult.DENY);
		game.setRule(GameRule.TEAM_CHAT, RuleResult.DENY);
		game.setRule(GameRule.THROW_ITEMS, RuleResult.DENY);
		game.setRule(GameRule.UNSTABLE_TNT, RuleResult.DENY);
	}

	public static void open(GameSpace gameSpace, CornMazeMap map, CornMazeConfig config) {
		Set<PlayerRef> players = gameSpace.getPlayers().stream().map(PlayerRef::of).collect(Collectors.toSet());
		CornMazeActivePhase phase = new CornMazeActivePhase(gameSpace, map, config, players);

		gameSpace.openGame(game -> {
			CornMazeActivePhase.setRules(game);

			// Listeners
			game.on(GameOpenListener.EVENT, phase::open);
			game.on(GameTickListener.EVENT, phase::tick);
			game.on(PlayerAddListener.EVENT, phase::addPlayer);
			game.on(PlayerRemoveListener.EVENT, phase::removePlayer);
			game.on(PlayerDeathListener.EVENT, phase::onPlayerDeath);
		});
	}

	private void open() {
 		for (PlayerRef playerRef : this.players) {
			playerRef.ifOnline(this.world, player -> {
				player.setGameMode(GameMode.ADVENTURE);
			});
		}
	}

	private void tick() {
		this.ticks += 1;

		for (PlayerRef playerRef : this.players) {
			playerRef.ifOnline(this.world, player -> {
				if (!this.map.getBox().contains(player.getPos())) {
					CornMazeActivePhase.spawn(this.world, this.map, player);
				} else if (this.map.getEndBox().contains(player.getPos())) {
					this.gameSpace.getPlayers().sendMessage(this.getWinMessage(player));
					gameSpace.close(GameCloseReason.FINISHED);
				}
			});
		}
	}

	private Text getWinMessage(ServerPlayerEntity winner) {
		return new TranslatableText("text.cornmaze.win", winner.getDisplayName(), MINUTE_FORMAT.format(this.ticks / 20d / 60d)).formatted(Formatting.GOLD);
	}

	private void addPlayer(ServerPlayerEntity player) {
		player.setGameMode(GameMode.SPECTATOR);
	}

	private void removePlayer(ServerPlayerEntity player) {
		this.players.remove(PlayerRef.of(player));
	}

	private ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
		CornMazeActivePhase.spawn(this.world, this.map, player);
		return ActionResult.FAIL;
	}

	public static void spawn(ServerWorld world, CornMazeMap map, ServerPlayerEntity player) {
		Vec3d center = map.getStartBox().getCenter();
		player.teleport(world, center.getX(), map.getBox().minY + 1, center.getZ(), 0, 0);
	}

	static {
		MINUTE_FORMAT.setRoundingMode(RoundingMode.UP);
	}
}