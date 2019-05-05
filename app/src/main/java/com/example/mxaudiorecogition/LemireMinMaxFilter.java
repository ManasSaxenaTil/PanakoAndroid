package com.example.mxaudiorecogition;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class LemireMinMaxFilter {

    private final int windowSize;
    private final Deque<Integer> maxFifo;
    private final Deque<Integer> minFifo;
    private final float[] maxVal;
    private final float[] minVal;
    private final float[] dataToFilter;

    private final boolean clampEdges;

    /**
     * Create a new min-max filter.
     * 
     * @param windowSize
     *            The size of the window, if clampEdges is true this should be odd.
     * @param dataLength
     *            The length of the data to filter
     * @param clampEdges
     *            If true the length of the resulting filtered values is as long as
     *            the data. Otherwise the length of the filtered values is
     *            <code>data.length - windowsize/2 + 1</code>. The data is extended
     *            with the values at the edges to make this possible.
     */
    public LemireMinMaxFilter(int windowSize, int dataLength, boolean clampEdges) {
        this.windowSize = windowSize;
        this.clampEdges = clampEdges;
        this.maxFifo = new ArrayDeque<>(windowSize);
        this.minFifo = new ArrayDeque<>(windowSize);
        if (clampEdges) {
            if (windowSize % 2 == 0) {
                throw new IllegalArgumentException("WindowSize should be odd when clamping edges, it is even.");
            }
            this.maxVal = new float[dataLength];
            this.minVal = new float[dataLength];
            this.dataToFilter = new float[dataLength + windowSize - 1];
        } else {
            this.maxVal = new float[dataLength - windowSize + 1];
            this.minVal = new float[dataLength - windowSize + 1];
            this.dataToFilter = null;
        }
    }

    public float[] getMinVal() {
        return minVal.clone();
    }

    public float[] getMaxVal() {
        return maxVal.clone();
    }

    /**
     * Run the filter. The resulting filtered data can requested by calling
     * getMaxVal() and getMinVal().
     * 
     * @param array
     *            the data to filter. It should have the same length as given in the
     *            constructor.
     */
    public void filter(float[] array) {

        if (clampEdges) {
            System.arraycopy(array, 0, dataToFilter, windowSize / 2, array.length);
            Arrays.fill(dataToFilter, 0, windowSize / 2, array[0]);
            Arrays.fill(dataToFilter, dataToFilter.length - windowSize / 2, dataToFilter.length,
                    array[array.length - 1]);
            array = dataToFilter;
        }

        // reuse fifo's to minimize memory use
        maxFifo.clear();
        minFifo.clear();

        maxFifo.addLast(0);
        minFifo.addLast(0);
        for (int i = 1; i < windowSize; ++i) {
            if (array[i] > array[i - 1]) { // overshoot
                maxFifo.removeLast();
                while (!maxFifo.isEmpty()) {
                    if (array[i] <= array[maxFifo.peekLast()]) {
                        break;
                    }
                    maxFifo.removeLast();
                }
            } else {
                minFifo.removeLast();
                while (!minFifo.isEmpty()) {
                    if (array[i] >= array[minFifo.peekLast()]) {
                        break;
                    }
                    minFifo.removeLast();
                }
            }
            maxFifo.addLast(i);
            minFifo.addLast(i);
        }

        for (int i = windowSize; i < array.length; ++i) {

            maxVal[i - windowSize] = array[maxFifo.peekFirst()];
            minVal[i - windowSize] = array[minFifo.peekFirst()];

            if (array[i] > array[i - 1]) {
                maxFifo.removeLast();
                while (!maxFifo.isEmpty()) {
                    if (array[i] <= array[maxFifo.peekLast()]) {
                        break;
                    }
                    maxFifo.removeLast();
                }
            } else {
                minFifo.removeLast();

                while (!minFifo.isEmpty()) {
                    if (array[i] >= array[minFifo.peekLast()]) {
                        break;
                    }
                    minFifo.removeLast();
                }
            }
            maxFifo.addLast(i);
            minFifo.addLast(i);
            if (i == windowSize + maxFifo.peekFirst()) {
                maxFifo.removeFirst();
            } else if (i == windowSize + minFifo.peekFirst()) {
                minFifo.removeFirst();
            }
        }
        maxVal[array.length - windowSize] = array[maxFifo.peekFirst()];
        minVal[array.length - windowSize] = array[minFifo.peekFirst()];
    }

}
