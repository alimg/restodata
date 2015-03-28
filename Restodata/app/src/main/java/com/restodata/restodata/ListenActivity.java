package com.restodata.restodata;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;


public class ListenActivity extends Activity{

    private static final String TAG = "ListenActivity";
    private TextView textOut;
    private SpeechRecognizer recognizer;
    private RecognitionListener mRecognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "ready: "+params);
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.d(TAG, "being");
        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            Log.d(TAG, "received "+ buffer.length);
        }

        @Override
        public void onEndOfSpeech() {
            Log.d(TAG, "end of speech");
        }

        @Override
        public void onError(int error) {
            Log.d(TAG, "error:" +error);
            if (error==SpeechRecognizer.ERROR_NO_MATCH ||
                    error==SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                recognizer.setRecognitionListener(mRecognitionListener);
                recognizer.startListening(recognizerIntent);
            }
        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> list = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            StringBuilder b = new StringBuilder();
            for (String s: list) {
                b.append(s).append("#");
            }
            textOut.append("results: "+b.toString()+"\n");
            recognizer.setRecognitionListener(mRecognitionListener);
            recognizer.startListening(recognizerIntent);
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            Log.d(TAG, "onPartialResults");
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            textOut.append("event: " + params.toString() + "\n");
        }
    };
    private Intent recognizerIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);

        textOut = (TextView) findViewById(R.id.text_out);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr");
    }

    @Override
    protected void onResume() {
        super.onResume();

        recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognizer.setRecognitionListener(mRecognitionListener);
        recognizer.startListening(recognizerIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        recognizer.stopListening();
        recognizer.cancel();
        recognizer.destroy();
        recognizer = null;
    }
}
