package dev.gegy.noise.sampler;

public interface NoiseSampler1d extends NoiseSampler1f {
    NoiseSamplerType<NoiseSampler1d> TYPE = new NoiseSamplerType<>(NoiseSampler1d.class, NoiseDimension.ONE, NoiseDataType.DOUBLE);

    @Override
    default float get(float x) {
        return (float) this.get((double) x);
    }

    double get(double x);

    @Override
    default NoiseSamplerType<?> getType() {
        return TYPE;
    }
}
