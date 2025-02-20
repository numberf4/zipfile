package vn.tapbi.zazip.ui.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import vn.tapbi.zazip.utils.DisplayUtils;

public class CustomViewShadow extends View {
    private Paint paint;

    private int SIZE_4 = DisplayUtils.dp2px(4f);

    public CustomViewShadow(Context context) {
        super(context);
        initData();
        setBackgroundColor(Color.TRANSPARENT);
    }

    public CustomViewShadow(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initData();
        setBackgroundColor(Color.TRANSPARENT);
    }

    public CustomViewShadow(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
        setBackgroundColor(Color.TRANSPARENT);
    }

    private void initData() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setShadowLayer(SIZE_4, 0, DisplayUtils.dp2px(2), Color.parseColor("#AFAFAF"));
        setLayerType(LAYER_TYPE_SOFTWARE, paint);
    }
    private void drawShadow(Canvas canvas){
        canvas.drawRect(0,DisplayUtils.dp2px(10),getWidth(),getHeight(),paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawShadow(canvas);
        super.onDraw(canvas);
    }
}
