package com.example.mxaudiorecogition;

/**
 * An event point is a key point is a spectral representation of a signal. It is
 * defined by a time and frequency, using indexes, and it has an energy
 * (magnitude).
 *
 */
public class NCteQEventPoint {

    /**
     * The time expressed using an analysis frame index.
     */
    public int t;

    /**
     * The frequency expressed using the bin number in the constant Q transform.
     */
    public int f;

    /**
     * The energy value of the element.
     */
    public final float energy;

    /**
     * Gives an indication how much the energy of the event point contrasts with its
     * 8 immediate neighbors.
     */
    public final float contrast;

    /**
     * Create a new event point with a time, frequency and energy and contrast..
     * 
     * @param t
     *            The time expressed using an analysis frame index.
     * @param f
     *            The frequency expressed using the bin number in the constant Q
     *            transform.
     * @param energy
     *            The energy value of the element.
     * @param contrast
     *            How much contrast there is between this point and the surrounding
     *            environment
     */
    public NCteQEventPoint(int t, int f, float energy, float contrast) {
        this.t = t;
        this.f = f;
        this.energy = energy;
        this.contrast = contrast;
    }
}