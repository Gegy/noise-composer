package dev.gegy.noise.compile;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public final class LocalRef implements AutoCloseable, ValueRef.Mutable {
    final LocalAllocator allocator;
    final int index;
    final Type type;

    private int refCount = 1;

    LocalRef(LocalAllocator allocator, int index, Type type) {
        this.allocator = allocator;
        this.index = index;
        this.type = type;
    }

    @Override
    public void load(MethodVisitor method) {
        method.visitVarInsn(this.type.getOpcode(Opcodes.ILOAD), this.index);
    }

    @Override
    public void store(MethodVisitor method) {
        method.visitVarInsn(this.type.getOpcode(Opcodes.ISTORE), this.index);
    }

    @Override
    public Type getType() {
        return this.type;
    }

    @Override
    public LocalRef asLocal(MethodVisitor method, LocalAllocator locals) {
        this.refCount++;
        return this;
    }

    @Override
    public void close() {
        if (this.refCount == 0) {
            throw new IllegalStateException("local variable already dropped");
        }

        if (--this.refCount == 0) {
            this.allocator.release(this);
        }
    }
}
