package dev.gegy.noise;

import dev.gegy.noise.sampler.NoiseSampler;

public interface NoiseFactory<S extends NoiseSampler> {
    S create(long seed);
}
