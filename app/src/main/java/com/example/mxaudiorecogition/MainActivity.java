package com.example.mxaudiorecogition;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mxaudiorecogition.com.mxaudiorecognition.client.FingerprintData;
import com.example.mxaudiorecogition.com.mxaudiorecognition.client.FingerprintGenerator;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity {

    private String downloadPath = "";
    private static FingerprintGenerator fingerprintGenerator;
    //private String sampleFileName = "";
    private MediaPlayer mp = new MediaPlayer();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        downloadPath = this.getCacheDir().getAbsolutePath();
        new AndroidFFMPEGLocator(this);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        setContentView(R.layout.activity_main);
        requestStoragePermission();
        initializeSDK();

        TextView printFingerprint = (TextView)findViewById(R.id.textView3);
        printFingerprint.setClickable(true);
        printFingerprint.setMovementMethod (LinkMovementMethod.getInstance());

        registerBrowseButtonListener();
        registerClipButtonListener();
        registerFingerprintMatcherListener();
        registerClearButtonListener();

    }

    private void initializeSDK(){
        fingerprintGenerator
                = FingerprintGenerator
                .newBuilder()
                .setDecoderBufferSize(44100)
                .setDecoderCommand("ffmpeg -ss %input_seeking%  %number_of_seconds% -i \"%resource%\" -vn -ar %sample_rate% -ac %channels% -sample_fmt s16 -f s16le pipe:1")
                .setDecoderTimeoutInSeconds(20)
                .setMinFrequencyInCents(3700)
                .setMaxFrequencyInCents(12200)
                .setBinsPerOctave(36)
                .setMaxEventPointDeltaFrequency(1066)
                .setStepSize(1536)
                .setSampleRate(44100)
                .build();
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

    private void registerClearButtonListener() {
        Button clearButton = (Button) findViewById(R.id.button4);
        clearButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mp.reset();
                TextView printFingerprint = (TextView)findViewById(R.id.textView3);
                printFingerprint.setText("");
                TextView statsText = (TextView)findViewById(R.id.statisticsText);
                statsText.setText("");
            }

        });
    }

        private void registerBrowseButtonListener(){
        Button browseFiles = (Button) findViewById(R.id.button);
        browseFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "Select file"), 1);
            }
        });
    }


    private void requestStoragePermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
        ){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);


            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    2);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    3);
        }
    }

    /**
     * Register listener for generate finger print button
     */
    @SuppressLint("NewApi")
    private void registerFingerprintMatcherListener() {
        Button generateFingerPrints = (Button) findViewById(R.id.button2);
        generateFingerPrints.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                EditText srcSongPath = (EditText) findViewById(R.id.editText3);
                EditText time = (EditText) findViewById(R.id.sampleTime);
                int sampleTime = Integer.parseInt(time.getText().toString());
                FingerprintGenerationAsyncTask fingerprintGenerationAsyncTask = new FingerprintGenerationAsyncTask();
                fingerprintGenerationAsyncTask.execute(new InputTrack(srcSongPath.getText().toString(),sampleTime));



            }
        });
    }

    private String getHtmlFormattedTrackResponse(Track matchedTrack) {
        String formattedResponse ="";
        formattedResponse += "<h2>" + " Song: <font color=\'#3c8cf0\'>" + matchedTrack.getTrackTitle() +" </font></h2>";
        formattedResponse += "<br> <h3 >" +" Album: <font color=\'#3c8cf0\'>" + matchedTrack.getAlbumTitle() +"</font> </h3>";
        String genres= "", artists ="";

        artists = TextUtils.join(", ",matchedTrack.getArtists());
        genres = TextUtils.join(", ",matchedTrack.getGeners());

        formattedResponse += "<br> <h4 >" +" Genre: <font color=\'#3c8cf0\'>" + genres +"</font></h4>";
        formattedResponse += "<br> <h4 >" +" Artist: <font color=\'#3c8cf0\'>" + artists +"</font></h4>";
        formattedResponse += "<br> <a href=\"" + "https://www.youtube.com/watch?v=" + matchedTrack.getYoutubeId() + "\"> Watch Video</a>";
        return formattedResponse;

    }

    private void playSong(String songPath){
        try{

            mp.reset();

            mp.setDataSource(songPath);
            mp.prepare();
            mp.start();
        }
        catch (IOException ex){
            System.out.println(ex);
        }

    }

    private Track matchFingerprintFromServer(List<FingerprintData> fingerprints) {
        RemoteCaller caller = new RemoteCaller();
        Track track = null;
        try {

            AudioQueryRequest request = new AudioQueryRequest(fingerprints);
            String jsonString = new Gson().toJson(request);

            Instant start = Instant.now();
            String response = caller.post("http://192.168.0.103:8082/v1/audio/query",jsonString);
            Instant end = Instant.now();
            TextView statsText = (TextView)findViewById(R.id.statisticsText);
            statsText.append("\n FP query time: " + Duration.between(end,start));

            track = new Gson().fromJson(response,Track.class);

        } catch (IOException e) {
            e.printStackTrace();

        }

        return track;
    }

    /**
     * Register listener for clip song button
     */
     private void registerClipButtonListener() {
        Button clipSongButton = (Button)findViewById(R.id.button3);
        clipSongButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                EditText srcSongPath = (EditText) findViewById(R.id.editText3);

                playSong(srcSongPath.getText().toString());
            }

        });
    }


    private List<FingerprintData> generatFingerprintData(String query, int sampleTime) {
        try {
            return fingerprintGenerator.getFingerprints(query,sampleTime);
        } catch (TimeoutException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private  class FingerprintGenerationAsyncTask extends AsyncTask<InputTrack,Integer,List<FingerprintData>> {

        @Override
        protected List<FingerprintData> doInBackground(InputTrack... inputTracks) {
            Instant start = Instant.now();
            List<FingerprintData> fps  = generatFingerprintData(inputTracks[0].trackPath,inputTracks[0].sampleTime);
            Instant end = Instant.now();
            TextView statsText = (TextView)findViewById(R.id.statisticsText);
            statsText.append("\n FP gen time: " + Duration.between(end,start));

            return fps;
        }

        @Override
        protected void onPostExecute(List<FingerprintData> fps){
            FingerprintQueryAsyncTask fingerprintQueryAsyncTask = new FingerprintQueryAsyncTask();
            fingerprintQueryAsyncTask.execute(fps);
        }
    }

    private  class FingerprintQueryAsyncTask extends AsyncTask<List<FingerprintData>,Integer,Track> {


        @Override
        protected Track doInBackground(List<FingerprintData>... fps) {
            Track matchedTrack = matchFingerprintFromServer(fps[0]);


            return matchedTrack;

        }

        @Override
        protected void onPostExecute(Track track){

            displayTrackResults(track);
        }

    }

    private void displayTrackResults(Track track) {
        String displayMessage = "";

        if(track==null || (track.getTrackTitle()==null && track.getSeokey()==null)){
            displayMessage = "<h2 color=red>No match found from server</h2>";
        } else {
            displayMessage = getHtmlFormattedTrackResponse(track);
        }
        TextView printFingerprint = (TextView)findViewById(R.id.textView3);
        printFingerprint.setText(Html.fromHtml(displayMessage,Html.FROM_HTML_MODE_COMPACT));
        mp.reset();
    }

}
