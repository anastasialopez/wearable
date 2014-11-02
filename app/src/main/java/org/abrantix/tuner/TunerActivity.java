package org.abrantix.tuner;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

public class TunerActivity extends Activity implements AudioProcessor.AudioProcessorListener {

    private static final String TAG = "TunerActivity";
    private TextView mTextView;
    private FFTGraph mGraph;
    private AudioProcessor mProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuner);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mGraph = (FFTGraph) findViewById(R.id.fft_graph);
                if (mProcessor == null) {
                    mProcessor = new AudioProcessor();
                    mProcessor.setListener(TunerActivity.this);
                    mProcessor.start();
                }
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
    }
}
