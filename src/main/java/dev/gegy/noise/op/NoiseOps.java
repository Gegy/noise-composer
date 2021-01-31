package dev.gegy.noise.op;

import dev.gegy.noise.Noise;
import dev.gegy.noise.NoiseRange;
import dev.gegy.noise.TypedNoise;

public final class NoiseOps {
    private NoiseOps() {
    }

    public static Noise add(Noise left, Noise right) {
        if (left.getRange().equals(NoiseRange.ZERO)) {
            return right;
        } else if (right.getRange().equals(NoiseRange.ZERO)) {
            return left;
        }
        return TypedNoise.createBinary(left, right, AddNoise::new);
    }

    public static Noise sum(Noise... terms) {
        return TypedNoise.createMultiary(terms, SumNoise::new);
    }

    public static Noise sub(Noise left, Noise right) {
        if (right.getRange().equals(NoiseRange.ZERO)) {
            return left;
        }
        return TypedNoise.createBinary(left, right, SubNoise::new);
    }

    public static Noise mul(Noise left, Noise right) {
        NoiseRange leftRange = left.getRange();
        NoiseRange rightRange = right.getRange();

        if (leftRange.equals(NoiseRange.ONE)) {
            return right;
        } else if (rightRange.equals(NoiseRange.ONE)) {
            return left;
        }

        if (leftRange.equals(NoiseRange.ZERO) || rightRange.equals(NoiseRange.ZERO)) {
            return Noise.constant(0.0);
        }

        return TypedNoise.createBinary(left, right, MulNoise::new);
    }

    public static Noise product(Noise... terms) {
        return TypedNoise.createMultiary(terms, ProductNoise::new);
    }

    public static Noise div(Noise left, Noise right) {
        NoiseRange leftRange = left.getRange();
        NoiseRange rightRange = right.getRange();

        if (rightRange.equals(NoiseRange.ONE)) {
            return left;
        } else if (rightRange.equals(NoiseRange.ZERO)) {
            return Noise.constant(Double.NaN);
        }

        if (leftRange.equals(NoiseRange.ZERO)) {
            return Noise.constant(0.0);
        }

        return TypedNoise.createBinary(left, right, DivNoise::new);
    }

    public static Noise pow(Noise left, Noise right) {
        NoiseRange leftRange = left.getRange();
        NoiseRange rightRange = right.getRange();

        if (leftRange.equals(NoiseRange.ONE)) {
            return Noise.constant(1.0);
        } else if (rightRange.equals(NoiseRange.ONE)) {
            return left;
        }

        return TypedNoise.createBinary(left, right, PowNoise::new);
    }

    public static Noise abs(Noise parent) {
        NoiseRange range = parent.getRange();
        if (range.min < 0.0) {
            return TypedNoise.createUnary(parent, AbsNoise::new);
        } else {
            return parent;
        }
    }

    public static Noise negate(Noise parent) {
        return TypedNoise.createUnary(parent, NegateNoise::new);
    }

    public static Noise reciprocal(Noise parent) {
        return TypedNoise.createUnary(parent, ReciprocalNoise::new);
    }

    public static Noise scale(Noise parent, double... scale) {
        if (isArrayOnly(scale, 1.0)) {
            return parent;
        }
        return TypedNoise.createUnary(parent, typed -> new ScaleNoise<>(typed, scale));
    }

    public static Noise shift(Noise parent, double... shift) {
        if (isArrayOnly(shift, 0.0)) {
            return parent;
        }
        return TypedNoise.createUnary(parent, typed -> new ShiftNoise<>(typed, shift));
    }

    public static Noise normalize(Noise parent) {
        NoiseRange range = parent.getRange();
        if (NoiseRange.NORMAL.equals(range)) {
            return parent;
        }

        double min = range.min;
        double max = range.max;
        double width = max - min;

        double radius = width / 2.0;
        double offset = min + radius;
        return parent.sub(offset).div(radius);
    }

    private static boolean isArrayOnly(double[] array, double value) {
        for (double v : array) {
            if (v != value) {
                return false;
            }
        }
        return true;
    }
}
