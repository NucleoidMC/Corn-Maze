package io.github.haykam821.cornmaze.game;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.haykam821.cornmaze.game.map.CornMazeMapConfig;
import xyz.nucleoid.plasmid.game.config.PlayerConfig;

public class CornMazeConfig {
	public static final Codec<CornMazeConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			CornMazeMapConfig.CODEC.fieldOf("map").forGetter(CornMazeConfig::getMapConfig),
			PlayerConfig.CODEC.fieldOf("players").forGetter(CornMazeConfig::getPlayerConfig)
		).apply(instance, CornMazeConfig::new);
	});

	private final CornMazeMapConfig mapConfig;
	private final PlayerConfig playerConfig;

	public CornMazeConfig(CornMazeMapConfig mapConfig, PlayerConfig playerConfig) {
		this.mapConfig = mapConfig;
		this.playerConfig = playerConfig;
	}

	public CornMazeMapConfig getMapConfig() {
		return this.mapConfig;
	}

	public PlayerConfig getPlayerConfig() {
		return this.playerConfig;
	}
}