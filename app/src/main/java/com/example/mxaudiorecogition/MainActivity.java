package com.example.mxaudiorecogition;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
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

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity {

    private String downloadPath = "";
    private static FingerprintGenerator fingerprintGenerator;
    private String sampleFileName = "";
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

        NCteQHashFunction.ncteqBinsPerOctave = 36;
        NCteQHashFunction.ncteqEventPointFrequencyDeltaMax = 1066;

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

                List<FingerprintData> result = getAudioFingerprints();
                TextView printFingerprint = (TextView)findViewById(R.id.textView3);
                printFingerprint.setClickable(true);
                printFingerprint.setMovementMethod (LinkMovementMethod.getInstance());

                Track matchedTrack = matchFingerprintFromServer(result);
                String displayMessage = "";

                if(matchedTrack==null || (matchedTrack.getTrackTitle()==null && matchedTrack.getSeokey()==null)){
                    displayMessage = "<h2 color=red>No match found from server</h2>";
                } else {
                    displayMessage = getHtmlFormattedTrackResponse(matchedTrack);
                }

                printFingerprint.setText(Html.fromHtml(displayMessage,Html.FROM_HTML_MODE_COMPACT));
                mp.reset();

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

    private void playSong(){
        try{
            File song = new File(downloadPath + "/" + sampleFileName);
            boolean songExist = song.exists();

            System.out.println("The song exists?"+songExist);

            EditText songFullPath = (EditText)findViewById(R.id.editText3);
            String sourceSong = songFullPath.getText().toString();
            mp.reset();

            mp.setDataSource(downloadPath + "/" + sampleFileName);
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
            System.out.println("sending data to server with fingerprint " + jsonString);
            String response = caller.post("https://mxaudio.dev.mxplay.com/v1/audio/query",jsonString);
            System.out.println("received response from server for matched fingerprint " + response);
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
                EditText fromSeconds = (EditText) findViewById(R.id.editText);
                EditText endSeconds = (EditText) findViewById(R.id.editText2);
                EditText srcSongPath = (EditText) findViewById(R.id.editText3);
                sampleFileName = UUID.randomUUID().toString() + ".mp3";
                clipSong(fromSeconds.getText().toString(),
                        endSeconds.getText().toString(),
                        srcSongPath.getText().toString(),
                        AndroidFFMPEGLocator.ffmpegTargetLocation().getAbsolutePath());

                playSong();
            }

        });
    }


    private List<FingerprintData> getAudioFingerprints() {
        List<String> files  = FileUtils.glob(downloadPath, sampleFileName, false);
        List<List<FingerprintData>> result = new ArrayList<>();
        if(files != null && !files.isEmpty()){
            List<FingerprintData> fingerprints = query(files.get(0));
            File fdelete = new File(downloadPath + "/" + sampleFileName);
            fdelete.delete();
            return fingerprints;
        }
        return  new ArrayList<>();
    }

    private List<FingerprintData> query(String query) {
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

        TextView output = (TextView)findViewById(R.id.textView3);

        ShellCommand sh = new ShellCommand();
        Process process = sh.run(cmd,null);
        while (true) {
            try{
                if(process.exitValue() == 0){
                    output.setText("Clip success ! Go ahead match your clip for recognition");
                }
                else{
                    output.setText("Unsupported song format unable to clip");
                    System.out.println(Util.convertInputStreamToString(process.getErrorStream()));
                }
                break;
            }
            catch (IllegalThreadStateException e){
                // do nothing
            }

        }





    }
}
