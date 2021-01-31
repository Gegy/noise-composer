package dev.gegy.noise.sampler;

public interface NoiseSampler2d extends NoiseSampler2f, NoiseSampler1d {
    NoiseSamplerType<NoiseSampler2d> TYPE = new NoiseSamplerType<>(NoiseSampler2d.class, NoiseDimension.TWO, NoiseDataType.DOUBLE);

    @Override
    default double get(double x) {
        return this.get(x, 0.0);
    }

    @Override
    default float get(float x) {
        return (float) this.get((double) x);
    }

    @Override
    default float get(float x, float y) {
        return (float) this.get(x, (double) y);
    }

    double get(double x, double y);

    @Override
    default NoiseSamplerType<?> getType() {
        return TYPE;
    }
}
