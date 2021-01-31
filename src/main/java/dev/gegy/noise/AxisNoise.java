package dev.gegy.noise;

import dev.gegy.noise.compile.InitCompileContext;
import dev.gegy.noise.compile.NoiseSampleCompiler;
import dev.gegy.noise.compile.ValueRef;
import dev.gegy.noise.sampler.NoiseSampler5d;
import dev.gegy.noise.sampler.NoiseSamplerType;

public final class AxisNoise implements TypedNoise<NoiseSampler5d> {
    private final int axis;

    private AxisNoise(int axis) {
        this.axis = axis;
    }

    public static Noise x() {
        return new AxisNoise(0);
    }

    public static Noise y() {
        return new AxisNoise(1);
    }

    public static Noise z() {
        return new AxisNoise(2);
    }

    public static Noise w() {
        return new AxisNoise(3);
    }

    public static Noise a() {
        return new AxisNoise(4);
    }

    @Override
    public NoiseSampleCompiler<NoiseSampler5d> compileInit(InitCompileContext<NoiseSampler5d> initCtx, ValueRef seed) {
        return (ctx, coords) -> {
            if (this.axis < coords.length) {
                ValueRef coord = coords[this.axis];
                coord.load(ctx.method);
            } else {
                ctx.dataType.visitConstant(ctx.method, 0.0);
            }
        };
    }

    @Override
    public NoiseRange getRange() {
        return NoiseRange.INFINITE;
    }

    @Override
    public NoiseSamplerType<NoiseSampler5d> getSamplerType() {
        return NoiseSampler5d.TYPE;
    }
}
