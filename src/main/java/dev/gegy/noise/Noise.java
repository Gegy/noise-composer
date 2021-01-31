package dev.gegy.noise;

import dev.gegy.noise.op.NoiseOps;

public interface Noise {
    static Noise constant(double value) {
        return new ConstantNoise(value);
    }

    static Noise sum(Noise... terms) {
        return NoiseOps.sum(terms);
    }

    static Noise product(Noise... terms) {
        return NoiseOps.product(terms);
    }

    NoiseRange getRange();

    default Noise add(Noise right) {
        return NoiseOps.add(this, right);
    }

    default Noise sub(Noise right) {
        return NoiseOps.sub(this, right);
    }

    default Noise mul(Noise right) {
        return NoiseOps.mul(this, right);
    }

    default Noise div(Noise right) {
        return NoiseOps.div(this, right);
    }

    default Noise pow(Noise right) {
        return NoiseOps.pow(this, right);
    }

    default Noise add(double right) {
        return this.add(constant(right));
    }

    default Noise sub(double right) {
        return this.sub(constant(right));
    }

    default Noise mul(double right) {
        return this.mul(constant(right));
    }

    default Noise div(double right) {
        return this.mul(constant(1.0 / right));
    }

    default Noise pow(double right) {
        return this.pow(constant(right));
    }

    default Noise negate() {
        return NoiseOps.negate(this);
    }

    default Noise reciprocal() {
        return NoiseOps.reciprocal(this);
    }

    default Noise abs() {
        return NoiseOps.abs(this);
    }

    default Noise normalize() {
        return NoiseOps.normalize(this);
    }

    default Noise scale(double... scale) {
        return NoiseOps.scale(this, scale);
    }

    default Noise shift(double... shift) {
        return NoiseOps.shift(this, shift);
    }
}
