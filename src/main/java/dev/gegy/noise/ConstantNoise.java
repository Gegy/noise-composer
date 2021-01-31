package dev.gegy.noise;

import dev.gegy.noise.compile.InitCompileContext;
import dev.gegy.noise.compile.NoiseSampleCompiler;
import dev.gegy.noise.compile.ValueRef;
import dev.gegy.noise.op.NoiseOps;
import dev.gegy.noise.sampler.NoiseSampler5d;
import dev.gegy.noise.sampler.NoiseSamplerType;

final class ConstantNoise implements TypedNoise<NoiseSampler5d> {
    private final double value;

    ConstantNoise(double value) {
        this.value = value;
    }

    @Override
    public NoiseSampleCompiler<NoiseSampler5d> compileInit(InitCompileContext<NoiseSampler5d> initCtx, ValueRef seed) {
        return (ctx, coords) -> {
            ctx.dataType.visitConstant(ctx.method, this.value);
        };
    }

    @Override
    public NoiseRange getRange() {
        return NoiseRange.of(this.value);
    }

    @Override
    public Noise add(Noise right) {
        if (right instanceof ConstantNoise) {
            return this.add(((ConstantNoise) right).value);
        }
        return NoiseOps.add(this, right);
    }

    @Override
    public Noise sub(Noise right) {
        if (right instanceof ConstantNoise) {
            return this.sub(((ConstantNoise) right).value);
        }
        return NoiseOps.sub(this, right);
    }

    @Override
    public Noise mul(Noise right) {
        if (right instanceof ConstantNoise) {
            return this.mul(((ConstantNoise) right).value);
        }
        return NoiseOps.mul(this, right);
    }

    @Override
    public Noise div(Noise right) {
        if (right instanceof ConstantNoise) {
            return this.div(((ConstantNoise) right).value);
        }
        return NoiseOps.div(this, right);
    }

    @Override
    public Noise pow(Noise right) {
        if (right instanceof ConstantNoise) {
            return this.pow(((ConstantNoise) right).value);
        }
        return NoiseOps.pow(this, right);
    }

    @Override
    public Noise add(double right) {
        return new ConstantNoise(this.value + right);
    }

    @Override
    public Noise sub(double right) {
        return new ConstantNoise(this.value - right);
    }

    @Override
    public Noise mul(double right) {
        return new ConstantNoise(this.value * right);
    }

    @Override
    public Noise div(double right) {
        return new ConstantNoise(this.value / right);
    }

    @Override
    public Noise pow(double right) {
        return new ConstantNoise(Math.pow(this.value, right));
    }

    @Override
    public Noise negate() {
        return new ConstantNoise(-this.value);
    }

    @Override
    public Noise reciprocal() {
        return new ConstantNoise(1.0 / this.value);
    }

    @Override
    public Noise abs() {
        return new ConstantNoise(Math.abs(this.value));
    }

    @Override
    public Noise scale(double... scale) {
        return this;
    }

    @Override
    public Noise shift(double... shift) {
        return this;
    }

    @Override
    public NoiseSamplerType<NoiseSampler5d> getSamplerType() {
        return NoiseSampler5d.TYPE;
    }
}
