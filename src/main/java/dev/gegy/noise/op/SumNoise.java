package dev.gegy.noise.op;

import dev.gegy.noise.NoiseRange;
import dev.gegy.noise.TypedNoise;
import dev.gegy.noise.compile.SamplerCompileContext;
import dev.gegy.noise.sampler.NoiseSampler;
import dev.gegy.noise.sampler.NoiseSamplerType;

final class SumNoise<S extends NoiseSampler> extends MultiaryOpNoise<S> {
    SumNoise(TypedNoise<S>[] terms, NoiseSamplerType<S> type) {
        super(terms, type);
    }

    @Override
    protected void compileOperator(SamplerCompileContext<S> ctx) {
        ctx.method.visitInsn(ctx.dataType.getAddOpcode());
    }

    @Override
    public NoiseRange getRange() {
        NoiseRange range = null;
        for (TypedNoise<S> term : this.terms) {
            if (range == null) {
                range = term.getRange();
            } else {
                range = NoiseRange.add(range, term.getRange());
            }
        }
        return range;
    }
}
