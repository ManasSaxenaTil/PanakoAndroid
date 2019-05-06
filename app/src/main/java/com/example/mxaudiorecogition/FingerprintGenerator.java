package com.example.mxaudiorecogition;

import android.text.TextUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
            String pipeEnvironment = null;
            String pipeArgument = null;
            if (System.getProperty("os.name").indexOf("indows") > 0) {
                pipeEnvironment = "cmd.exe";
                pipeArgument = "/C";
            } else if (new File("/bin/bash").exists()) {
                pipeEnvironment = "/bin/bash";
                pipeArgument = "-c";
            } else if (new File("/system/bin/sh").exists()) {
                pipeEnvironment = "/system/bin/sh";
                pipeArgument = "-c";
            }

            String var5 = System.getProperty("java.io.tmpdir");
            String decoderBinaryAbsolutePath = fingerprintGenerator.decoderCommand;
            File var4 = new File(var5, "ffmpeg");
            if (var4.exists() && var4.length() > 1000000L && var4.canExecute()) {
                 decoderBinaryAbsolutePath = var4.getAbsolutePath() + fingerprintGenerator.decoderCommand;
            }



            if (pipeEnvironment != null) {
                // @formatter:off
                PipeDecoder decoder = new PipeDecoder(pipeEnvironment, 
                                                      pipeArgument,
                                                    decoderBinaryAbsolutePath,
                                                      null,
                                                      fingerprintGenerator.decoderBufferSize);



                // @formatter:on
                PipedAudioStream.setDecoder(decoder);
            }
            return fingerprintGenerator;
        }

    }

    public List<NFFTFingerprint> getFingerprints(String resource) throws TimeoutException {
        int overlap = nfftSize - nfftStepSize;

        PipedAudioStream var6 = new PipedAudioStream(resource);
        TarsosDSPAudioInputStream audioStream = var6.getMonoStream(nfftSampleRate, 0);
        TimedAudioDispatcher d = new TimedAudioDispatcher(audioStream, nfftSize, overlap);

        final NFFTEventPointProcessor minMaxProcessor = new NFFTEventPointProcessor(nfftSize, overlap,
                nfftSampleRate);
        d.addAudioProcessor(minMaxProcessor);
        d.run(decoderTimeoutInSeconds, TimeUnit.SECONDS);
        return minMaxProcessor.getFingerprints();

    }

}
