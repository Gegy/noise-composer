package dev.gegy.noise.op;

import dev.gegy.noise.NoiseRange;
import dev.gegy.noise.TypedNoise;
import dev.gegy.noise.compile.SamplerCompileContext;
import dev.gegy.noise.sampler.NoiseSampler;
import dev.gegy.noise.sampler.NoiseSamplerType;

final class MulNoise<S extends NoiseSampler> extends BinaryOpNoise<S> {
    MulNoise(TypedNoise<S> left, TypedNoise<S> right, NoiseSamplerType<S> type) {
        super(left, right, type);
    }

    @Override
    protected void compileOperator(SamplerCompileContext<S> ctx) {
        ctx.method.visitInsn(ctx.dataType.getMulOpcode());
    }

    @Override
    public NoiseRange getRange() {
        return NoiseRange.mul(this.left.getRange(), this.right.getRange());
    }
}
