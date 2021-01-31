package dev.gegy.noise.sampler;

public interface NoiseSampler1f extends NoiseSampler {
    NoiseSamplerType<NoiseSampler1f> TYPE = new NoiseSamplerType<>(NoiseSampler1f.class, NoiseDimension.ONE, NoiseDataType.FLOAT);

    float get(float x);

    @Override
    default NoiseSamplerType<?> getType() {
        return TYPE;
    }
}
