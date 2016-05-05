package com.customview;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.skykai.stickercamera.R;
import com.stickercamera.AppConstants;
import com.stickercamera.app.camera.util.EffectUtil;
import com.stickercamera.app.model.TagItem;
import com.common.util.DistanceUtil;


/**
 * @author tongqian.ni
 */
public class LabelView extends LinearLayout {

    private TagItem tagItem = new TagItem();
    private float parentWidth = 0;
    private float parentHeight = 0;
    private ImageView labelIcon;
    private TextView labelTxtLeft;
    private TextView labelTxtRight;

    public TagItem getTagItem() {
        return this.tagItem;
    }

    public LabelView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.view_label, this);
        labelIcon = (ImageView) findViewById(R.id.label_icon);
        labelTxtLeft = (TextView) findViewById(R.id.label_text_left);
        labelTxtRight = (TextView) findViewById(R.id.label_text_right);
    }

    public LabelView(Context context, AttributeSet attr) {
        super(context, attr);
        LayoutInflater.from(context).inflate(R.layout.view_label, this);
        labelIcon = (ImageView) findViewById(R.id.label_icon);
        labelTxtLeft = (TextView) findViewById(R.id.label_text_left);
        labelTxtRight = (TextView) findViewById(R.id.label_text_right);
    }

    public void init(TagItem tagItem) {
        this.tagItem = tagItem != null ? tagItem : new TagItem();
        labelTxtLeft.setText(tagItem.getName());
        labelTxtRight.setText(tagItem.getName());
        if (this.tagItem.getType() == AppConstants.POST_TYPE_POI) {
            labelIcon.setImageResource(R.drawable.point_poi);
        }
    }

    public interface OnLabelClickListener {
        void onClick(LabelView view);
    }

    OnLabelClickListener onClick;

    public void setOnLabelClickListener(OnLabelClickListener onClick) {
        this.onClick = onClick;
    }

    public void onLabelClick(OnLabelClickListener onClick) {
        this.onClick = onClick;
    }

    public interface Action3<T, T2, T3> {
        void call(T t, T2 t2, T3 t3);
    }

    public interface ViewAction<T extends View> extends Action3<T, Integer, Integer> {
    }

    public interface OnLabelAction extends ViewAction<LabelView> {
    }

    public interface OnLabelMoveListener extends OnLabelAction {
    }

    public interface OnLabelUpListener extends OnLabelAction {
    }

    OnLabelMoveListener onMove;

    public void setOnLabelMoveListener(OnLabelMoveListener onMove) {
        this.onMove = onMove;
    }

    public void onLabelMove(OnLabelMoveListener onMove) {
        this.onMove = onMove;
    }

    OnLabelUpListener onUp;

    public void onUp(OnLabelUpListener onUp) {
        this.onUp = onUp;
    }

    public void onClick() {
        if (onClick == null) return;
        onClick.onClick(this);
    }

    public void onUp(int x, int y) {
        if (onUp != null) {
            onUp.call(this, x, y);
        }
        this.tagItem.onUp(EffectUtil.getStandDis(getContext(), left, this.parentWidth),
                EffectUtil.getStandDis(getContext(), top, this.parentHeight));
    }

    /**
     * 将标签放置于对应RelativeLayout的对应位置，考虑引入postion作为参数？？
     *
     * @param parent
     * @param left
     * @param top
     */
    public void draw(ViewGroup parent, final int left, final int top, boolean isLeft) {
        this.parentWidth = parent.getWidth();
        if (parentWidth <= 0) {
            parentWidth = DistanceUtil.getInstance(getContext()).getScreenWidth();
        }
        setImageWidth((int) parentWidth);
        this.parentHeight = parentWidth;
        if (isLeft) {
            labelTxtRight.setVisibility(View.VISIBLE);
            labelTxtLeft.setVisibility(View.GONE);
            setupLocation(left, top);
            parent.addView(this);
        } else {
            labelTxtRight.setVisibility(View.GONE);
            labelTxtLeft.setVisibility(View.VISIBLE);
            setupLocation(left, top);
            parent.addView(this);
        }

    }

    /**
     * 将标签放置于对应RelativeLayout的对应位置，考虑引入postion作为参数？？
     *
     * @param parent
     * @param left
     * @param top
     */
    public void addTo(ViewGroup parent, final int left, final int top) {
        if (left > parent.getWidth() / 2) {
            this.tagItem.setLeft(false);
        }
        this.parentWidth = parent.getWidth();
        if (parentWidth <= 0) {
            parentWidth = DistanceUtil.getInstance(getContext()).getScreenWidth();
        }
        setImageWidth((int) parentWidth);
        this.parentHeight = parentWidth;
        if (emptyItem) {
            labelTxtRight.setVisibility(View.GONE);
            labelTxtLeft.setVisibility(View.GONE);
            setupLocation(left, top);
            parent.addView(this);
        } else if (this.tagItem.isLeft()) {
            labelTxtRight.setVisibility(View.VISIBLE);
            labelTxtLeft.setVisibility(View.GONE);
            setupLocation(left, top);
            parent.addView(this);
        } else {
            labelTxtRight.setVisibility(View.GONE);
            labelTxtLeft.setVisibility(View.INVISIBLE);
            setupLocation(20, 20);
            parent.addView(this);

            post(new Runnable() {
                @Override
                public void run() {
                    int toLeft = left - getWidth() + labelIcon.getWidth();
                    setupLocation(toLeft, top);
                    labelTxtLeft.setVisibility(View.VISIBLE);
                }
            });
        }

    }

    private void setupLocation(int leftLoc, int topLoc) {
        android.util.Log.d("Log8", "LabelView.setupLocation: ");
        boolean posChanged = !(this.left == leftLoc && this.top == topLoc);
        android.util.Log.d("Log8", "LabelView.setupLocation: posChanged: " + posChanged);
        this.left = leftLoc;
        this.top = topLoc;

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        if (getImageWidth() - left - getWidth() < 0) {
            left = getImageWidth() - getWidth();
        }
        if (getImageWidth() - top - getHeight() < 0) {
            top = getImageWidth() - getHeight();
        }
        if (left < 0 && top < 0) {
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
        } else if (left < 0) {
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.setMargins(0, top, 0, 0);
        } else if (top < 0) {
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            params.setMargins(left, 0, 0, 0);
        } else {
            params.setMargins(left, top, 0, 0);
        }

        this.tagItem.setPosition(EffectUtil.getStandDis(getContext(), left, this.parentWidth),
                EffectUtil.getStandDis(getContext(), top, this.parentHeight));
        setLayoutParams(params);
        if (onMove != null) onMove.call(this, left, top);
    }

    private void setImageWidth(int width) {
        this.imageWidth = width;
    }

    private int getImageWidth() {
        return imageWidth <= 0 ? DistanceUtil.getInstance(getContext()).getScreenWidth() : imageWidth;
    }

    private int left = -1, top = -1;
    private int imageWidth = 0;

    private static final int ANIMATIONEACHOFFSET = 600;

    private boolean emptyItem = false;

    public void setEmpty() {
        emptyItem = true;
        labelTxtLeft.setVisibility(View.GONE);
        labelTxtRight.setVisibility(View.GONE);
    }

    public void wave() {
        AnimationSet as = new AnimationSet(true);
        ScaleAnimation sa = new ScaleAnimation(1f, 1.5f, 1f, 1.5f, ScaleAnimation.RELATIVE_TO_SELF,
                0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(ANIMATIONEACHOFFSET * 3);
        sa.setRepeatCount(10);// 设置循环
        AlphaAnimation aniAlp = new AlphaAnimation(1, 0.1f);
        aniAlp.setRepeatCount(10);// 设置循环
        as.setDuration(ANIMATIONEACHOFFSET * 3);
        as.addAnimation(sa);
        as.addAnimation(aniAlp);
        labelIcon.startAnimation(as);
    }

    public void updateLocation(int x, int y) {
        android.util.Log.d("Log8", "LabelView.updateLocation: ");
        x = x < 0 ? 0 : x;
        y = y < 0 ? 0 : y;
        setupLocation(x, y);
    }
}
