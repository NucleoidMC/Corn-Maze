package io.github.haykam821.cornmaze.game;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.haykam821.cornmaze.game.map.CornMazeMapConfig;
import net.minecraft.SharedConstants;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import xyz.nucleoid.plasmid.game.common.config.PlayerConfig;

public class CornMazeConfig {
	public static final Codec<CornMazeConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			CornMazeMapConfig.CODEC.fieldOf("map").forGetter(CornMazeConfig::getMapConfig),
			PlayerConfig.CODEC.fieldOf("players").forGetter(CornMazeConfig::getPlayerConfig),
			IntProvider.NON_NEGATIVE_CODEC.optionalFieldOf("ticks_until_close", ConstantIntProvider.create(SharedConstants.TICKS_PER_SECOND * 5)).forGetter(CornMazeConfig::getTicksUntilClose)
		).apply(instance, CornMazeConfig::new);
	});

	private final CornMazeMapConfig mapConfig;
	private final PlayerConfig playerConfig;
	private final IntProvider ticksUntilClose;

	public CornMazeConfig(CornMazeMapConfig mapConfig, PlayerConfig playerConfig, IntProvider ticksUntilClose) {
		this.mapConfig = mapConfig;
		this.playerConfig = playerConfig;
		this.ticksUntilClose = ticksUntilClose;
	}

	public CornMazeMapConfig getMapConfig() {
		return this.mapConfig;
	}

	public PlayerConfig getPlayerConfig() {
		return this.playerConfig;
	}

	public IntProvider getTicksUntilClose() {
		return this.ticksUntilClose;
	}
}