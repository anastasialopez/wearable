package org.abrantix.tuner;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by fabrantes on 01/11/14.
 */
public class AudioProcessor {

    AudioRecord mAudioRecord;
    public static final int mSampleRateHz = 8000; //samp per sec 8000, 11025, 22050 44100 or 48000
    private int mChannelConfig = AudioFormat.CHANNEL_IN_MONO;
    private int mAudioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    private int mBufferSize;
    private AudioProcessorListener mListener;
    private AudioHandler mHandler;
    private short[] mShortBuffer;
    private int mFftSampleSize;
    private FFT mFft;
    private double mDominantFreq = 0;
    private double mSmooth = 0.25;

    public AudioProcessor() {
        mHandler = new AudioHandler(this);
        mBufferSize = AudioRecord.getMinBufferSize(mSampleRateHz, mChannelConfig, mAudioEncoding);
        mBufferSize = calcLowestPowerOfTwo(mBufferSize);
        mShortBuffer = new short[mBufferSize/2];
        mFftSampleSize = mBufferSize / 2;
        mFft = new FFT(mFftSampleSize);
        mAudioRecord = new AudioRecord(
                android.media.MediaRecorder.AudioSource.MIC,
                mSampleRateHz,
                mChannelConfig,
                mAudioEncoding,
                mBufferSize);
    }

    private int calcLowestPowerOfTwo(int bufferSize) {
        int size = 1;
        int power = 0;
        while(size < bufferSize) {
            power++;
            size = (int) Math.pow(2, power);
        }
        return size;
    }


    public void start() {
        mAudioRecord.startRecording();

        final Message message = mHandler.obtainMessage();
        message.obj = this;
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessage(0);
    }

    public void stop() {
        mAudioRecord.stop();
        mHandler.removeMessages(0);
    }

    public void setListener(@Nullable AudioProcessorListener listener) {
        mListener = listener;
    }

    public void read() {
        mAudioRecord.read(mShortBuffer, 0, mShortBuffer.length);

        final double[] inputAsDoubles = new double[mFftSampleSize];
        for (int i = 0; i < mFftSampleSize; i++) {
            inputAsDoubles[i] = mShortBuffer[i] + Short.MAX_VALUE / 2;
        }

        final double[] imFftResult = new double[mFftSampleSize];
        mFft.fft(inputAsDoubles /* at the end this will actually hold the real part of the fft */,
                imFftResult /* this will hold the imag part of the fft */);

        double highestFreqVal = 0;
        double highestFreq = 0;
        final double[] realPlusIm = new double[mFftSampleSize];
        final double[] realPlusImLog = new double[mFftSampleSize];
        for (int i = 0; i < mFftSampleSize / 2; i++) {
            realPlusIm[i] = Math.sqrt(inputAsDoubles[i] * inputAsDoubles[i] +
                                imFftResult[i] * imFftResult[i]);
            realPlusImLog[i] = 10 * Math.log10(realPlusIm[i]);

            if (i != 0 && realPlusIm[i] > highestFreqVal) {
                highestFreq = i;
                highestFreqVal = realPlusIm[i];
            }
        }
        final double strongnessMeasure = highestFreqVal / (realPlusIm[0] / 4);
        highestFreq = highestFreq * (mSampleRateHz / (double) mFftSampleSize);
        mDominantFreq += (highestFreq - mDominantFreq) * mSmooth * strongnessMeasure;
//        mDominantFreq += (highestFreq - mDominantFreq) * mSmooth;

        if (mListener != null) {
            mListener.onAudioProcessed(this, mDominantFreq, mSampleRateHz / 2, realPlusIm,
                    realPlusIm[0] / 2);
        }
    }

    public static class AudioHandler extends Handler {

        AudioProcessor mAudioProcessor;

        public AudioHandler(AudioProcessor audioProcessor) {
            mAudioProcessor = audioProcessor;
        }

        @Override
        public void handleMessage(Message msg) {
            mAudioProcessor.read();
            this.sendEmptyMessageDelayed(0, 0);
        }
    }

    public static interface AudioProcessorListener {
        void onAudioProcessed(@NonNull AudioProcessor processor, double dominantFreq,
                              double maxFreq, double[] fft, double maxNorm);
    }
}
