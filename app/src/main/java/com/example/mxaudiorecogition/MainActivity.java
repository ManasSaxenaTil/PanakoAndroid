package com.example.mxaudiorecogition;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.AudioDispatcher;

public class MainActivity extends AppCompatActivity {

    private static final String songName = "full-0.mp3";
    private static final String sampleFileName = "testsample.mp3";
    private static final String downloadPath = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .getAbsolutePath();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new AndroidFFMPEGLocator(this);

        registerClipButtonListener();
        registerFingerprintGenerateButtonListener();

    }

    /**
     *Register listener for generate finger print button
     *
     */
    private void registerFingerprintGenerateButtonListener() {
        Button generateFingerPrints = (Button) findViewById(R.id.button2);
        generateFingerPrints.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                List<List<NFFTFingerprint>> result = getAudioFingerprints();
                TextView printFingerprint = (TextView)findViewById(R.id.textView3);
                printFingerprint.setText(result.toString());
                File fdelete = new File(downloadPath + "/" + sampleFileName);
                fdelete.delete();
            }
        });
    }

    /**
     * Register listener for clip song button
     */
    private void registerClipButtonListener() {
        Button clipSongButton = (Button)findViewById(R.id.button3);
        clipSongButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                EditText fromSeconds = (EditText) findViewById(R.id.editText);
                EditText endSeconds = (EditText) findViewById(R.id.editText2);
                EditText srcSongPath = (EditText) findViewById(R.id.editText3);
                clipSong(fromSeconds.getText().toString(),
                        endSeconds.getText().toString(),
                        srcSongPath.getText().toString(),
                        AndroidFFMPEGLocator.ffmpegTargetLocation().getAbsolutePath());
            }

        });
    }


    private List<List<NFFTFingerprint>> getAudioFingerprints() {
        List<String> files  = FileUtils.glob(downloadPath, sampleFileName, false);

        List<List<NFFTFingerprint>> result = new ArrayList<>();

        for(String x : files){

            result.add(query(x));
        }
        return result;
    }

    private List<NFFTFingerprint> query(String query) {
        int samplerate = Config.getInt(Key.NFFT_SAMPLE_RATE);
        int size = Config.getInt(Key.NFFT_SIZE);
        int overlap = size - Config.getInt(Key.NFFT_STEP_SIZE);
        AudioDispatcher d = AudioDispatcherFactory.fromPipe(query, samplerate, size, overlap);
        final NFFTEventPointProcessor minMaxProcessor = new NFFTEventPointProcessor(size, overlap, samplerate);
        d.addAudioProcessor(minMaxProcessor);
        d.run();
        List<NFFTFingerprint> fingerprints = new ArrayList<NFFTFingerprint>(minMaxProcessor.getFingerprints());
        return fingerprints;
    }


    private void clipSong(String startTime, String endTime, String srcSongPath ,String ffmpegDestination){

        String targetSongPath = downloadPath + "/" + sampleFileName;

        String[] cmd = {
                ffmpegDestination,
                "-i",
                srcSongPath,
                "-ss",
                startTime,
                "-to",
                endTime,
                "-c",
                "copy",
                targetSongPath};

        FFcommandExecuteAsyncTask task = new FFcommandExecuteAsyncTask(cmd, null, Long.MAX_VALUE, null);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private void copyFromAssetsToDownloadPath() {
        String[] test = null;
        InputStream in = null;
        OutputStream out = null;
        try {
            test = this.getAssets().list("");


            in = this.getAssets().open(songName);
            boolean b = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs();
            File outFile = new File(downloadPath, songName);
            out = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int read;
            while((read = in.read(buffer)) != -1){
                out.write(buffer, 0, read);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // NOOP
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // NOOP
                }
            }
        }
    }
}
