package dev.gegy.noise.op;

import dev.gegy.noise.NoiseRange;
import dev.gegy.noise.TypedNoise;
import dev.gegy.noise.compile.InitCompileContext;
import dev.gegy.noise.compile.NoiseSampleCompiler;
import dev.gegy.noise.compile.ValueRef;
import dev.gegy.noise.sampler.NoiseSampler;
import dev.gegy.noise.sampler.NoiseSamplerType;

final class ScaleNoise<S extends NoiseSampler> implements TypedNoise<S> {
    private final TypedNoise<S> parent;
    private final double[] scale;

    ScaleNoise(TypedNoise<S> parent, double[] scale) {
        this.parent = parent;
        this.scale = scale;
    }

    @Override
    public NoiseSampleCompiler<S> compileInit(InitCompileContext<S> initCtx, ValueRef seed) {
        NoiseSampleCompiler<S> parentCompiler = this.parent.compileInit(initCtx, seed);

        return (ctx, coords) -> {
            ValueRef[] scaledCoords = new ValueRef[coords.length];
            for (int i = 0; i < coords.length; i++) {
                double scale = i < this.scale.length ? this.scale[i] : 1.0;
                if (scale == 0.0) {
                    scaledCoords[i] = ValueRef.lazy(ctx.dataType.getType(), m -> ctx.dataType.visitConstant(m, 0.0));
                } else if (scale == 1.0) {
                    scaledCoords[i] = coords[i];
                } else {
                    scaledCoords[i] = coords[i].map(m -> {
                        ctx.dataType.visitConstant(m, scale);
                        m.visitInsn(ctx.dataType.getMulOpcode());
                    });
                }
            }

            parentCompiler.compile(ctx, scaledCoords);
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
