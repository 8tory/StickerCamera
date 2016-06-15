package com.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.customview.drawable.StickerDrawable;
import com.customview.drawable.EditableDrawable;
import com.customview.drawable.FeatherDrawable;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import com.stickercamera.app.camera.util.MatrixUtils;
import android.view.View;
import android.view.ViewGroup;
import android.support.annotation.DrawableRes;
import android.support.annotation.CallSuper;

public class MyImageViewDrawableOverlay extends ImageViewTouch {

    public static interface IOnDrawableEventListener {
        void onFocusChange(MyHighlightView newFocus, MyHighlightView oldFocus);

        void onDown(MyHighlightView view);

        void onMove(MyHighlightView view);

        void onClick(MyHighlightView view);

        //标签的点击事件处理
        void onClick(final LabelView label);

        void onUp(final LabelView label, Integer x, Integer y);
    };

    public static class OnDrawableEventListener implements IOnDrawableEventListener {
        @Override
        public void onFocusChange(MyHighlightView newFocus, MyHighlightView oldFocus) {
        }

        @Override
        public void onDown(MyHighlightView view) {
        }

        @Override
        public void onMove(MyHighlightView view) {
        }

        @CallSuper
        public void onMove(MyHighlightView view, float x, float y) {
            onMove(view);
        }

        @Override
        public void onClick(MyHighlightView view) {
        }

        @CallSuper
        public void onClick(MyHighlightView view, float x, float y) {
            onClick(view);
        }

        // TODO: NOT IMPL, NEVER CALALED
        public void onUp(MyHighlightView view, float x, float y) {
        }

        @Override
        public void onClick(final LabelView label) {
        }

        @Override
        public void onUp(final LabelView label, Integer x, Integer y) {
        }
    };

    //删除的时候会出错
    private List<MyHighlightView>   mOverlayViews         = new CopyOnWriteArrayList<MyHighlightView>();

    private MyHighlightView         mOverlayView;

    private OnDrawableEventListener mDrawableListener;

    private boolean                 mForceSingleSelection = true;

    private Paint                   mDropPaint;

    private Rect                    mTempRect             = new Rect();

    private boolean                 mScaleWithContent     = false;

    private List<LabelView>         labels                = new ArrayList<LabelView>();
    //当前被点击的标签
    private LabelView               currentLabel;
    //标签被点击的处与基本坐标的距离
    private float                   labelX, labelY, downLabelX, downLabelY;

    /************************[BEGIN]贴纸处理**********************/
    /**
     * 用于感知label被点击了
     * @param label
     * @param locationX
     * @param locationY
     */
    //贴纸在上面进行操作
    public synchronized void setCurrentLabel(LabelView label, float eventRawX, float eventRawY) {
        if (labels.contains(label)) {
            currentLabel = label;
            int[] location = new int[2];
            label.getLocationOnScreen(location);
            labelX = eventRawX - location[0];
            labelY = eventRawY - location[1];

            downLabelX = eventRawX;
            downLabelY = eventRawY;
        } else if (label == null) {
            currentLabel = null;
        }
    }

    public synchronized void addLabel(LabelView label) {
        labels.add(label);
    }

    public synchronized void removeLabel(LabelView label) {
        currentLabel = null;
        labels.remove(label);
    }

    public synchronized void clearAnimation() {
        for (View v : labels) {
            v.clearAnimation();
        }
    }

    public synchronized void wave() {
        for (LabelView label : labels) {
            label.wave();
        }
    }

    private boolean mLabelMovingEnabled = true;

    public void disableLabelMoving() {
        mLabelMovingEnabled = false;
    }

