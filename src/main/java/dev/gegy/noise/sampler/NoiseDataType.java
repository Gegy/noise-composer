package dev.gegy.noise.sampler;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public final class NoiseDataType {
    public static final NoiseDataType FLOAT = new NoiseDataType(Type.FLOAT_TYPE);
    public static final NoiseDataType DOUBLE = new NoiseDataType(Type.DOUBLE_TYPE);

    private final Type type;

    private NoiseDataType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    public int getLoadOpcode() {
        return this.type.getOpcode(Opcodes.ILOAD);
    }

    public int getStoreOpcode() {
        return this.type.getOpcode(Opcodes.ISTORE);
    }

    public int getReturnOpcode() {
        return this.type.getOpcode(Opcodes.IRETURN);
    }

    public int getAddOpcode() {
        return this.type.getOpcode(Opcodes.IADD);
    }

    public int getSubOpcode() {
        return this.type.getOpcode(Opcodes.ISUB);
    }

    public int getMulOpcode() {
        return this.type.getOpcode(Opcodes.IMUL);
    }

    public int getNegOpcode() {
        return this.type.getOpcode(Opcodes.INEG);
    }

    public int getDivOpcode() {
        return this.type.getOpcode(Opcodes.IDIV);
    }

    public void visitConstant(MethodVisitor method, double constant) {
        if (this == NoiseDataType.DOUBLE) {
            if (constant == 0.0) {
                method.visitInsn(Opcodes.DCONST_0);
            } else if (constant == 1.0) {
                method.visitInsn(Opcodes.DCONST_1);
            } else {
                method.visitLdcInsn(constant);
            }
        } else {
            if (constant == 0.0) {
                method.visitInsn(Opcodes.FCONST_0);
            } else if (constant == 1.0) {
                method.visitInsn(Opcodes.FCONST_1);
            } else if (constant == 2.0) {
                method.visitInsn(Opcodes.FCONST_2);
            } else {
                method.visitLdcInsn((float) constant);
            }
        }
    }
}
