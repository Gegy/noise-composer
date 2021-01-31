package dev.gegy.noise.compile;

import dev.gegy.noise.sampler.NoiseSampler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.function.Consumer;

public final class InitCompileContext<S extends NoiseSampler> {
    public final ClassWriter classWriter;
    public final Type selfType;
    public final MethodVisitor method;
    public final LocalAllocator locals;

    private final SamplerParameters parameters;
    private final ValueRef parametersRef;

    private int fieldIndex;
    private int methodIndex;

    InitCompileContext(ClassWriter classWriter, Type selfType, MethodVisitor method, LocalAllocator locals, SamplerParameters parameters, ValueRef parametersRef) {
        this.classWriter = classWriter;
        this.selfType = selfType;
        this.method = method;
        this.locals = locals;
        this.parameters = parameters;
        this.parametersRef = parametersRef;
    }

    public FieldRef defineField(Type type, Object value) {
        String name = "field$" + (++this.fieldIndex);
        String descriptor = type.getDescriptor();

        this.classWriter.visitField(Opcodes.ACC_PRIVATE, name, descriptor, null, value);

        return new FieldRef(this.selfType, name, descriptor);
    }

    public MethodRef defineMethod(String descriptor, Consumer<MethodVisitor> body) {
        return this.defineMethod(descriptor, body, Opcodes.ACC_PRIVATE, Opcodes.INVOKEVIRTUAL);
    }

    public MethodRef defineStaticMethod(String descriptor, Consumer<MethodVisitor> body) {
        return this.defineMethod(descriptor, body, Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, Opcodes.INVOKESTATIC);
    }

    private MethodRef defineMethod(String descriptor, Consumer<MethodVisitor> body, int access, int opcode) {
        String name = "method$" + (++this.methodIndex);

        MethodVisitor method = this.classWriter.visitMethod(access, name, descriptor, null, null);
        body.accept(method);
        method.visitMaxs(0, 0);

        return new MethodRef(this.selfType, name, descriptor, opcode);
    }

    public <T> void loadReference(MethodVisitor method, T value, Class<T> type) {
        int index = this.parameters.add(value);

        this.parametersRef.load(method);
        SamplerParameters.visitGet(method, Type.getType(type), index);
    }

    public static final class FieldRef {
        public final Type owner;
        public final String name;
        public final String descriptor;

        FieldRef(Type owner, String name, String descriptor) {
            this.owner = owner;
            this.name = name;
            this.descriptor = descriptor;
        }

        public void putValue(MethodVisitor method) {
            method.visitFieldInsn(Opcodes.PUTFIELD, this.owner.getInternalName(), this.name, this.descriptor);
        }

        public void get(MethodVisitor method) {
            method.visitFieldInsn(Opcodes.GETFIELD, this.owner.getInternalName(), this.name, this.descriptor);
        }
    }

    public static final class MethodRef {
        public final Type owner;
        public final String name;
        public final String descriptor;
        public final int opcode;

        MethodRef(Type owner, String name, String descriptor, int opcode) {
            this.owner = owner;
            this.name = name;
            this.descriptor = descriptor;
            this.opcode = opcode;
        }

        public void invoke(MethodVisitor method) {
            method.visitMethodInsn(this.opcode, this.owner.getInternalName(), this.name, this.descriptor, false);
        }
    }
}
