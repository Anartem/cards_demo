package com.projects.anartem.cards.widgets.itemdecorations;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SelectionItemDecoration extends RecyclerView.ItemDecoration {
    private final Paint mPaint;
    private final Bitmap mBitmap;

    public SelectionItemDecoration(Context context, int id) {
        mPaint = new Paint();
        mPaint.setFilterBitmap(true);
        mPaint.setAntiAlias(true);

        mBitmap = getBitmapFromVectorDrawable(context, id);
    }

    @Override
    public void onDrawOver(final Canvas canvas,
                           final RecyclerView parent,
                           final RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);

        for (int i = 0, max = parent.getChildCount(); i < max; i++) {
            final View child = parent.getChildAt(i);

            if (child.isActivated()) {
                canvas.drawBitmap(
                        mBitmap,
                        child.getLeft(),
                        child.getTop(),
                        mPaint);
            }
        }
    }

    private static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);

        if (drawable == null) {
            return null;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}