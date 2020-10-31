package io.github.haykam821.cornmaze.game.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class CornMazeMapConfig {
	public static final Codec<CornMazeMapConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			Codec.INT.fieldOf("x").forGetter(CornMazeMapConfig::getX),
			Codec.INT.fieldOf("z").forGetter(CornMazeMapConfig::getZ),
			Codec.INT.optionalFieldOf("height", 8).forGetter(CornMazeMapConfig::getHeight),
			Codec.INT.optionalFieldOf("scale", 4).forGetter(CornMazeMapConfig::getScale)
		).apply(instance, CornMazeMapConfig::new);
	});

	private final int x;
	private final int z;
	private final int height;
	private final int scale;

	public CornMazeMapConfig(int x, int z, int height, int scale) {
		this.x = x % 2 == 0 ? x + 1 : x;
		this.z = z % 2 == 0 ? z + 1 : z;
		this.height = height;
		this.scale = scale;
	}

	public int getX() {
		return this.x;
	}

	public int getZ() {
		return this.z;
	}

	public int getHeight() {
		return this.height;
	}

	public int getScale() {
		return this.scale;
	}
}