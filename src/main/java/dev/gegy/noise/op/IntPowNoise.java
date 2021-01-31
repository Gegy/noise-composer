package dev.gegy.noise.op;

import dev.gegy.noise.NoiseRange;
import dev.gegy.noise.TypedNoise;
import dev.gegy.noise.compile.SamplerCompileContext;
import dev.gegy.noise.sampler.NoiseSampler;

final class IntPowNoise<S extends NoiseSampler> extends UnaryOpNoise<S> {
    private final int power;

    IntPowNoise(TypedNoise<S> parent, int power) {
        super(parent);
        this.power = power;
    }

    @Override
    protected void compileOperator(SamplerCompileContext<S> ctx) {
        for (int i = 0; i < this.power - 1; i++) {
            ctx.method.visitInsn(ctx.dataType.getDupOpcode());
        }

        for (int i = 0; i < this.power - 1; i++) {
            ctx.method.visitInsn(ctx.dataType.getMulOpcode());
        }
    }

    @Override
    public NoiseRange getRange() {
        NoiseRange range = this.parent.getRange();
        return NoiseRange.of(Math.pow(range.min, this.power), Math.pow(range.max, this.power));
    }
}
