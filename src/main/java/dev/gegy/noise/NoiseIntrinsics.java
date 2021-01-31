package dev.gegy.noise;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public final class NoiseIntrinsics {
    private static final long MIX_MULTIPLIER = 6364136223846793005L;
    private static final long MIX_INCREMENT = 1442695040888963407L;

    public static long mixSeed(long seed, long salt) {
        seed *= seed * MIX_MULTIPLIER + MIX_INCREMENT;
        seed += salt;
        return seed;
    }

    public static class Visitor {
        private static final Type TYPE = Type.getType(NoiseIntrinsics.class);
        private static final String INTERNAL_NAME = TYPE.getInternalName();

        public static void mixSeed(MethodVisitor method) {
            method.visitMethodInsn(Opcodes.INVOKESTATIC, INTERNAL_NAME, "mixSeed", "(JJ)J", false);
        }
    }
}
