package dev.gegy.noise.op;

import dev.gegy.noise.NoiseRange;
import dev.gegy.noise.TypedNoise;
import dev.gegy.noise.compile.SamplerCompileContext;
import dev.gegy.noise.sampler.NoiseDataType;
import dev.gegy.noise.sampler.NoiseSampler;
import dev.gegy.noise.sampler.NoiseSamplerType;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

final class PowNoise<S extends NoiseSampler> extends BinaryOpNoise<S> {
    PowNoise(TypedNoise<S> left, TypedNoise<S> right, NoiseSamplerType<S> type) {
        super(left, right, type);
    }

    @Override
    protected void compileAfterParent(SamplerCompileContext<S> ctx) {
        if (ctx.dataType == NoiseDataType.FLOAT) {
            ctx.method.visitInsn(Opcodes.F2D);
        }
    }

    @Override
    protected void compileOperator(SamplerCompileContext<S> ctx) {
        ctx.method.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Math.class), "pow", "(DD)D", false);

        if (ctx.dataType == NoiseDataType.FLOAT) {
            ctx.method.visitInsn(Opcodes.D2F);
        }
    }

    @Override
    public NoiseRange getRange() {
        return NoiseRange.pow(this.left.getRange(), this.right.getRange());
    }
}
