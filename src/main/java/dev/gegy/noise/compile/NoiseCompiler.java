package dev.gegy.noise.compile;

import dev.gegy.noise.Noise;
import dev.gegy.noise.NoiseFactory;
import dev.gegy.noise.TypedNoise;
import dev.gegy.noise.sampler.NoiseSampler;
import dev.gegy.noise.sampler.NoiseSamplerType;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

public final class NoiseCompiler {
    private static final boolean DEBUG = false;
    private static final Path DEBUG_ROOT = Paths.get("debug");

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    private final ClassLoader parentClassLoader;

    private NoiseCompiler(ClassLoader parentClassLoader) {
        this.parentClassLoader = parentClassLoader;
    }

    public static NoiseCompiler create(ClassLoader parentClassLoader) {
        return new NoiseCompiler(parentClassLoader);
    }

    private static String nextClassName(Class<?> clazz) {
        return "Compiled" + clazz.getSimpleName() + COUNTER.incrementAndGet();
    }

    @SuppressWarnings("unchecked")
    public <S extends NoiseSampler> NoiseFactory<S> compile(Noise noise, NoiseSamplerType<S> samplerType) {
        String className = nextClassName(noise.getClass());

        TypedNoise<S> typedNoise = TypedNoise.cast(noise, samplerType);

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        writer.visit(
                Opcodes.V1_8,
                Opcodes.ACC_PUBLIC,
                className,
                null,
                Type.getInternalName(Object.class),
                new String[] { Type.getInternalName(samplerType.asClass()) }
        );

        Type selfType = Type.getObjectType(className);

        SamplerGenerator<S> generator = new SamplerGenerator<>(samplerType, writer, selfType);
        SamplerParameters parameters = generator.accept(typedNoise);

        byte[] bytes = writer.toByteArray();

        try {
            Class<?> definedClass = this.defineClass(className, bytes);
            return seed -> {
                try {
                    Constructor<?> constructor = definedClass.getDeclaredConstructor(long.class, SamplerParameters.class);
                    return (S) constructor.newInstance(seed, parameters);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException("Failed to construct noise sampler", e);
                }
            };
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to define feature on ClassLoader", e);
        }
    }

    private Class<?> defineClass(String className, byte[] bytes) throws ClassNotFoundException {
        if (DEBUG) {
            writeDebugClass(className, bytes);
        }

        ClassLoader classLoader = new ClassLoader(this.parentClassLoader) {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                if (name.equals(className)) {
                    return super.defineClass(name, bytes, 0, bytes.length);
                }
                return super.loadClass(name);
            }
        };

        return classLoader.loadClass(className);
    }

    private static void writeDebugClass(String className, byte[] bytes) {
        try {
            if (!Files.exists(DEBUG_ROOT)) {
                Files.createDirectories(DEBUG_ROOT);
            }

            Path path = DEBUG_ROOT.resolve(className + ".class");
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
