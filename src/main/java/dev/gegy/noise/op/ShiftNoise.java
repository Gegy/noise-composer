package dev.gegy.noise.op;

import dev.gegy.noise.NoiseRange;
import dev.gegy.noise.TypedNoise;
import dev.gegy.noise.compile.InitCompileContext;
import dev.gegy.noise.compile.NoiseSampleCompiler;
import dev.gegy.noise.compile.ValueRef;
import dev.gegy.noise.sampler.NoiseSampler;
import dev.gegy.noise.sampler.NoiseSamplerType;

final class ShiftNoise<S extends NoiseSampler> implements TypedNoise<S> {
    private final TypedNoise<S> parent;
    private final double[] shift;

    ShiftNoise(TypedNoise<S> parent, double[] shift) {
        this.parent = parent;
        this.shift = shift;
    }

    @Override
    public NoiseSampleCompiler<S> compileInit(InitCompileContext<S> initCtx, ValueRef seed) {
        NoiseSampleCompiler<S> parentCompiler = this.parent.compileInit(initCtx, seed);
        return (ctx, coords) -> {
            ValueRef[] shiftedCoords = new ValueRef[coords.length];
            for (int i = 0; i < coords.length; i++) {
                double shift = i < this.shift.length ? this.shift[i] : 0.0;
                if (shift == 0.0) {
                    shiftedCoords[i] = coords[i];
                } else {
                    shiftedCoords[i] = coords[i].map(m -> {
                        ctx.dataType.visitConstant(m, shift);
                        m.visitInsn(ctx.dataType.getAddOpcode());
                    });
                }
            }

            parentCompiler.compile(ctx, shiftedCoords);
        };
    }

    @Override
    public NoiseRange getRange() {
        return this.parent.getRange();
    }

    @Override
    public NoiseSamplerType<S> getSamplerType() {
        return this.parent.getSamplerType();
    }
}
