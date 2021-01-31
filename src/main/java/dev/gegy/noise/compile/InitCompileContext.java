package dev.gegy.noise.compile;

import dev.gegy.noise.sampler.NoiseSampler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public final class InitCompileContext<S extends NoiseSampler> {
    public final ClassWriter classWriter;
    public final Type selfType;
    public final MethodVisitor method;
    public final LocalAllocator locals;

    private final SamplerParameters parameters;
    private final ValueRef parametersRef;

    private int fieldIndex;

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
}
