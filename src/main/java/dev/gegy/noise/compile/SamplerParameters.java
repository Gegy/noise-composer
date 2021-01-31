package dev.gegy.noise.compile;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

public final class SamplerParameters {
    private final List<Object> values = new ArrayList<>();

    public int add(Object value) {
        this.values.add(value);
        return this.values.size() - 1;
    }

    public Object get(int index) {
        return this.values.get(index);
    }

    public static void visitGet(MethodVisitor method, Type type, int index) {
        method.visitIntInsn(Opcodes.SIPUSH, index);

        method.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(SamplerParameters.class), "get", "(I)Ljava/lang/Object;", false);
        method.visitTypeInsn(Opcodes.CHECKCAST, type.getInternalName());
    }
}
