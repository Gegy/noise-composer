package dev.gegy.noise.op;

import dev.gegy.noise.NoiseRange;
import dev.gegy.noise.TypedNoise;
import dev.gegy.noise.compile.SamplerCompileContext;
import dev.gegy.noise.sampler.NoiseSampler;

final class NegateNoise<S extends NoiseSampler> extends UnaryOpNoise<S> {
    NegateNoise(TypedNoise<S> parent) {
        super(parent);
    }

    @Override
    protected void compileOperator(SamplerCompileContext<S> ctx) {
        ctx.method.visitInsn(ctx.dataType.getNegOpcode());
    }

    @Override
    public NoiseRange getRange() {
        return NoiseRange.negate(this.parent.getRange());
    }
}
