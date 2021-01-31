package dev.gegy.noise.compile;

import dev.gegy.noise.TypedNoise;
import dev.gegy.noise.sampler.NoiseDataType;
import dev.gegy.noise.sampler.NoiseDimension;
import dev.gegy.noise.sampler.NoiseSampler;
import dev.gegy.noise.sampler.NoiseSamplerType;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

final class SamplerGenerator<S extends NoiseSampler> {
    private final NoiseSamplerType<S> samplerType;
    private final ClassWriter classWriter;
    private final Type selfType;

    private final SamplerParameters parameters = new SamplerParameters();

    SamplerGenerator(NoiseSamplerType<S> samplerType, ClassWriter classWriter, Type selfType) {
        this.samplerType = samplerType;
        this.classWriter = classWriter;
        this.selfType = selfType;
    }

    public SamplerParameters accept(TypedNoise<S> noise) {
        MethodVisitor method = this.classWriter.visitMethod(
                Opcodes.ACC_PUBLIC,
                "<init>",
                "(J" + Type.getDescriptor(SamplerParameters.class) + ")V",
                null,
                null
        );

        method.visitVarInsn(Opcodes.ALOAD, 0);
        method.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(Object.class), "<init>", "()V", false);

        LocalAllocator locals = LocalAllocator.forInstance(this.selfType);
        LocalRef seedLocal = locals.allocate(Type.LONG_TYPE);
        LocalRef parametersLocal = locals.allocate(Type.getType(SamplerParameters.class));

        InitCompileContext<S> ctx = new InitCompileContext<>(this.classWriter, this.selfType, method, locals, this.parameters, parametersLocal);

        NoiseSampleCompiler<S> samplerCompiler = noise.compileInit(ctx, seedLocal);
        this.acceptSampler(samplerCompiler);

        method.visitInsn(Opcodes.RETURN);

        // auto-compute
        method.visitMaxs(0, 0);

        return this.parameters;
    }

    private void acceptSampler(NoiseSampleCompiler<S> builder) {
        for (NoiseDimension dimension : NoiseDimension.DIMENSIONS) {
            this.acceptSampler(dimension, NoiseDataType.DOUBLE, builder);
            this.acceptSampler(dimension, NoiseDataType.FLOAT, builder);
        }
    }

    private void acceptSampler(NoiseDimension dimension, NoiseDataType dataType, NoiseSampleCompiler<S> builder) {
        if (!this.samplerType.supportsDimension(dimension) || !this.samplerType.supportsDataType(dataType)) {
            return;
        }

        MethodVisitor method = this.classWriter.visitMethod(
                Opcodes.ACC_PUBLIC,
                "get",
                dimension.getDescriptor(dataType),
                null,
                null
        );

        LocalAllocator locals = LocalAllocator.forInstance(this.selfType);

        ValueRef[] coords = new ValueRef[dimension.getSize()];
        for (int i = 0; i < coords.length; i++) {
            coords[i] = locals.allocate(dataType.getType());
        }

        SamplerCompileContext<S> ctx = new SamplerCompileContext<>(method, locals, dimension, dataType);
        builder.compile(ctx, coords);

        method.visitInsn(dataType.getReturnOpcode());

        // auto-compute
        method.visitMaxs(0, 0);
    }
}
