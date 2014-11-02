package org.abrantix.tuner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.wearable.view.WatchViewStub;
import android.view.View;

public class TunerActivity extends Activity implements AudioProcessor.AudioProcessorListener {

    private static final String TAG = "TunerActivity";

    private static final int MODE_AUTO_TUNE = 99;
    private static final int MODE_NORMAL_TUNE = 66;
    private static final int VOICE_REC_REQ_CODE = 123;

    private int mMode = MODE_NORMAL_TUNE;
    private TunerText mNoteLabel;
    private FFTGraph mGraph;
    private AudioProcessor mProcessor;
    private NoteFreqTable mNoteTable;
    private NoteFreqTable.Note mTargetNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuner);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mNoteLabel = (TunerText) findViewById(R.id.note_label);
                if (mNoteTable == null) {
                    mNoteTable = new NoteFreqTable(3, 8);
                    mTargetNote = mNoteTable.get(12 * 3 + 4);
                }
                mGraph = (FFTGraph) findViewById(R.id.fft_graph);
                if (mProcessor == null) {
                    mProcessor = new AudioProcessor();
                    mProcessor.setListener(TunerActivity.this);
                    mProcessor.start();
                }
                mGraph.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        triggerVoice();
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        mProcessor.stop();
        super.onDestroy();
    }

    @Override
    public void onAudioProcessed(@NonNull AudioProcessor processor, double dominantFreq,
                                 double maxFreq, double[] fft, double maxNorm) {
        if (mGraph != null) {
            mGraph.setFft(dominantFreq, maxFreq, fft, maxNorm);
        }
        final NoteFreqTable.Note note = mMode == MODE_AUTO_TUNE ?
                mNoteTable.getClosestNote(dominantFreq) : mTargetNote;
        final float displacement = (float) ((dominantFreq - note.mFreq) / (note.mFreq * .05f));
        mNoteLabel.setAdaptingText(note.mName, displacement);
        mNoteLabel.invalidate();
    }

    private void triggerVoice() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a note");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        startActivityForResult(intent, VOICE_REC_REQ_CODE);
    }
}
