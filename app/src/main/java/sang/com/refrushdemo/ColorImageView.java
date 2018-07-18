package sang.com.refrushdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import sang.com.easyrefrush.refrushutils.JLog;

/**
 * 作者： ${PING} on 2018/7/18.
 */

public class ColorImageView extends android.support.v7.widget.AppCompatImageView {

    private Paint mPaint;
    private int mCurrentColor;
    private PorterDuffXfermode xfermode;

    public ColorImageView(Context context) {
        this(context, null);
    }

    public ColorImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mCurrentColor=Color.BLACK;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
            canvas.save();
            mPaint.setColor(mCurrentColor);
            mPaint.setXfermode(xfermode);

            if (bitmap == null) {
                bitmap = creatBitmap(canvas);
            }
            canvas.drawBitmap(bitmap, 0, 0, mPaint);
            mPaint.setXfermode(null);
            canvas.restore();
    }

    public void setCurrentColor(int mCurrentColor) {
        this.mCurrentColor = mCurrentColor;
        postInvalidate();
    }

    private Bitmap bitmap;

    private Bitmap creatBitmap(Canvas canvas) {
        Bitmap bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas newCanvas = new Canvas(bitmap);
        newCanvas.drawColor(mCurrentColor);
        newCanvas.setBitmap(null);
        newCanvas = null;
        return bitmap;

    }

}
