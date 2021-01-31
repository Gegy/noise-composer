package dev.gegy.noise.sampler;

public interface NoiseSampler5f extends NoiseSampler4f {
    NoiseSamplerType<NoiseSampler5f> TYPE = new NoiseSamplerType<>(NoiseSampler5f.class, NoiseDimension.FIVE, NoiseDataType.FLOAT);

    @Override
    default float get(float x, float y, float z, float w) {
        return this.get(x, y, z, w, 0.0F);
    }

    float get(float x, float y, float z, float w, float a);

    @Override
    default NoiseSamplerType<?> getType() {
        return TYPE;
    }
}
