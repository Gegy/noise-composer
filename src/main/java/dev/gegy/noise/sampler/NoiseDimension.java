package dev.gegy.noise.sampler;

public final class NoiseDimension {
    public static final NoiseDimension ONE = new NoiseDimension(1);
    public static final NoiseDimension TWO = new NoiseDimension(2);
    public static final NoiseDimension THREE = new NoiseDimension(3);
    public static final NoiseDimension FOUR = new NoiseDimension(4);
    public static final NoiseDimension FIVE = new NoiseDimension(5);

    public static final NoiseDimension[] DIMENSIONS = new NoiseDimension[] { ONE, TWO, THREE, FOUR, FIVE };

    private final int size;

    private NoiseDimension(int size) {
        this.size = size;
    }

    public int getSize() {
        return this.size;
    }

    public boolean greaterOrEqual(NoiseDimension dimension) {
        return this.size >= dimension.size;
    }

    public String getDescriptor(NoiseDataType dataType) {
        String descriptor = dataType.getType().getDescriptor();

        StringBuilder result = new StringBuilder();
        result.append("(");
        for (int i = 0; i < this.size; i++) {
            result.append(descriptor);
        }
        result.append(")").append(descriptor);

        return result.toString();
    }
}
