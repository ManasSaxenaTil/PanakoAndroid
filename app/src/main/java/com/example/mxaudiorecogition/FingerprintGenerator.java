package com.example.mxaudiorecogition;

import static java.util.concurrent.TimeUnit.SECONDS;
import static com.example.mxaudiorecogition.ClientUtils.enrichDecoderCommand;
import static com.example.mxaudiorecogition.ClientUtils.setPipeArtifacts;
import static com.example.mxaudiorecogition.FingerprintData.createData;


import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import be.tarsos.dsp.ConstantQ;
import be.tarsos.dsp.io.PipeDecoder;
import be.tarsos.dsp.io.PipedAudioStream;
import be.tarsos.dsp.io.TarsosDSPAudioInputStream;

public class FingerprintGenerator {

    private String decoderCommand;

    private int decoderBufferSize;

    private int decoderTimeoutInSeconds;

    private int sampleRate;

    private int stepSize;

    private int maxEventPointDeltaFrequency;

    private int binsPerOctave;

    private int minFrequencyInCents;

    private int maxFrequencyInCents;

    private FingerprintGenerator() {
        super();
    }

    public static FingerprintGeneratorBuilder newBuilder() {
        return new FingerprintGeneratorBuilder();
    }

    public static class FingerprintGeneratorBuilder implements Builder<FingerprintGenerator> {

        private FingerprintGenerator fingerprintGenerator;

        public FingerprintGeneratorBuilder() {
            super();
            fingerprintGenerator = new FingerprintGenerator();
        }

        public FingerprintGeneratorBuilder setDecoderCommand(String decoderCommand) {
            fingerprintGenerator.decoderCommand = decoderCommand;
            return this;
        }

        public FingerprintGeneratorBuilder setDecoderBufferSize(int decoderBufferSize) {
            fingerprintGenerator.decoderBufferSize = decoderBufferSize;
            return this;
        }

        public FingerprintGeneratorBuilder setDecoderTimeoutInSeconds(int decoderTimeoutInSeconds) {
            fingerprintGenerator.decoderTimeoutInSeconds = decoderTimeoutInSeconds;
            return this;
        }

        public FingerprintGeneratorBuilder setSampleRate(int sampleRate) {
            fingerprintGenerator.sampleRate = sampleRate;
            return this;
        }

        public FingerprintGeneratorBuilder setStepSize(int stepSize) {
            fingerprintGenerator.stepSize = stepSize;
            return this;
        }

        public FingerprintGeneratorBuilder setMaxEventPointDeltaFrequency(int maxEventPointDeltaFrequency) {
            fingerprintGenerator.maxEventPointDeltaFrequency = maxEventPointDeltaFrequency;
            return this;
        }

        public FingerprintGeneratorBuilder setBinsPerOctave(int binsPerOctave) {
            fingerprintGenerator.binsPerOctave = binsPerOctave;
            return this;
        }

        public FingerprintGeneratorBuilder setMinFrequencyInCents(int minFrequencyInCents) {
            fingerprintGenerator.minFrequencyInCents = minFrequencyInCents;
            return this;
        }

        public FingerprintGeneratorBuilder setMaxFrequencyInCents(int maxFrequencyInCents) {
            fingerprintGenerator.maxFrequencyInCents = maxFrequencyInCents;
            return this;
        }

        @Override
        public FingerprintGenerator build() {
            setPipedDecoder();
            return fingerprintGenerator;
        }

        private void setPipedDecoder() {
            String[] artifacts = setPipeArtifacts();
            String pipeEnvironment = artifacts[0];
            String pipeArgument = artifacts[1];
            String decoderCommand = enrichDecoderCommand(fingerprintGenerator.decoderCommand);
            if (pipeEnvironment != null) {
                // @formatter:off
                PipeDecoder decoder = new PipeDecoder(pipeEnvironment, 
                                                      pipeArgument, 
                                                      decoderCommand, 
                                                      null,
                                                      fingerprintGenerator.decoderBufferSize);
                // @formatter:on
                PipedAudioStream.setDecoder(decoder);
            }
        }

    }

    public List<FingerprintData> getFingerprints(String resource) throws TimeoutException {
        ConstantQ constantQ = createConstantQ();
        int size = constantQ.getFFTlength();
        int overlap = size - stepSize;
        PipedAudioStream pipedAudioStream = new PipedAudioStream(resource);
        TarsosDSPAudioInputStream audioStream = pipedAudioStream.getMonoStream(sampleRate, 0);
        TimedAudioDispatcher d = new TimedAudioDispatcher(audioStream, size, overlap);
        // @formatter:off
        final NCteQEventPointProcessor minMaxProcessor = new NCteQEventPointProcessor(
                                                                constantQ, 
                                                                sampleRate, 
                                                                stepSize,
                                                                maxEventPointDeltaFrequency);
        d.addAudioProcessor(minMaxProcessor);
        d.run(decoderTimeoutInSeconds, SECONDS);

        return minMaxProcessor
                   .getFingerprints()
                   .stream()
                   .map(f -> createData(f.hash(), f.t1, f.timeDelta(), f.f1))
                   .collect(Collectors.toList());
        // @formatter:on
    }

    private ConstantQ createConstantQ() {
        float minFreqInHerz = (float) PitchUnit.HERTZ.convert(minFrequencyInCents, PitchUnit.ABSOLUTE_CENTS);
        float maxFreqInHertz = (float) PitchUnit.HERTZ.convert(maxFrequencyInCents, PitchUnit.ABSOLUTE_CENTS);
        return new ConstantQ(sampleRate, minFreqInHerz, maxFreqInHertz, binsPerOctave);
    }

}
