package dev.gegy.noise.op;

import dev.gegy.noise.NoiseRange;
import dev.gegy.noise.TypedNoise;
import dev.gegy.noise.compile.SamplerCompileContext;
import dev.gegy.noise.sampler.NoiseDataType;
import dev.gegy.noise.sampler.NoiseSampler;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

final class AbsNoise<S extends NoiseSampler> extends UnaryOpNoise<S> {
    AbsNoise(TypedNoise<S> parent) {
        super(parent);
    }

    @Override
    protected void compileOperator(SamplerCompileContext<S> ctx) {
        String descriptor = ctx.dataType == NoiseDataType.DOUBLE ? "(D)D" : "(F)F";
        ctx.method.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Math.class), "abs", descriptor, false);
    }

    @Override
    public NoiseRange getRange() {
        return NoiseRange.abs(this.parent.getRange());
    }
}
