package dev.gegy.noise.compile;

import dev.gegy.noise.sampler.NoiseSampler;

public interface NoiseSampleCompiler<S extends NoiseSampler> {
    void compile(SamplerCompileContext<S> ctx, ValueRef[] coords);
}
