package com.example.mxaudiorecogition;

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.AudioDispatcher;

public class MainActivity extends AppCompatActivity {

    private static final String sampleFileName = "testsample.mp3";
    private static final String downloadPath = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .getAbsolutePath();
    private String path = downloadPath;

    private static FingerprintGenerator fingerprintGenerator;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestStoragePermission();
        initializeSDK();
        new AndroidFFMPEGLocator(this);
        registerBrowseButtonListener();
        registerClipButtonListener();
        registerFingerprintGenerateButtonListener();

    }

    private void initializeSDK(){
        fingerprintGenerator
                = FingerprintGenerator
                .newBuilder()
                .setDecoderBufferSize(44100)
                .setDecoderCommand(" -ss %input_seeking%  %number_of_seconds% -i \"%resource%\" -vn -ar %sample_rate% -ac %channels% -sample_fmt s16 -f s16le pipe:1")
                .setDecoderTimeoutInSeconds(1000)
                .setNfftSampleRate(8000)
                .setNfftSize(512)
                .setNfftStepSize(256)
                .build();

        NFFTEventPointProcessor.defaultMaxFilterWindowSize = 15;
        NFFTEventPointProcessor.defaultMinFilterWindowSize = 7;
        NFFTEventPointProcessor.maxFingerprintsPerEventPoint = 2;
        NFFTEventPointProcessor.maxEventPointsPerFrame = 3;
        NFFTEventPointProcessor.nfftSize = 512;
        NFFTEventPointProcessor.nfftStepSize = 256;
        NFFTEventPointProcessor.nfftSampleRate = 8000;
        NFFTEventPointProcessor.nfftMinEventPointsDistance = 600;

        NFFTFingerprint.hashWithFrequencyEstimate = true;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                try {
                    File file = FileUtils.from(MainActivity.this,uri);
                    EditText songFullPath = (EditText)findViewById(R.id.editText3);
                    songFullPath.setText(file.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void registerBrowseButtonListener(){
        Button browseFiles = (Button) findViewById(R.id.button);
        browseFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("audio/*");
                startActivityForResult(Intent.createChooser(intent, "Select file"), 1);
            }
        });
    }


    private void requestStoragePermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);


            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    2);
        }
    }

    /**
     * Register listener for generate finger print button
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
        try {
            return fingerprintGenerator.getFingerprints(query);
        } catch (TimeoutException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
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
}
