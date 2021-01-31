package dev.gegy.noise;

public final class NoiseRange {
    public static final NoiseRange ZERO = new NoiseRange(0.0, 0.0);
    public static final NoiseRange ONE = new NoiseRange(1.0, 1.0);

    public static final NoiseRange NORMAL = new NoiseRange(-1.0, 1.0);

    public static final NoiseRange INFINITE = new NoiseRange(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

    public final double min;
    public final double max;

    private NoiseRange(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public static NoiseRange of(double a, double b) {
        return new NoiseRange(Math.min(a, b), Math.max(a, b));
    }

    public static NoiseRange of(double value) {
        return new NoiseRange(value, value);
    }

    public static NoiseRange add(NoiseRange left, NoiseRange right) {
        return new NoiseRange(left.min + right.min, left.max + right.max);
    }

    public static NoiseRange sub(NoiseRange left, NoiseRange right) {
        return new NoiseRange(left.min - right.min, left.max - right.max);
    }

    public static NoiseRange mul(NoiseRange left, NoiseRange right) {
        return of(left.min * right.min, left.max * right.max);
    }

    public static NoiseRange div(NoiseRange left, NoiseRange right) {
        return of(left.min / right.min, left.max / right.max);
    }

    public static NoiseRange pow(NoiseRange left, NoiseRange right) {
        return of(Math.pow(left.min, right.min), Math.pow(left.max, right.max));
    }

    public static NoiseRange abs(NoiseRange range) {
        return of(Math.abs(range.min), Math.abs(range.max));
    }

    public static NoiseRange negate(NoiseRange range) {
        return new NoiseRange(-range.max, -range.min);
    }

    public static NoiseRange reciprocal(NoiseRange range) {
        return new NoiseRange(1.0 / range.max, 1.0 / range.min);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj instanceof NoiseRange) {
            NoiseRange range = (NoiseRange) obj;
            return Math.abs(this.min - range.min) < 1e-4
                    && Math.abs(this.max - range.max) < 1e-4;
        }

        return false;
    }
}
