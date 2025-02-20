package vn.tapbi.zazip.ui.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import vn.tapbi.zazip.R;
import vn.tapbi.zazip.utils.Utils;

public class CustomClickItem extends androidx.appcompat.widget.AppCompatImageView {
    private Paint paint;
    private Bitmap mBitmap;
    private int strokeCircle;
    private Boolean checkClick = false;
    private int resourceBm;
    private Boolean checkBitmap = false;

    public CustomClickItem(Context context) {
        super(context);
        init();
    }

    public CustomClickItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getAttr(attrs);
        init();
    }

    public CustomClickItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttr(attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeCircle);
        paint.setColor(Color.parseColor("#AFAFAF"));
    }

    @SuppressLint("Recycle")
    private void getAttr(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CustomClickItem);
        resourceBm = typedArray.getResourceId(R.styleable.CustomClickItem_imageBitmap, R.drawable.icon_check_fill);
        mBitmap = BitmapFactory.decodeResource(getContext().getResources(), resourceBm);
        strokeCircle = typedArray.getDimensionPixelSize(R.styleable.CustomClickItem_strokeCircle, Utils.dpToPx(1));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (checkClick) {
            drawBitmap(canvas);
        } else {
            drawCircleEmpty(canvas);
        }
        super.onDraw(canvas);
    }

    public void checkClickItem(Boolean checkClick) {
        this.checkClick = checkClick;
        invalidate();
    }

    public void setCheckBitmap(Boolean checkBitmap) {
        this.checkBitmap = checkBitmap;
        invalidate();
    }

    public Boolean getCheckClick() {
        return checkClick;
    }

    private void drawCircleEmpty(Canvas canvas) {
        int radius;
        if (checkBitmap) {
            radius = Utils.dpToPx(6);
        } else {
            radius = Utils.dpToPx(10);
        }
        canvas.drawCircle(getWidth() / 2f,
                getHeight() / 2f, radius, paint);
    }

    public void setColor(int color) {
        paint.setColor(color);
        invalidate();
    }

    private void drawBitmap(Canvas canvas) {
        if (mBitmap == null) return;
        if (checkBitmap) {
            mBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_check_fill_png);
        } else {
            mBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.icon_check_fill);
        }
        canvas.drawBitmap(mBitmap, getWidth() / 2f - mBitmap.getWidth() / 2f, getHeight() / 2f - mBitmap.getHeight() / 2f, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

}
