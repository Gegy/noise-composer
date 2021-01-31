package dev.gegy.noise.sampler;

public interface NoiseSampler4d extends NoiseSampler4f, NoiseSampler3d {
    NoiseSamplerType<NoiseSampler4d> TYPE = new NoiseSamplerType<>(NoiseSampler4d.class, NoiseDimension.FOUR, NoiseDataType.DOUBLE);

    @Override
    default double get(double x, double y, double z) {
        return this.get(x, y, z, 0.0);
    }

    @Override
    default float get(float x, float y, float z) {
        return (float) this.get(x, y, (double) z);
    }

    @Override
    default float get(float x, float y, float z, float w) {
        return (float) this.get(x, y, z, (double) w);
    }

    double get(double x, double y, double z, double w);

    @Override
    default NoiseSamplerType<?> getType() {
        return TYPE;
    }
}
