package dev.gegy.noise.op;

import dev.gegy.noise.NoiseIntrinsics;
import dev.gegy.noise.TypedNoise;
import dev.gegy.noise.compile.*;
import dev.gegy.noise.sampler.NoiseSampler;
import dev.gegy.noise.sampler.NoiseSamplerType;

abstract class MultiaryOpNoise<S extends NoiseSampler> implements TypedNoise<S> {
    protected final TypedNoise<S>[] terms;
    protected final NoiseSamplerType<S> type;

    MultiaryOpNoise(TypedNoise<S>[] terms, NoiseSamplerType<S> type) {
        this.terms = terms;
        this.type = type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public NoiseSampleCompiler<S> compileInit(InitCompileContext<S> initCtx, ValueRef seed) {
        NoiseSampleCompiler<S>[] termCompilers = new NoiseSampleCompiler[this.terms.length];

        try (LocalRef seedLocal = seed.asLocal(initCtx.method, initCtx.locals)) {
            for (int i = 0; i < this.terms.length; i++) {
                long salt = i;
                termCompilers[i] = this.terms[i].compileInit(initCtx, seedLocal.map(m -> {
                    m.visitLdcInsn(salt);
                    NoiseIntrinsics.Visitor.mixSeed(m);
                }));
            }
        }

        return (ctx, coords) -> {
            LocalRef[] localCoords = new LocalRef[coords.length];
            for (int i = 0; i < coords.length; i++) {
                localCoords[i] = coords[i].asLocal(ctx.method, ctx.locals);
            }

            try {
                for (int i = 0; i < termCompilers.length; i++) {
                    NoiseSampleCompiler<S> term = termCompilers[i];
                    term.compile(ctx, localCoords);
                    if (i > 0) {
                        this.compileOperator(ctx);
                    }
                }
            } finally {
                for (LocalRef local : localCoords) {
                    local.close();
                }
            }
        };
    }

    protected abstract void compileOperator(SamplerCompileContext<S> ctx);

    @Override
    public final NoiseSamplerType<S> getSamplerType() {
        return this.type;
    }
}
