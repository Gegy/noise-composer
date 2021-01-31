package dev.gegy.noise;

import dev.gegy.noise.op.NoiseOps;

import java.util.ArrayList;
import java.util.List;

public final class OctaveNoise {
    private double amplitude = 1.0;
    private double scaleX = 1.0;
    private double scaleY = 1.0;
    private double scaleZ = 1.0;

    private double persistence = 1.0 / 2.0;
    private double lacunarity = 2.0;

    private final List<Noise> octaves = new ArrayList<>();

    OctaveNoise() {
    }

    public static OctaveNoise builder() {
        return new OctaveNoise();
    }

    public OctaveNoise setAmplitude(double amplitude) {
        this.amplitude = amplitude;
        return this;
    }

    public OctaveNoise setScaleX(double scaleX) {
        this.scaleX = scaleX;
        return this;
    }

    public OctaveNoise setScaleY(double scaleY) {
        this.scaleY = scaleY;
        return this;
    }

    public OctaveNoise setScaleZ(double scaleZ) {
        this.scaleZ = scaleZ;
        return this;
    }

    public OctaveNoise setScaleXZ(double scaleXZ) {
        this.scaleX = this.scaleZ = scaleXZ;
        return this;
    }

    public OctaveNoise setPersistence(double persistence) {
        this.persistence = persistence;
        return this;
    }

    public OctaveNoise setLacunarity(double lacunarity) {
        this.lacunarity = lacunarity;
        return this;
    }

    public OctaveNoise add(Noise octave) {
        this.octaves.add(octave);
        return this;
    }

    public OctaveNoise add(Noise octave, int count) {
        for (int i = 0; i < count; i++) {
            this.octaves.add(octave);
        }
        return this;
    }

    public Noise build() {
        double amplitude = this.amplitude;
        double scaleX = this.scaleX;
        double scaleY = this.scaleY;
        double scaleZ = this.scaleZ;

        List<Noise> octaves = this.octaves;
        Noise[] terms = new Noise[octaves.size()];

        for (int i = 0; i < octaves.size(); i++) {
            Noise octave = octaves.get(i);
            terms[i] =  octave
                    .mul(amplitude)
                    .scale(scaleX, scaleY, scaleZ);

            amplitude *= this.persistence;
            scaleX *= this.lacunarity;
            scaleY *= this.lacunarity;
            scaleZ *= this.lacunarity;
        }

        return NoiseOps.sum(terms);
    }
}
