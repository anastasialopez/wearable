package org.abrantix.tuner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by fabrantes on 01/11/2014.
 */
public class FFTGraph extends View {

    private double[] mFft;
    private double mMaxNorm;
    private double mDominantFreq;
    private double mMaxFreq;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public FFTGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FFTGraph(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setFft(double dominantFreq, double maxFreq, @NonNull double[] fft, double maxNorm) {
        mFft = fft;
        mDominantFreq = dominantFreq;
        mMaxFreq = maxFreq;
        mMaxNorm = maxNorm;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mFft != null) {
            for (int i = 0; i < mFft.length; i++) {
                final int left = (int) (getWidth() * (i / (float) mFft.length * 2));
                final int top = (int) (getHeight() * (1 - mFft[i] / mMaxNorm));
                mPaint.setColor(Color.RED);
                canvas.drawRect(left, top, left + 1, getHeight(), mPaint);
            }
        }

        final int left = (int) (getWidth() * (mDominantFreq / mMaxFreq));
        final int top = 0;
        mPaint.setColor(Color.YELLOW);
        canvas.drawRect(left, top, left + 1, getHeight(), mPaint);
    }
}
