package dev.gegy.noise.sampler;

public interface NoiseSampler2f extends NoiseSampler1f {
    NoiseSamplerType<NoiseSampler2f> TYPE = new NoiseSamplerType<>(NoiseSampler2f.class, NoiseDimension.TWO, NoiseDataType.FLOAT);

    @Override
    default float get(float x) {
        return this.get(x, 0.0F);
    }

    float get(float x, float y);

    @Override
    default NoiseSamplerType<?> getType() {
        return TYPE;
    }
}
