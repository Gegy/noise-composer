package dev.gegy.noise.sampler;

public interface NoiseSampler5d extends NoiseSampler5f, NoiseSampler4d {
    NoiseSamplerType<NoiseSampler5d> TYPE = new NoiseSamplerType<>(NoiseSampler5d.class, NoiseDimension.FIVE, NoiseDataType.DOUBLE);

    @Override
    default double get(double x, double y, double z, double w) {
        return this.get(x, y, z, w, 0.0);
    }

    @Override
    default float get(float x, float y, float z, float w) {
        return (float) this.get(x, y, z, (double) w);
    }

    @Override
    default float get(float x, float y, float z, float w, float a) {
        return (float) this.get(x, y, z, w, (double) a);
    }

    double get(double x, double y, double z, double w, double a);

    @Override
    default NoiseSamplerType<?> getType() {
        return TYPE;
    }
}
