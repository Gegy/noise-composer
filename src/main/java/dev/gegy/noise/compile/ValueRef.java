package dev.gegy.noise.compile;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.function.Consumer;

public interface ValueRef {
    static ValueRef lazy(Type type, Consumer<MethodVisitor> load) {
        return new ValueRef() {
            @Override
            public void load(MethodVisitor method) {
                load.accept(method);
            }

            @Override
            public Type getType() {
                return type;
            }
        };
    }

    void load(MethodVisitor method);

    Type getType();

    default LocalRef asLocal(MethodVisitor method, LocalAllocator locals) {
        return locals.copiedFrom(method, this);
    }

    default ValueRef map(Consumer<MethodVisitor> map) {
        return lazy(this.getType(), method -> {
            this.load(method);
            map.accept(method);
        });
    }

    interface Mutable extends ValueRef {
        void store(MethodVisitor method);
    }
}
