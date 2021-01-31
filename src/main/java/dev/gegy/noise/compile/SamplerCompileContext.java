package dev.gegy.noise.compile;

import dev.gegy.noise.sampler.NoiseDataType;
import dev.gegy.noise.sampler.NoiseDimension;
import dev.gegy.noise.sampler.NoiseSampler;
import org.objectweb.asm.MethodVisitor;

public final class SamplerCompileContext<S extends NoiseSampler> {
    public final MethodVisitor method;
    public final LocalAllocator locals;
    public final NoiseDimension dimension;
    public final NoiseDataType dataType;

    SamplerCompileContext(MethodVisitor method, LocalAllocator locals, NoiseDimension dimension, NoiseDataType dataType) {
        this.method = method;
        this.locals = locals;
        this.dimension = dimension;
        this.dataType = dataType;
    }
}
