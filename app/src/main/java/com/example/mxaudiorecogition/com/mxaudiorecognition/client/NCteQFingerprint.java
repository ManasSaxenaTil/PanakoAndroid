package com.example.mxaudiorecogition.com.mxaudiorecognition.client;

/**
 * A fingerprint connects three event points in a spectrogram. The points are
 * defined by a time and frequency pair, both encoded with an integer. The
 * frequency is defined by the bin index in the spectrogram. The time is defined
 * as the index of the block processed.
 */
public class NCteQFingerprint {

    public final int t1;
    public final int f1;

    public final int t2;
    public final int f2;

    public final int t3;
    public final int f3;

    private final NCteQHashFunction hashFunction;

    public NCteQFingerprint(int t1, int f1, int t2, int f2, int t3, int f3) {
        this.t1 = t1;
        this.f1 = f1;

        this.t2 = t2;
        this.f2 = f2;

        this.t3 = t3;
        this.f3 = f3;

        this.hashFunction = new NCteQHashFunction();

        assert t2 > t1;
        assert t3 > t2;
    }

    public NCteQFingerprint(NCteQEventPoint l1, NCteQEventPoint l2, NCteQEventPoint l3) {
        this(l1.t, l1.f, l2.t, l2.f, l3.t, l3.f);
    }

    /**
     * Calculate a hash representing this fingerprint.
     *
     * @return a hash representing this fingerprint.
     */
    public int hash() {
        return hashFunction.calculateHash(this);
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (!(other instanceof NCteQFingerprint)) {
            return false;
        }
        NCteQFingerprint otherFingerprint = (NCteQFingerprint) other;
        boolean sameHash = otherFingerprint.hash() == this.hash();
        /*
         * if closer than 100 analysis frames (of e.g. 32ms), then hash is deemed the
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
     * The time delta between the first and last event is max 2.4 seconds. In
     * analysis frames of 1024 samples at 44100Hz, this is 2.4 * 44100/1024 = 104
     * max (1.2 seconds is 52 per steps).
     *
     * [0.96,1.44] to [41.3,62.01]
     *
     * @return The difference between t1 and t3, in analysis frames.
     */
    public int timeDelta() {
        return t3 - t1;
    }
}