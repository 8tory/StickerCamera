package com.stickercamera.app.camera.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.common.util.ImageUtils;
import com.customview.LabelView;
import com.customview.MyHighlightView;
import com.customview.MyImageViewDrawableOverlay;
import com.customview.drawable.StickerDrawable;
import com.github.skykai.stickercamera.R;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import com.stickercamera.AppConstants;
import com.stickercamera.app.model.Addon;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import com.common.util.DistanceUtil;

/**
 * Created by sky on 15/7/6.
 */
public class EffectUtil {

    public static List<Addon> addonList                 = new ArrayList<Addon>();

    static {
        addonList.add(new Addon(R.drawable.fb_smile));
        addonList.add(new Addon(R.drawable.sticker1));
        addonList.add(new Addon(R.drawable.sticker2));
        addonList.add(new Addon(R.drawable.sticker3));
        addonList.add(new Addon(R.drawable.sticker4));
        addonList.add(new Addon(R.drawable.sticker5));
        addonList.add(new Addon(R.drawable.sticker6));
        addonList.add(new Addon(R.drawable.sticker7));
        addonList.add(new Addon(R.drawable.sticker8));
    }

    public static int getStandDis(Context context, float realDis, float baseWidth) {
        float imageWidth = baseWidth <= 0 ? DistanceUtil.getInstance(context).getScreenWidth() : baseWidth;
        float radio = AppConstants.DEFAULT_PIXEL / imageWidth;
        return (int) (radio * realDis);
    }

    public static int getRealDis(Context context, float standardDis, float baseWidth) {
        float imageWidth = baseWidth <= 0 ? DistanceUtil.getInstance(context).getScreenWidth() : baseWidth;
        float radio = imageWidth / AppConstants.DEFAULT_PIXEL;
        return (int) (radio * standardDis);
    }

    //添加水印
    public static void applyOnSave(Canvas canvas, ImageViewTouch processImage) {
        applyOnSave(canvas, (MyImageViewDrawableOverlay) processImage);
    }

    public static void applyOnSave(Canvas canvas, MyImageViewDrawableOverlay processImage) {
        for (MyHighlightView view : processImage.highlightViews()) {
            applyOnSave(canvas, processImage, view);
        }
    }

    private static void applyOnSave(Canvas canvas, ImageViewTouch processImage, MyHighlightView view) {

        if (view != null && view.getContent() instanceof StickerDrawable) {

            final StickerDrawable stickerDrawable = ((StickerDrawable) view.getContent());
            RectF cropRect = view.getCropRectF();
            Rect rect = new Rect((int) cropRect.left, (int) cropRect.top, (int) cropRect.right,
                    (int) cropRect.bottom);

            Matrix rotateMatrix = view.getCropRotationMatrix();
            Matrix matrix = new Matrix(processImage.getImageMatrix());
            if (!matrix.invert(matrix)) {
            }
            int saveCount = canvas.save(Canvas.MATRIX_SAVE_FLAG);
            canvas.concat(rotateMatrix);

            stickerDrawable.setDropShadow(false);
            view.getContent().setBounds(rect);
            view.getContent().draw(canvas);
            canvas.restoreToCount(saveCount);
        }
    }

}
