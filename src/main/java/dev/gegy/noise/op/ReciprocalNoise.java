package dev.gegy.noise.op;

import dev.gegy.noise.NoiseRange;
import dev.gegy.noise.TypedNoise;
import dev.gegy.noise.compile.SamplerCompileContext;
import dev.gegy.noise.sampler.NoiseSampler;

final class ReciprocalNoise<S extends NoiseSampler> extends UnaryOpNoise<S> {
    ReciprocalNoise(TypedNoise<S> parent) {
        super(parent);
    }

    @Override
    protected void compileBeforeOperator(SamplerCompileContext<S> ctx) {
        ctx.dataType.visitConstant(ctx.method, 1.0);
    }

    @Override
    protected void compileOperator(SamplerCompileContext<S> ctx) {
        ctx.method.visitInsn(ctx.dataType.getDivOpcode());
    }

    @Override
    public NoiseRange getRange() {
        return NoiseRange.reciprocal(this.parent.getRange());
    }
}
