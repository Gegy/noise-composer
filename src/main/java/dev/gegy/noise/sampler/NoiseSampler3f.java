package dev.gegy.noise.sampler;

public interface NoiseSampler3f extends NoiseSampler2f {
    NoiseSamplerType<NoiseSampler3f> TYPE = new NoiseSamplerType<>(NoiseSampler3f.class, NoiseDimension.THREE, NoiseDataType.FLOAT);

    @Override
    default float get(float x, float y) {
        return this.get(x, y, 0.0F);
    }

    float get(float x, float y, float z);

    @Override
    default NoiseSamplerType<?> getType() {
        return TYPE;
    }
}
