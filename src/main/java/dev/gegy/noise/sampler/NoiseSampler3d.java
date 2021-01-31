package dev.gegy.noise.sampler;

public interface NoiseSampler3d extends NoiseSampler3f, NoiseSampler2d {
    NoiseSamplerType<NoiseSampler3d> TYPE = new NoiseSamplerType<>(NoiseSampler3d.class, NoiseDimension.THREE, NoiseDataType.DOUBLE);

    @Override
    default double get(double x, double y) {
        return this.get(x, y, 0.0);
    }

    @Override
    default float get(float x, float y) {
        return (float) this.get(x, (double) y);
    }

    @Override
    default float get(float x, float y, float z) {
        return (float) this.get(x, y, (double) z);
    }

    double get(double x, double y, double z);

    @Override
    default NoiseSamplerType<?> getType() {
        return TYPE;
    }
}
