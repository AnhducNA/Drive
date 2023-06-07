package com.kma.drive;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class CustomView extends View {
    private Paint paint;
    private ImageView imageView;
    private TextView textView;
    private Context context;

    public CustomView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        init();
    }

    public CustomView(Context context, ImageView imageView, TextView textView){
        super(context);
        this.context = context;
        this.imageView = imageView;
        this.textView = textView;
        init();
    }
    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(canvas.getWidth(), MeasureSpec.EXACTLY);
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(canvas.getHeight(), MeasureSpec.EXACTLY);
        imageView.measure(widthMeasureSpec, heightMeasureSpec);
        textView.measure(widthMeasureSpec, heightMeasureSpec);

        imageView.layout(5, 5, canvas.getWidth(), canvas.getHeight());
        textView.layout(5, 5, canvas.getWidth(), canvas.getHeight());

        // Vẽ view lên Canvas
        imageView.draw(canvas);
        textView.draw(canvas);
    }
}