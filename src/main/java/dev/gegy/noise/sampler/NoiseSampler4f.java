package dev.gegy.noise.sampler;

public interface NoiseSampler4f extends NoiseSampler3f {
    NoiseSamplerType<NoiseSampler4f> TYPE = new NoiseSamplerType<>(NoiseSampler4f.class, NoiseDimension.FOUR, NoiseDataType.FLOAT);

    @Override
    default float get(float x, float y, float z) {
        return this.get(x, y, z, 0.0F);
    }

    float get(float x, float y, float z, float w);

    @Override
    default NoiseSamplerType<?> getType() {
        return TYPE;
    }
}
