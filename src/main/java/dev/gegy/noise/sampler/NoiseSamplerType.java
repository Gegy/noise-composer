package dev.gegy.noise.sampler;

public final class NoiseSamplerType<S extends NoiseSampler> {
    private final Class<S> clazz;
    private final NoiseDimension dimension;
    private final NoiseDataType dataType;

    public NoiseSamplerType(Class<S> clazz, NoiseDimension dimension, NoiseDataType dataType) {
        this.clazz = clazz;
        this.dimension = dimension;
        this.dataType = dataType;
    }

    public Class<S> asClass() {
        return this.clazz;
    }

    public NoiseDimension getDimension() {
        return this.dimension;
    }

    public NoiseDataType getDataType() {
        return this.dataType;
    }

    public boolean greaterOrEqual(NoiseSamplerType<?> other) {
        return other.clazz.isAssignableFrom(this.clazz);
    }

    public boolean supportsDimension(NoiseDimension dimension) {
        return this.dimension.greaterOrEqual(dimension);
    }

    public boolean supportsDataType(NoiseDataType dataType) {
        if (dataType == NoiseDataType.DOUBLE) {
            return this.dataType == dataType;
        } else {
            return true;
        }
    }
}
