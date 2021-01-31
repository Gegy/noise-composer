package dev.gegy.noise;

import dev.gegy.noise.compile.InitCompileContext;
import dev.gegy.noise.compile.NoiseSampleCompiler;
import dev.gegy.noise.compile.ValueRef;
import dev.gegy.noise.sampler.NoiseSampler;
import dev.gegy.noise.sampler.NoiseSamplerType;

import java.util.function.Function;

public interface TypedNoise<S extends NoiseSampler> extends Noise {
    static <S extends NoiseSampler> TypedNoise<S> cast(Noise noise, NoiseSamplerType<S> castType) {
        TypedNoise<S> typedNoise = coerceUnchecked(noise);
        NoiseSamplerType<S> type = typedNoise.getSamplerType();
        if (type.greaterOrEqual(castType)) {
            return typedNoise;
        } else {
            throw new ClassCastException("noise of type " + type + " cannot be cast to" + castType);
        }
    }

    @SuppressWarnings("unchecked")
    static <S extends NoiseSampler> TypedNoise<S> coerceUnchecked(Noise noise) {
        if (noise instanceof TypedNoise) {
            return (TypedNoise<S>) noise;
        } else {
            throw new IllegalArgumentException("illegal noise implementation! " + noise + " does not implement TypedNoise");
        }
    }

    NoiseSampleCompiler<S> compileInit(InitCompileContext<S> initCtx, ValueRef seed);

    NoiseSamplerType<S> getSamplerType();

    static <S extends NoiseSampler, T extends TypedNoise<S>> T createUnary(Noise parent, Function<TypedNoise<S>, T> create) {
        TypedNoise<S> parentTyped = coerceUnchecked(parent);
        return create.apply(parentTyped);
    }

    @SuppressWarnings("unchecked")
    static <S extends NoiseSampler, T extends TypedNoise<S>> T createBinary(
            Noise left, Noise right,
            BinaryFactory<S, T> factory
    ) {
        TypedNoise<?> leftTyped = coerceUnchecked(left);
        TypedNoise<?> rightTyped = coerceUnchecked(right);

        NoiseSamplerType<?> leftType = leftTyped.getSamplerType();
        NoiseSamplerType<?> rightType = rightTyped.getSamplerType();

        NoiseSamplerType<S> resultType;
        if (leftType.greaterOrEqual(rightType)) {
            resultType = (NoiseSamplerType<S>) leftType;
        } else if (rightType.greaterOrEqual(leftType)) {
            resultType = (NoiseSamplerType<S>) rightType;
        } else {
            throw new ClassCastException("incompatible noise types " + leftType + " + " + rightType);
        }

        return factory.create(coerceUnchecked(left), coerceUnchecked(right), resultType);
    }

    @SuppressWarnings("unchecked")
    static <S extends NoiseSampler, T extends TypedNoise<S>> T createMultiary(
            Noise[] terms,
            MultiaryFactory<S, T> factory
    ) {
        TypedNoise<S>[] typedTerms = new TypedNoise[terms.length];
        for (int i = 0; i < terms.length; i++) {
            typedTerms[i] = coerceUnchecked(terms[i]);
        }

        NoiseSamplerType<S> resultType = null;
        for (int i = 0; i < terms.length; i++) {
            TypedNoise<S> typedTerm = coerceUnchecked(terms[i]);
            typedTerms[i] = typedTerm;

            NoiseSamplerType<S> termType = typedTerm.getSamplerType();
            if (resultType == null) {
                resultType = termType;
            } else if (resultType.greaterOrEqual(termType)) {
                resultType = termType;
            } else if (!termType.greaterOrEqual(resultType)) {
                throw new ClassCastException("incompatible noise types " + resultType + " + " + termType);
            }
        }

        return factory.create(typedTerms, resultType);
    }

    interface BinaryFactory<S extends NoiseSampler, T> {
        T create(TypedNoise<S> left, TypedNoise<S> right, NoiseSamplerType<S> type);
    }

    interface MultiaryFactory<S extends NoiseSampler, T> {
        T create(TypedNoise<S>[] terms, NoiseSamplerType<S> type);
    }
}
