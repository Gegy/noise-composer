package dev.gegy.noise;

import dev.gegy.noise.compile.InitCompileContext;
import dev.gegy.noise.compile.NoiseSampleCompiler;
import dev.gegy.noise.compile.ValueRef;
import dev.gegy.noise.sampler.NoiseDataType;
import dev.gegy.noise.sampler.NoiseSampler;
import dev.gegy.noise.sampler.NoiseSamplerType;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public final class DomainWrapNoise<S extends NoiseSampler> implements TypedNoise<S> {
    private final TypedNoise<S> parent;
    private final double size;

    DomainWrapNoise(TypedNoise<S> parent, double size) {
        this.parent = parent;
        this.size = size;
    }

    public static <S extends NoiseSampler> DomainWrapNoise<S> of(TypedNoise<S> parent, double size) {
        return new DomainWrapNoise<>(parent, size);
    }

    @Override
    public NoiseSampleCompiler<S> compileInit(InitCompileContext<S> initCtx, ValueRef seed) {
        NoiseSampleCompiler<S> parentCompiler = this.parent.compileInit(initCtx, seed);

        InitCompileContext.MethodRef wrapDouble = initCtx.defineStaticMethod("(D)D", method -> this.compileWrap(NoiseDataType.DOUBLE, method));
        InitCompileContext.MethodRef wrapFloat = initCtx.defineStaticMethod("(F)F", method -> this.compileWrap(NoiseDataType.FLOAT, method));

        return (ctx, coords) -> {
            InitCompileContext.MethodRef wrapMethod = ctx.dataType == NoiseDataType.DOUBLE ? wrapDouble : wrapFloat;

            ValueRef[] wrappedCoords = new ValueRef[coords.length];
            for (int i = 0; i < coords.length; i++) {
                wrappedCoords[i] = coords[i].map(wrapMethod::invoke);
            }

            parentCompiler.compile(ctx, wrappedCoords);
        };
    }

    private void compileWrap(NoiseDataType dataType, MethodVisitor method) {
        double size = this.size;
        double halfSize = size / 2.0;

        Label normal = new Label();
        Label positive = new Label();
        Label notPositive = new Label();
        Label negative = new Label();

        ValueRef coord = ValueRef.lazy(dataType.getType(), m -> {
            m.visitVarInsn(dataType.getLoadOpcode(), 0);
        });

        // if (x >= halfSize) positive else notPositive
        {
            coord.load(method);
            dataType.visitConstant(method, halfSize);
            method.visitInsn(dataType.getCmplOpcode());
            method.visitJumpInsn(Opcodes.IFLT, notPositive);
        }

        // x - (int) (x / size + 0.5) * size
        {
            method.visitLabel(positive);

            this.compileWrapToNearest(dataType, method, coord, dataType.getAddOpcode());
            method.visitInsn(dataType.getReturnOpcode());
        }

        // if (x <= -halfSize) negative else normal
        {
            method.visitLabel(notPositive);

            coord.load(method);
            dataType.visitConstant(method, -halfSize);
            method.visitInsn(dataType.getCmpgOpcode());
            method.visitJumpInsn(Opcodes.IFGT, normal);
        }

        // x - (int) (x / size - 0.5) * size
        {
            method.visitLabel(negative);

            this.compileWrapToNearest(dataType, method, coord, dataType.getSubOpcode());
            method.visitInsn(dataType.getReturnOpcode());
        }

        method.visitLabel(normal);
        coord.load(method);
        method.visitInsn(dataType.getReturnOpcode());
    }

    private void compileWrapToNearest(NoiseDataType dataType, MethodVisitor method, ValueRef coord, int roundOpcode) {
        coord.load(method);

        coord.load(method);
        dataType.visitConstant(method, this.size);
        method.visitInsn(dataType.getDivOpcode());

        dataType.visitConstant(method, 0.5);
        method.visitInsn(roundOpcode);

        method.visitInsn(dataType.getToIntOpcode());
        method.visitInsn(dataType.getFromIntOpcode());

        dataType.visitConstant(method, this.size);
        method.visitInsn(dataType.getMulOpcode());

        method.visitInsn(dataType.getSubOpcode());
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