    public void enableLabelMoving() {
        mLabelMovingEnabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("Log8", "onTouchEvent");

        if (currentLabel != null) {
            if (mLabelMovingEnabled) {
                currentLabel.updateLocation((int) (event.getX() - labelX),
                    (int) (event.getY() - labelY));
                currentLabel.invalidate();
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:// 手指离开时 
                case MotionEvent.ACTION_CANCEL:
                    Log.i("Log8", "onTouchEvent: up/cancel");

                    float upX = event.getRawX();
                    float upY = event.getRawY();
                    double distance = Math.sqrt(Math.abs(upX - downLabelX)
                                                * Math.abs(upX - downLabelX)
                                                + Math.abs(upY - downLabelY)
                                                * Math.abs(upY - downLabelY));//两点之间的距离
                    if (distance < 15) { // 距离较小，当作click事件来处理
                        Log.i("Log8", "onTouchEvent: click");
                        if (mDrawableListener != null) {
                            Log.i("Log8", "onTouchEvent: click label");
                            mDrawableListener.onClick(currentLabel);
                        }
                        if (currentLabel != null) {
                            Log.i("Log8", "onTouchEvent: currentLabel.onClick");
                            currentLabel.onClick(); // Allow perform click listener for each label
                        }
                    } else {
                        if (mDrawableListener != null) {
                            mDrawableListener.onUp(currentLabel, (int) event.getX(), (int) event.getY());
                        }
                        if (currentLabel != null) currentLabel.onUp((int) event.getX(), (int) event.getY());
                    }

                    if (mLabelMovingEnabled) {
                        Log.i("Log8", "onTouchEvent: currentLabel := null");
                        currentLabel = null; // delete onClick
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    /************************[END]贴纸处理**********************/

    public MyImageViewDrawableOverlay(Context context) {
        super(context);
        //setScrollEnabled(false);
    }

    public MyImageViewDrawableOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        //setScrollEnabled(false);
    }

    public MyImageViewDrawableOverlay(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //setScrollEnabled(false);
    }

    protected void panBy(double dx, double dy) {
        RectF rect = getBitmapRect();
        mScrollRect.set((float) dx, (float) dy, 0, 0);
        updateRect(rect, mScrollRect);
        //FIXME 贴纸移动到边缘次数多了以后会爆,原因不明朗  。后续需要好好重写ImageViewTouch
        //postTranslate(mScrollRect.left, mScrollRect.top);
        center(true, true);
    }

    @Override
    protected void init(Context context, AttributeSet attrs, int defStyle) {
        super.init(context, attrs, defStyle);
        mTouchSlop = ViewConfiguration.get(context).getScaledDoubleTapSlop();
        mGestureDetector.setIsLongpressEnabled(false);
    }

    /**
     * How overlay content will be scaled/moved
     * when zomming/panning the base image
     * 
     * @param value
     *            true if content will scale according to the image
     */
    public void setScaleWithContent(boolean value) {
        mScaleWithContent = value;
    }

    public boolean getScaleWithContent() {
        return mScaleWithContent;
    }

    /**
     * If true, when the user tap outside the drawable overlay and
     * there is only one active overlay selection is not changed.
     * 
     * @param value
     *            the new force single selection
     */
    public void setForceSingleSelection(boolean value) {
        mForceSingleSelection = value;
    }

    public void setOnDrawableEventListener(OnDrawableEventListener listener) {
        mDrawableListener = listener;
    }

    @Override
    public void setImageDrawable(android.graphics.drawable.Drawable drawable,
                                 Matrix initial_matrix, float min_zoom, float max_zoom) {
        super.setImageDrawable(drawable, initial_matrix, min_zoom, max_zoom);
    }

    @Override
    protected void onLayoutChanged(int left, int top, int right, int bottom) {
        super.onLayoutChanged(left, top, right, bottom);

        if (getDrawable() != null) {

            Iterator<MyHighlightView> iterator = mOverlayViews.iterator();
            while (iterator.hasNext()) {
                MyHighlightView view = iterator.next();
                view.getMatrix().set(getImageMatrix());
                view.invalidate();
            }
        }
    }

    @Override
    public void postTranslate(float deltaX, float deltaY) {
        super.postTranslate(deltaX, deltaY);

        Iterator<MyHighlightView> iterator = mOverlayViews.iterator();
        while (iterator.hasNext()) {
            MyHighlightView view = iterator.next();
            if (getScale() != 1) {
                float[] mvalues = new float[9];
                getImageMatrix().getValues(mvalues);
                final float scale = mvalues[Matrix.MSCALE_X];

                if (!mScaleWithContent)
                    view.getCropRectF().offset(-deltaX / scale, -deltaY / scale);
            }

            view.getMatrix().set(getImageMatrix());
            view.invalidate();
        }
    }

    @Override
    protected void postScale(float scale, float centerX, float centerY) {

        if (mOverlayViews.size() > 0) {
            Iterator<MyHighlightView> iterator = mOverlayViews.iterator();

            Matrix oldMatrix = new Matrix(getImageViewMatrix());
            super.postScale(scale, centerX, centerY);

            while (iterator.hasNext()) {
                MyHighlightView view = iterator.next();

                if (!mScaleWithContent) {
                    RectF cropRect = view.getCropRectF();
                    RectF rect1 = view.getDisplayRect(oldMatrix, view.getCropRectF());
                    RectF rect2 = view.getDisplayRect(getImageViewMatrix(), view.getCropRectF());

                    float[] mvalues = new float[9];
                    getImageViewMatrix().getValues(mvalues);
                    final float currentScale = mvalues[Matrix.MSCALE_X];

                    cropRect.offset((rect1.left - rect2.left) / currentScale,
                        (rect1.top - rect2.top) / currentScale);
                    cropRect.right += -(rect2.width() - rect1.width()) / currentScale;
                    cropRect.bottom += -(rect2.height() - rect1.height()) / currentScale;

                    view.getMatrix().set(getImageMatrix());
                    view.getCropRectF().set(cropRect);
                } else {
                    view.getMatrix().set(getImageMatrix());
                }
                view.invalidate();
            }
        } else {
            super.postScale(scale, centerX, centerY);
        }
    }

    private void ensureVisible(MyHighlightView hv, float deltaX, float deltaY) {
        RectF r = hv.getDrawRect();
        int panDeltaX1 = 0, panDeltaX2 = 0;
        int panDeltaY1 = 0, panDeltaY2 = 0;

        if (deltaX > 0)
            panDeltaX1 = (int) Math.max(0, getLeft() - r.left);
        if (deltaX < 0)
            panDeltaX2 = (int) Math.min(0, getRight() - r.right);

        if (deltaY > 0)
            panDeltaY1 = (int) Math.max(0, getTop() - r.top);

        if (deltaY < 0)
            panDeltaY2 = (int) Math.min(0, getBottom() - r.bottom);

        int panDeltaX = panDeltaX1 != 0 ? panDeltaX1 : panDeltaX2;
        int panDeltaY = panDeltaY1 != 0 ? panDeltaY1 : panDeltaY2;

        if (panDeltaX != 0 || panDeltaY != 0) {
            panBy(panDeltaX, panDeltaY);
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.i("Log8", "onSingleTapConfirmed");

        boolean selected = false;
        // iterate the items and post a single tap event to the selected item
        Iterator<MyHighlightView> iterator = mOverlayViews.iterator();
        while (iterator.hasNext()) {
            MyHighlightView view = iterator.next();
            if (view.isSelected()) {
                view.onSingleTapConfirmed(e.getX(), e.getY());
                postInvalidate();
                selected = true;
            }
        }
        // cannot avoid setSingleUpListener();
        //if (selected) return true;
        return super.onSingleTapConfirmed(e);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.i("Log8", "onDown");

        mScrollStarted = false;
        mLastMotionScrollX = e.getX();
        mLastMotionScrollY = e.getY();

        // return the item being clicked
        MyHighlightView newSelection = checkSelection(e);
        MyHighlightView realNewSelection = newSelection;

        if (newSelection == null && mOverlayViews.size() == 1 && mForceSingleSelection) {
            // force a selection if none is selected, when force single selection is
            // turned on
            newSelection = mOverlayViews.get(0);
        }

        setSelectedHighlightView(newSelection);

        if (realNewSelection != null && mScaleWithContent) {
            RectF displayRect = realNewSelection.getDisplayRect(realNewSelection.getMatrix(),
                realNewSelection.getCropRectF());
            boolean invalidSize = realNewSelection.getContent().validateSize(displayRect);

            Log.d("Log8", "invalidSize: " + invalidSize);

            if (!invalidSize) {
                Log.w("Log8", "drawable too small!!!");

                float minW = realNewSelection.getContent().getMinWidth();
                float minH = realNewSelection.getContent().getMinHeight();

                Log.d("Log8", "minW: " + minW);
                Log.d("Log8", "minH: " + minH);

                float minSize = Math.min(minW, minH) * 1.1f;

                Log.d("Log8", "minSize: " + minSize);

                float minRectSize = Math.min(displayRect.width(), displayRect.height());

                Log.d("Log8", "minRectSize: " + minRectSize);

                float diff = minSize / minRectSize;

                Log.d("Log8", "diff: " + diff);

                Log.d("Log8", "min.size: " + minW + "x" + minH);
                Log.d("Log8", "cur.size: " + displayRect.width() + "x" + displayRect.height());
                Log.d("Log8", "zooming to: " + (getScale() * diff));

                zoomTo(getScale() * diff, displayRect.centerX(), displayRect.centerY(),
                    DEFAULT_ANIMATION_DURATION * 1.5f);
                return true;
            }
        }

        if (mOverlayView != null) {
            //通过触摸区域得到Mode
            int edge = mOverlayView.getHit(e.getX(), e.getY());
            if (edge != MyHighlightView.NONE) {
                mOverlayView.setMode((edge == MyHighlightView.MOVE) ? MyHighlightView.MOVE
                    : (edge == MyHighlightView.ROTATE ? MyHighlightView.ROTATE
                        : MyHighlightView.GROW));
                postInvalidate();
                if (mDrawableListener != null) {
                    mDrawableListener.onDown(mOverlayView);
                }
            }
        }

        return super.onDown(e);
    }

    public float getmLastMotionScrollX() {
        return mLastMotionScrollX;
    }

    public float getmLastMotionScrollY() {
        return mLastMotionScrollY;
    }

    @Override
    public boolean onUp(MotionEvent e) {
        Log.i("Log8", "onUp");

        if (mOverlayView != null) {
            mOverlayView.setMode(MyHighlightView.NONE);
            postInvalidate();
        }
        return super.onUp(e);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.i("Log8", "onSingleTapUp");

        if (mOverlayView != null) {
            Log.i("Log8", "onSingleTapUp: mOverlayView != null");

            int edge = mOverlayView.getHit(e.getX(), e.getY());
            if ((edge & MyHighlightView.MOVE) == MyHighlightView.MOVE) {
                if (mDrawableListener != null) {
                    Log.i("Log8", "onSingleTapUp: click");
                    mDrawableListener.onClick(mOverlayView);
                }
                return true;
            }

            Log.i("Log8", "onSingleTapUp: !MOVE");
            // cannot avoid setSingleUpListener();
            // if (edge != MyHighlightView.NONE) return true;

            mOverlayView.setMode(MyHighlightView.NONE);
            postInvalidate();

            Log.d("Log8", "selected items: " + mOverlayViews.size());

            if (mOverlayViews.size() != 1) {
                setSelectedHighlightView(null);
            }
        }

        // cannot avoid setSingleUpListener();
        //if (checkSelection(e) != null) return true;

        return super.onSingleTapUp(e);
    }

    boolean mScrollStarted;
    float   mLastMotionScrollX, mLastMotionScrollY;

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.i("Log8", "onScroll");

        float dx, dy;

        float x = e2.getX();
        float y = e2.getY();

        if (!mScrollStarted) {
            dx = 0;
            dy = 0;
            mScrollStarted = true;
        } else {
            dx = mLastMotionScrollX - x;
            dy = mLastMotionScrollY - y;
        }

        mLastMotionScrollX = x;
        mLastMotionScrollY = y;

        if (mOverlayView != null && mOverlayView.getMode() != MyHighlightView.NONE) {
            mOverlayView.onMouseMove(mOverlayView.getMode(), e2, -dx, -dy);
            postInvalidate();

            if (mDrawableListener != null) {
                mDrawableListener.onMove(mOverlayView, mLastMotionScrollX, mLastMotionScrollY);
            }

            if (mOverlayView.getMode() == MyHighlightView.MOVE) {
                if (!mScaleWithContent) {
                    ensureVisible(mOverlayView, distanceX, distanceY);
                }
            }
            return true;
        } else {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.i("Log8", "onFling");

        if (mOverlayView != null && mOverlayView.getMode() != MyHighlightView.NONE)
            return false;
        return super.onFling(e1, e2, velocityX, velocityY);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        boolean shouldInvalidateAfter = false;

        for (int i = 0; i < mOverlayViews.size(); i++) {
            canvas.save(Canvas.MATRIX_SAVE_FLAG);

            MyHighlightView current = mOverlayViews.get(i);
            current.draw(canvas);

            // check if we should invalidate again the canvas
            if (!shouldInvalidateAfter) {
                FeatherDrawable content = current.getContent();
                if (content instanceof EditableDrawable) {
                    if (((EditableDrawable) content).isEditing()) {
                        shouldInvalidateAfter = true;
                    }
                }
            }

            canvas.restore();
        }

        if (null != mDropPaint) {
            getDrawingRect(mTempRect);
            canvas.drawRect(mTempRect, mDropPaint);
        }

        if (shouldInvalidateAfter) {
            postInvalidateDelayed(EditableDrawable.CURSOR_BLINK_TIME);
        }
    }

    public synchronized void clearOverlays() {
        synchronized (mOverlayViews) {
        Log.i("Log8", "clearOverlays");
        setSelectedHighlightView(null);
        while (mOverlayViews.size() > 0) {
            MyHighlightView hv = mOverlayViews.remove(0);
            hv.dispose();
        }
        mOverlayView = null;
        }
    }

    public boolean addHighlightView(MyHighlightView hv) {
        for (int i = 0; i < mOverlayViews.size(); i++) {
            if (mOverlayViews.get(i).equals(hv))
                return false;
        }
        mOverlayViews.add(hv);
        postInvalidate();

        if (mOverlayViews.size() == 1) {
            setSelectedHighlightView(hv);
        }

        return true;
    }

    public int getHighlightCount() {
        return mOverlayViews.size();
    }

    public MyHighlightView getHighlightViewAt(int index) {
        return mOverlayViews.get(index);
    }

    public boolean removeHightlightView(MyHighlightView view) {
        Log.i("Log8", "removeHightlightView");
        for (int i = 0; i < mOverlayViews.size(); i++) {
            if (mOverlayViews.get(i).equals(view)) {
                MyHighlightView hv = mOverlayViews.remove(i);
                if (hv.equals(mOverlayView)) {
                    setSelectedHighlightView(null);
                }
                hv.dispose();
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onZoomAnimationCompleted(float scale) {
        Log.i("Log8", "onZoomAnimationCompleted: " + scale);
        super.onZoomAnimationCompleted(scale);

        if (mOverlayView != null) {
            mOverlayView.setMode(MyHighlightView.MOVE);
            postInvalidate();
        }
    }

    public MyHighlightView getSelectedHighlightView() {
        return mOverlayView;
    }

    public void commit(Canvas canvas) {

        MyHighlightView hv;
        for (int i = 0; i < getHighlightCount(); i++) {
            hv = getHighlightViewAt(i);
            FeatherDrawable content = hv.getContent();
            if (content instanceof EditableDrawable) {
                ((EditableDrawable) content).endEdit();
            }

            Matrix rotateMatrix = hv.getCropRotationMatrix();
            Rect rect = hv.getCropRect();

            int saveCount = canvas.save(Canvas.MATRIX_SAVE_FLAG);
            canvas.concat(rotateMatrix);
            content.setBounds(rect);
            content.draw(canvas);
            canvas.restoreToCount(saveCount);
        }
    }

    private MyHighlightView checkSelection(MotionEvent e) {
        Iterator<MyHighlightView> iterator = mOverlayViews.iterator();
        MyHighlightView selection = null;
        while (iterator.hasNext()) {
            MyHighlightView view = iterator.next();
            int edge = view.getHit(e.getX(), e.getY());
            if (edge != MyHighlightView.NONE) {
                selection = view;
            }
        }
        return selection;
    }

    public void setSelectedHighlightView(MyHighlightView newView) {

        final MyHighlightView oldView = mOverlayView;

        if (mOverlayView != null && !mOverlayView.equals(newView)) {
            mOverlayView.setSelected(false);
        }

        if (newView != null) {
            newView.setSelected(true);
        }

        postInvalidate();

        mOverlayView = newView;

        if (mDrawableListener != null) {
            mDrawableListener.onFocusChange(newView, oldView);
        }
    }

    public void clear() {
        clearOverlays();
    }

    // clear with labels
    public synchronized void clear(ViewGroup container) {
        synchronized (labels) {
            currentLabel = null;
            for (LabelView label : labels) container.removeView(label);
            labels.clear();
            clearOverlays();
            // Avoid ConcurrentModificationException
            /*
            for (Iterator it = labels.iterator(); it.hasNext();) {
                LabelView label = (LabelView) it.next();
                if (label != null) {
                    removeLabel(label);
                }
            }
            */
            /* ConcurrentModificationException
            for (LabelView label : labels) {
                container.removeView(label);
                removeLabel(label);
            }
            */
            // Collections.synchronizedList
            // synchronized (synchronizedList) {
            //   while (iterator.hasNext()) {
            //   }
            // }
        }
    }

    public void removeSticker(MyHighlightView hv) {
        removeHightlightView(hv);
        invalidate();
    }

    public MyHighlightView addSticker(@DrawableRes int res, final MyHighlightView.OnDeleteClickListener onDelete) {
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), res);
        if (bitmap == null) {
            return null;
        }
        return addSticker(bitmap, onDelete);
    }

    public MyHighlightView addSticker(final Bitmap bitmap, final MyHighlightView.OnDeleteClickListener onDelete) {
        StickerDrawable drawable = new StickerDrawable(getContext().getResources(), bitmap);
        return addSticker(drawable, onDelete);
    }

    public MyHighlightView addSticker(final StickerDrawable drawable, final MyHighlightView.OnDeleteClickListener onDelete) {
        drawable.setAntiAlias(true);
        drawable.setMinSize(30, 30);

        final MyHighlightView hv = new MyHighlightView(this, drawable);
        //设置贴纸padding
        hv.setPadding(10);
        hv.setOnDeleteClickListener(new MyHighlightView.OnDeleteClickListener() {

            @Override
            public void onDeleteClick() {
                removeSticker(hv);
                onDelete.onDeleteClick();
            }
        });

        Matrix imageMatrix = getImageViewMatrix();

        int cropWidth, cropHeight;
        int x, y;

        final int width = getWidth();
        final int height = getHeight();

        // width/height of the sticker
        cropWidth = (int) drawable.getCurrentWidth();
        cropHeight = (int) drawable.getCurrentHeight();

        final int cropSize = Math.max(cropWidth, cropHeight);
        final int screenSize = Math.min(getWidth(), getHeight());
        RectF positionRect = null;
        if (cropSize > screenSize) {
            float ratio;
            float widthRatio = (float) getWidth() / cropWidth;
            float heightRatio = (float) getHeight() / cropHeight;

            if (widthRatio < heightRatio) {
                ratio = widthRatio;
            } else {
                ratio = heightRatio;
            }

            cropWidth = (int) ((float) cropWidth * (ratio / 2));
            cropHeight = (int) ((float) cropHeight * (ratio / 2));

            int w = getWidth();
            int h = getHeight();
            positionRect = new RectF(w / 2 - cropWidth / 2, h / 2 - cropHeight / 2,
                    w / 2 + cropWidth / 2, h / 2 + cropHeight / 2);

            positionRect.inset((positionRect.width() - cropWidth) / 2,
                    (positionRect.height() - cropHeight) / 2);
        }

        if (positionRect != null) {
            x = (int) positionRect.left;
            y = (int) positionRect.top;

        } else {
            x = (width - cropWidth) / 2;
            y = (height - cropHeight) / 2;
        }

        Matrix matrix = new Matrix(imageMatrix);
        matrix.invert(matrix);

        float[] pts = new float[] { x, y, x + cropWidth, y + cropHeight };
        MatrixUtils.mapPoints(matrix, pts);

        RectF cropRect = new RectF(pts[0], pts[1], pts[2], pts[3]);
        Rect imageRect = new Rect(0, 0, width, height);

        hv.setup(getContext(), imageMatrix, imageRect, cropRect, false);

        addHighlightView(hv);
        setSelectedHighlightView(hv);
        return hv;
    }

    //----添加标签-----
    public void addLabelEditable(ViewGroup container, LabelView label, int left, int top) {
        addLabel(container, label, left, top);
        addLabel2Overlay(label);
    }

    private void addLabel(ViewGroup container, LabelView label, int left, int top) {
        label.addTo(container, left, top);
    }

    public void removeLabelEditable(ViewGroup container, LabelView label) {
        container.removeView(label);
        removeLabel(label);
    }

    /**
     * 使标签在Overlay上可以移动
     * @param overlay
     * @param label
     */
    private void addLabel2Overlay(final LabelView label) {
        //添加事件，触摸生效
        addLabel(label);
        label.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:// 手指按下时
                        setCurrentLabel(label, event.getRawX(), event.getRawY());
                        return false;
                    default:
                        return false;
                }
            }
        });
    }

    // TODO ImmutableList
    public List<MyHighlightView> highlightViews() {
        return mOverlayViews;
    }

}
