package com.example.mxaudiorecogition;


import be.tarsos.dsp.util.PitchConverter;

/**
 * A fingerprint connects two event points in a spectrogram. The points are
 * defined by a time and frequency pair, both encoded with an integer. The
 * frequency is defined by the bin index in the spectrogram. The time is defined
 * as the index of the block processed.
 * 
 * @author Joren Six
 */
public class NFFTFingerprint {

    public final int t1;
    public final int f1;
    public final double f1Estimate;

    public final int t2;
    public final int f2;
    public final double f2Estimate;

    public NFFTEventPoint p1, p2;

    private boolean hashWithFrequencyEstimate = Config.getBoolean(Key.NFFT_USE_PHASE_REFINED_HASH);

    public double energy;

    public NFFTFingerprint(int t1, int f1, float f1Estimate, int t2, int f2, float f2Estimate) {
        this.t1 = t1;
        this.f1 = f1;

        this.t2 = t2;
        this.f2 = f2;

        if (f1Estimate == 0 || f2Estimate == 0) {
            hashWithFrequencyEstimate = false;
        }

        if (hashWithFrequencyEstimate) {
            this.f1Estimate = PitchConverter.hertzToAbsoluteCent(f1Estimate);
            this.f2Estimate = PitchConverter.hertzToAbsoluteCent(f2Estimate);
        } else {
            this.f1Estimate = 0.0;
            this.f2Estimate = 0.0;
        }

        assert t2 > t1;
    }

    public NFFTFingerprint(NFFTEventPoint l1, NFFTEventPoint l2) {
        this(l1.t, l1.f, l1.frequencyEstimate, l2.t, l2.f, l2.frequencyEstimate);
        p1 = l1;
        p2 = l2;
    }

    /**
     * Calculate a hash representing this fingerprint.
     * 
     * @return a hash representing this fingerprint.
     */
    public int hash() {
        final int hash;

        if (hashWithFrequencyEstimate) {
            // 8 bits for the exact location of the frequency component
            int f = f1 & ((1 << 8) - 1);
            // 8 bits for the frequency delta (not fully used?)
            int deltaF = Math.abs(f2 - f1);
            deltaF = deltaF & ((1 << 8) - 1);
            // 6 bits for the time difference
            int deltaT = Math.abs(timeDelta()) & ((1 << 7) - 1);
            // In total the hash contains 8 + 8 + 6 bits == 22 bits (about 4 million values)
            int binHash = (f << 15) + (deltaF << 7) + deltaT;

            int deltaFInCents = (int) Math.round(Math.abs(f2Estimate - f1Estimate) / 7.0);
            binHash = binHash | deltaFInCents;
            if (f1 > f2) {
                hash = binHash * -1;
            } else {
                hash = binHash;
            }
        } else {
            // 8 bits for the exact location of the frequency component
            int f = f1 & ((1 << 8) - 1);
            // 8 bits for the frequency delta (not fully used?)
            int deltaF = Math.abs(f2 - f1);
            deltaF = deltaF & ((1 << 8) - 1);
            // 6 bits for the time difference
            int deltaT = Math.abs(timeDelta()) & ((1 << 7) - 1);
            // In total the hash contains 8 + 8 + 6 bits == 22 bits (about 4 million values)
            int binHash = (f << 15) + (deltaF << 7) + deltaT;
            if (f1 > f2) {
                hash = binHash * -1;
            } else {
                hash = binHash;
            }
        }
        return hash;
    }

    public String toString() {
        return String.format("%d,%d,%d,%d,%d", t1, f1, t2, f2, hash());
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (!(other instanceof NFFTFingerprint)) {
            return false;
        }
        NFFTFingerprint otherFingerprint = (NFFTFingerprint) other;
        boolean sameHash = otherFingerprint.hash() == this.hash();
        /*
         * if closer than 100 analysis frames (of e.g. 32ms), than hash is deemed the
         * same).
         */
        boolean closeInTime = Math.abs(otherFingerprint.t1 - this.t1) < 100;
        return sameHash && closeInTime;
    }

    /*
     * This is not completely consistent with the expected hash code / equals
     * behavior: It is very well possible that that two hashes collide, while the
     * fingerprints are not equal to each other. Implementing hash code makes sure
     * no identical fingerprints are added, but also that no collisions are allowed.
     * Take care when using sets.
     */
    public int hashCode() {
        return hash();
    }

    /**
     * The time delta between the first and last event.
     * 
     * @return The difference between t1 and t2, in analysis frames.
     */
    public int timeDelta() {
        return t2 - t1;
    }
}
