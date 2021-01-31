package dev.gegy.noise.op;

import dev.gegy.noise.TypedNoise;
import dev.gegy.noise.compile.InitCompileContext;
import dev.gegy.noise.compile.NoiseSampleCompiler;
import dev.gegy.noise.compile.SamplerCompileContext;
import dev.gegy.noise.compile.ValueRef;
import dev.gegy.noise.sampler.NoiseSampler;
import dev.gegy.noise.sampler.NoiseSamplerType;

abstract class UnaryOpNoise<S extends NoiseSampler> implements TypedNoise<S> {
    protected final TypedNoise<S> parent;

    protected UnaryOpNoise(TypedNoise<S> parent) {
        this.parent = parent;
    }

    @Override
    public NoiseSampleCompiler<S> compileInit(InitCompileContext<S> initCtx, ValueRef seed) {
        NoiseSampleCompiler<S> parentCompiler = this.parent.compileInit(initCtx, seed);
        return (ctx, coords) -> {
            this.compileBeforeOperator(ctx);
            parentCompiler.compile(ctx, coords);
            this.compileOperator(ctx);
        };
    }

    protected void compileBeforeOperator(SamplerCompileContext<S> ctx) {
    }

    protected abstract void compileOperator(SamplerCompileContext<S> ctx);

    @Override
    public final NoiseSamplerType<S> getSamplerType() {
        return this.parent.getSamplerType();
    }
}
