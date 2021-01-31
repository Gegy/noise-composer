package dev.gegy.noise.op;

import dev.gegy.noise.NoiseIntrinsics;
import dev.gegy.noise.TypedNoise;
import dev.gegy.noise.compile.*;
import dev.gegy.noise.sampler.NoiseSampler;
import dev.gegy.noise.sampler.NoiseSamplerType;
import org.objectweb.asm.Opcodes;

abstract class BinaryOpNoise<S extends NoiseSampler> implements TypedNoise<S> {
    protected final TypedNoise<S> left;
    protected final TypedNoise<S> right;
    protected final NoiseSamplerType<S> type;

    protected BinaryOpNoise(TypedNoise<S> left, TypedNoise<S> right, NoiseSamplerType<S> type) {
        this.left = left;
        this.right = right;
        this.type = type;
    }

    @Override
    public NoiseSampleCompiler<S> compileInit(InitCompileContext<S> initCtx, ValueRef seed) {
        NoiseSampleCompiler<S> leftCompiler;
        NoiseSampleCompiler<S> rightCompiler;

        try (LocalRef seedLocal = seed.asLocal(initCtx.method, initCtx.locals)) {
            leftCompiler = this.left.compileInit(initCtx, seedLocal.map(m -> {
                m.visitInsn(Opcodes.LCONST_1);
                NoiseIntrinsics.Visitor.mixSeed(m);
            }));

            rightCompiler = this.right.compileInit(initCtx, seedLocal.map(m -> {
                m.visitLdcInsn(2L);
                NoiseIntrinsics.Visitor.mixSeed(m);
            }));
        }

        return (ctx, coords) -> {
            LocalRef[] localCoords = new LocalRef[coords.length];
            for (int i = 0; i < coords.length; i++) {
                localCoords[i] = coords[i].asLocal(ctx.method, ctx.locals);
            }

            try {
                this.compileBeforeParent(ctx);
                leftCompiler.compile(ctx, localCoords);
                this.compileAfterParent(ctx);

                this.compileBeforeParent(ctx);
                rightCompiler.compile(ctx, localCoords);
                this.compileAfterParent(ctx);

                this.compileOperator(ctx);
            } finally {
                for (LocalRef local : localCoords) {
                    local.close();
                }
            }
        };
    }

    protected void compileBeforeParent(SamplerCompileContext<S> ctx) {
    }

    protected void compileAfterParent(SamplerCompileContext<S> ctx) {
    }

    protected abstract void compileOperator(SamplerCompileContext<S> ctx);

    @Override
    public final NoiseSamplerType<S> getSamplerType() {
        return this.type;
    }
}
