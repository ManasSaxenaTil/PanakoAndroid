package com.example.mxaudiorecogition;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.PipeDecoder;
import be.tarsos.dsp.io.PipedAudioStream;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;

public class FingerprintGenerator {

    private String decoderEnvironment;

    private String decoderArg;

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

        public FingerprintGeneratorBuilder setDecoderEnvironment(String decoderEnvironment) {
            fingerprintGenerator.decoderEnvironment = decoderEnvironment;
            return this;
        }

        public FingerprintGeneratorBuilder setDecoderArg(String decoderArg) {
            fingerprintGenerator.decoderArg = decoderArg;
            return this;
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
            // @formatter:off
            PipeDecoder decoder = new PipeDecoder(fingerprintGenerator.decoderEnvironment, 
                                                  fingerprintGenerator.decoderArg, 
                                                  fingerprintGenerator.decoderCommand, 
                                                  null,
                                                  fingerprintGenerator.decoderBufferSize);
            // @formatter:on
            PipedAudioStream.setDecoder(decoder);
            return fingerprintGenerator;
        }

    }

    public List<NFFTFingerprint> getFingerprints(String resource) {
        int overlap = nfftSize - nfftStepSize;
        //CompletableFuture<List<NFFTFingerprint>> fingerprintsAsync = CompletableFuture.supplyAsync(() -> {
            AudioDispatcher d = AudioDispatcherFactory.fromPipe(resource, nfftSampleRate, nfftSize, overlap);
            final NFFTEventPointProcessor minMaxProcessor = new NFFTEventPointProcessor(nfftSize, overlap,
                    nfftSampleRate);
            d.addAudioProcessor(minMaxProcessor);
            d.run();
            return minMaxProcessor.getFingerprints();
      //  });
        /*try {
            return fingerprintsAsync.get(decoderTimeoutInSeconds, SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            Log.e("Timeout reached while generating fingerprints for resource: " + resource);
            return new ArrayList<>();
        }*/
    }

}
