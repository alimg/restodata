package com.restodata.restodata;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.restodata.restodata.api.WebApiClient;
import com.restodata.webapp.model.ApiResponse;
import com.restodata.webapp.model.MenuItem;
import com.restodata.webapp.model.PredictRequest;

import java.util.ArrayList;


public class ListenActivity extends Activity{

    private static final String TAG = "ListenActivity";

    private SpeechRecognizer recognizer;
    private WebApiClient.WebApiCallback mOrderRequestCallback = new WebApiClient.WebApiCallback() {
        @Override
        public void onSuccess(ApiResponse response) {
            if (response.status.equals("success")) {
                registeredOrder(response.matchedItem);
                Log.d(TAG, "registered order: " + response.matchedItem.name+"\n");
            } else {
                Log.d(TAG, "no match for that\n");
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
            setListening(true);
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
                setListening(false);
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

                Log.d(TAG, "results: " + b.toString());
            } else {
                Log.d(TAG, "results: null");
            }
            recognizer.setRecognitionListener(mRecognitionListener);
            recognizer.startListening(recognizerIntent);
            setListening(false);
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            Log.d(TAG, "onPartialResults");
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            Log.d(TAG, "event: " + params.toString() + "\n");
        }
    };

    private Intent recognizerIntent;
    private LinearLayout layoutOrders;
    private ScrollView scrollView;
    private View listeningView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        //scrollView.fullScroll(View.FOCUS_DOWN);
        layoutOrders = (LinearLayout) findViewById(R.id.layout_orders);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr");

        listeningView = getLayoutInflater().inflate(R.layout.order_item, null);
        ((TextView)listeningView).setText("...");
        //final LayoutTransition transitioner = new LayoutTransition();
        //layoutOrders.setLayoutTransition(transitioner);
    }

    private void setListening(boolean enabled) {
        if (enabled) {
            try {
                if (listeningView.getParent() == null)
                    layoutOrders.addView(listeningView);
            } catch (Exception e) {
                e.printStackTrace();
            }
            scrollToBottom();
        } else {
            layoutOrders.removeView(listeningView);
        }
    }

    private void registeredOrder(MenuItem matchedItem) {
        TextView tv = (TextView) LayoutInflater.from(this).inflate(R.layout.order_item, null);
        tv.setText(matchedItem.name);
        layoutOrders.addView(tv);
        scrollToBottom();
    }

    private void scrollToBottom() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, scrollView.getBottom());
            }
        });
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
