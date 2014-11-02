package org.abrantix.tuner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by fabrantes on 02/11/14.
 */
public class TunerText extends TextView {

    TextView mBackTextView;
    Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
    float mDisplacement = 0f;
    private Rect mSrcRect = new Rect();
    private RectF mDstRect = new RectF();
    private Random mRandom = new Random();

    public TunerText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBackTextView = new TextView(context, attrs);
    }

    public TunerText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBackTextView = new TextView(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mBackTextView.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mBackTextView.layout(left, top, right, bottom);
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        mBackTextView.setLayoutParams(params);
    }

    public void setAdaptingText(@Nullable CharSequence txt, float displacement) {
        mDisplacement = displacement;
        this.setText(txt);
        mBackTextView.setText(txt);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        if (this.getDrawingCache() == null) {
            buildDrawingCache();
        }
        drawBlur(canvas);
//        final Canvas backCanvas = new Canvas(getDrawingCache());
//        getDrawingCache().eraseColor(Color.TRANSPARENT);
//        mBackTextView.draw(backCanvas);
//        final int columns = 50;
//        for (int column = 0; column < columns; column++) {
//            mSrcRect.left = column * getDrawingCache().getWidth() / columns;
//            mSrcRect.right = (column + 1) * getDrawingCache().getWidth() / columns;
//            mSrcRect.top = 0;
//            mSrcRect.bottom = getDrawingCache().getHeight();
//
//            final float delta = -mDisplacement * getHeight() / 3;
//            final float factor = (columns / 2 - Math.abs(column - columns / 2)) / (float)
//                    (columns / 2);
//            mDstRect.left = column * getWidth() / columns;
//            mDstRect.right = (column + 1) * getWidth() / columns;
//            mDstRect.top = delta * factor;
//            mDstRect.bottom = mDstRect.top + getHeight();
//
//            canvas.drawBitmap(getDrawingCache(), mSrcRect, mDstRect, mPaint);
//        }

    }

    Allocation mOverlayAlloc;
    RenderScript mRenderscript;

    public void drawBlur (Canvas canvas) {
        float radius = Math.max(Math.abs(mDisplacement) * 25, 1);
        radius = Math.min(radius, 75);

        final Bitmap overlay = getDrawingCache();
        final Canvas backCanvas = new Canvas(getDrawingCache());
        getDrawingCache().eraseColor(Color.TRANSPARENT);
        mBackTextView.draw(backCanvas);
        if (mRenderscript == null) {
            mRenderscript = RenderScript.create(getContext());
        }
        // if (mOverlayAlloc == null) { // we should get rid of this
            mOverlayAlloc = Allocation.createFromBitmap(mRenderscript, overlay);
        // }
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(mRenderscript,
                mOverlayAlloc.getElement());
        blur.setInput(mOverlayAlloc);
        while (true) {
            final int r = (int) Math.min(radius, 25);
            if (r  == 0) {
                break;
            }
            blur.setRadius(r);
            blur.forEach(mOverlayAlloc);
            radius -= r;
        }
        mOverlayAlloc.copyTo(overlay);
        canvas.drawBitmap(overlay, 0, 0, mPaint);
    }
}
