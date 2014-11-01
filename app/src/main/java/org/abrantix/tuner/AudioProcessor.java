package org.abrantix.tuner;

import android.media.AudioFormat;
import android.media.AudioRecord;

/**
 * Created by fabrantes on 01/11/14.
 */
public class AudioProcessor {

    AudioRecord mAudioRecord;
    public static final int mSampleRateHz = 44100; //samp per sec 8000, 11025, 22050 44100 or 48000
    public int mChannelConfig = AudioFormat.CHANNEL_IN_MONO;
    public int mAudioEncoding = AudioFormat.ENCODING_PCM_16BIT;

//    public AudioProcessor() {
//        mAudioRecord = new AudioRecord(
//                android.media.MediaRecorder.AudioSource.MIC,
//                mSampleRateHz,
//                mChannelConfig,
//                mAudioEncoding,
//
//
//        );
//    }

}
