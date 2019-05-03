package com.example.mxaudiorecogition;

/**
 * An event point is a key point is a spectral representation of a signal. It is
 * defined by a time and frequency, using indexes, and it has an energy
 * (magnitude).
 *
 */
public class NFFTEventPoint {

    /**
     * The time expressed using an analysis frame index.
     */
    public int t;

    /**
     * The frequency expressed using the bin number in the FFT-transform.
     */
    public int f;

    public float contrast;

    /**
     * The frequency expressed in Hz.
     */
    public float frequencyEstimate;

    /**
     * Create a new event point with a time, frequency and energy and contrast..
     * 
     * @param t
     *            The time expressed using an analysis frame index.
     * @param f
     *            The frequency expressed using the bin number in the constant Q
     *            transform.
     * @param frequencyEstimate
     *            A more detailed estimate of the frequency in Hz (using phase
     *            information).
     * @param contrast
     *            How much contrast there is between this point and the surrounding
     *            environment
     */
    public NFFTEventPoint(int t, int f, float frequencyEstimate, float contrast) {
        this.t = t;
        this.f = f;
        this.contrast = contrast;
        this.frequencyEstimate = frequencyEstimate;
    }
}
