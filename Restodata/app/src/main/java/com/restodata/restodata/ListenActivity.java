package com.restodata.restodata;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.TextView;

import com.restodata.restodata.api.WebApiClient;
import com.restodata.webapp.model.ApiResponse;

import java.util.ArrayList;


public class ListenActivity extends Activity{

    private static final String TAG = "ListenActivity";
    private TextView textOut;
    private SpeechRecognizer recognizer;
    private WebApiClient.WebApiCallback mOrderRequestCallback = new WebApiClient.WebApiCallback() {
        @Override
        public void onSuccess(ApiResponse response) {
            if (response.status.equals("success")) {
                textOut.append("registered order: " + response.matchedItem.name+"\n");
            } else {
                textOut.append("no match for that\n");
            }
        }

        @Override
        public void onError(String error) {

        }
    };
    private RecognitionListener mRecognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "ready: "+params);
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.d(TAG, "begin");
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
                    error==SpeechRecognizer.ERROR_SPEECH_TIMEOUT ||
                    error==SpeechRecognizer.ERROR_NETWORK ||
                    error==SpeechRecognizer.ERROR_NETWORK_TIMEOUT ) {
                recognizer.setRecognitionListener(mRecognitionListener);
                recognizer.startListening(recognizerIntent);
            }
        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> list = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (list != null) {
                StringBuilder b = new StringBuilder();
                for (String s : list) {
                    b.append(s).append("#");
                }
                WebApiClient.sendOrderRequest(list, mOrderRequestCallback);

                textOut.append("results: " + b.toString() + "\n");
            } else {
                textOut.append("results: null\n");
            }
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
