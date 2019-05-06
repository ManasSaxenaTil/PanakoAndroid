package com.example.mxaudiorecogition;

import static java.util.concurrent.TimeUnit.SECONDS;
import static com.example.mxaudiorecogition.ClientUtils.enrichDecoderCommand;
import static com.example.mxaudiorecogition.ClientUtils.setPipeArtifacts;

import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import be.tarsos.dsp.io.PipeDecoder;
import be.tarsos.dsp.io.PipedAudioStream;
import be.tarsos.dsp.io.TarsosDSPAudioInputStream;

public class FingerprintGenerator {

    private String decoderCommand;

    private int decoderBufferSize;

    private int decoderTimeoutInSeconds;

    private int nfftSampleRate;

    private int nfftSize;

    private int nfftStepSize;

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

        public FingerprintGeneratorBuilder setNfftSampleRate(int nfftSampleRate) {
            fingerprintGenerator.nfftSampleRate = nfftSampleRate;
            return this;
        }

        public FingerprintGeneratorBuilder setNfftSize(int nfftSize) {
            fingerprintGenerator.nfftSize = nfftSize;
            return this;
        }

        public FingerprintGeneratorBuilder setNfftStepSize(int nfftStepSize) {
            fingerprintGenerator.nfftStepSize = nfftStepSize;
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
        int overlap = nfftSize - nfftStepSize;
        PipedAudioStream pipedAudioStream = new PipedAudioStream(resource);
        TarsosDSPAudioInputStream audioStream = pipedAudioStream.getMonoStream(nfftSampleRate, 0);
        TimedAudioDispatcher d = new TimedAudioDispatcher(audioStream, nfftSize, overlap);
        final NFFTEventPointProcessor minMaxProcessor = new NFFTEventPointProcessor(nfftSize, overlap, nfftSampleRate);
        d.addAudioProcessor(minMaxProcessor);
        d.run(decoderTimeoutInSeconds, SECONDS);
        // @formatter:off
        return minMaxProcessor
                   .getFingerprints()
                   .stream()
                   .map(f -> new FingerprintData(f.hash(), f.t1))
                   .collect(Collectors.toList());
        // @formatter:on
    }

}
