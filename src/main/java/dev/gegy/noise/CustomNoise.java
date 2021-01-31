package dev.gegy.noise;

import dev.gegy.noise.compile.InitCompileContext;
import dev.gegy.noise.compile.NoiseSampleCompiler;
import dev.gegy.noise.compile.ValueRef;
import dev.gegy.noise.sampler.NoiseSampler;
import dev.gegy.noise.sampler.NoiseSamplerType;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public final class CustomNoise<S extends NoiseSampler> implements TypedNoise<S> {
    private final NoiseFactory<S> samplerFactory;
    private final NoiseSamplerType<S> samplerType;
    private final NoiseRange range;

    private CustomNoise(NoiseFactory<S> samplerFactory, NoiseSamplerType<S> samplerType, NoiseRange range) {
        this.samplerFactory = samplerFactory;
        this.samplerType = samplerType;
        this.range = range;
    }

    public static <S extends NoiseSampler> CustomNoise<S> of(NoiseFactory<S> samplerFactory, NoiseSamplerType<S> samplerType, NoiseRange range) {
        return new CustomNoise<>(samplerFactory, samplerType, range);
    }

    @Override
    public NoiseSampleCompiler<S> compileInit(InitCompileContext<S> initCtx, ValueRef seed) {
        Type samplerObjectType = Type.getType(this.samplerType.asClass());
        InitCompileContext.FieldRef field = initCtx.defineField(samplerObjectType, null);

        initCtx.method.visitVarInsn(Opcodes.ALOAD, 0);

        // pass the sampler factory into our generated type
        initCtx.loadReference(initCtx.method, this.samplerFactory, NoiseFactory.class);

        // call the factory
        seed.load(initCtx.method);
        initCtx.method.visitMethodInsn(
                Opcodes.INVOKEINTERFACE, Type.getInternalName(NoiseFactory.class),
                "create", "(J)" + Type.getDescriptor(NoiseSampler.class),
                true
        );

        // store our created sampler
        field.putValue(initCtx.method);

        return (ctx, coords) -> {
            ctx.method.visitVarInsn(Opcodes.ALOAD, 0);
            field.get(ctx.method);

            for (ValueRef coord : coords) {
                coord.load(ctx.method);
            }

            String descriptor = ctx.dimension.getDescriptor(ctx.dataType);
            ctx.method.visitMethodInsn(Opcodes.INVOKEINTERFACE, samplerObjectType.getInternalName(), "get", descriptor, true);
        };
    }

    @Override
    public NoiseSamplerType<S> getSamplerType() {
        return this.samplerType;
    }

    @Override
    public NoiseRange getRange() {
        return this.range;
    }
}
