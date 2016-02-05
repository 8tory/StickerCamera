package com.customview;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.common.util.DistanceUtil;
import com.common.util.FileUtils;
import com.common.util.ImageUtils;
import com.common.util.StringUtils;
import com.common.util.TimeUtils;
import com.customview.LabelSelector;
import com.customview.LabelView;
import com.customview.MyHighlightView;
import com.customview.MyImageViewDrawableOverlay;
import com.github.skykai.stickercamera.R;
import com.stickercamera.app.camera.adapter.FilterAdapter;
import com.stickercamera.app.camera.adapter.StickerToolAdapter;
import com.stickercamera.app.camera.effect.FilterEffect;
import com.stickercamera.app.camera.EffectService;
import com.stickercamera.app.camera.util.GPUImageFilterTools;
import com.stickercamera.AppConstants;
import com.stickercamera.app.model.Addon;
import com.stickercamera.app.model.FeedItem;
import com.stickercamera.app.model.TagItem;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.widget.HListView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;
import com.afollestad.materialdialogs.MaterialDialog;
import android.text.InputType;
import android.support.annotation.DrawableRes;

public class StickerManager {
    private ViewGroup parent;

    private MyImageViewDrawableOverlay mImageView;

    private LabelSelector labelSelector;

    private LabelView emptyLabelView;
    private String mId;
    private OnAdd mOnAdd;

    // TODO merge into Overlay
    private List<LabelView> labels = new ArrayList<LabelView>();

    //标签区域
    //private View commonLabelArea;

    public StickerManager setId(String id) {
        mId = id;
        return this;
    }

    public StickerManager onAdd(OnAdd onAdd) {
        mOnAdd = onAdd;
        return this;
    }

    public StickerManager(ViewGroup parent) {
        this(parent, true);
    }

    public StickerManager(ViewGroup parent, boolean interactive) {
        this.parent = parent;
        //public RelativeLayout parent;

        //添加贴纸水印的画布
        View overlay = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.view_drawable_overlay, null);
        mImageView = (MyImageViewDrawableOverlay) overlay.findViewById(R.id.drawable_overlay);
        ViewGroup.LayoutParams params = null;
        RelativeLayout.LayoutParams rparams = null;
        if (interactive) {
            params = new ViewGroup.LayoutParams(DistanceUtil.getInstance(parent.getContext()).getScreenWidth(),
                    DistanceUtil.getInstance(parent.getContext()).getScreenWidth());
            rparams = new RelativeLayout.LayoutParams(DistanceUtil.getInstance(parent.getContext()).getScreenWidth(), DistanceUtil.getInstance(parent.getContext()).getScreenWidth());
        } else {
            params = new ViewGroup.LayoutParams(parent.getWidth(), parent.getHeight());
            rparams = new RelativeLayout.LayoutParams(parent.getWidth(), parent.getHeight());
        }
        mImageView.setLayoutParams(params);
        overlay.setLayoutParams(params);
        parent.addView(overlay);

        labelSelector = new LabelSelector(parent.getContext());
        labelSelector.setLayoutParams(rparams);
        parent.addView(labelSelector);
        labelSelector.hide();

        //初始化空白标签
        emptyLabelView = new LabelView(parent.getContext());
        emptyLabelView.setEmpty();
        mImageView.addLabelEditable(parent, emptyLabelView, mImageView.getWidth() / 2, mImageView.getWidth() / 2);
        emptyLabelView.setVisibility(View.INVISIBLE);

        labelSelector.setTxtClicked(v -> {
            new MaterialDialog.Builder(parent.getContext())
                .title(R.string.comment)
                //.inputRangeRes(2, 20, R.color.material_red_500)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("Say something..", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        TagItem tagItem = new TagItem(0, input.toString());
                        addLabel(tagItem);
                    }
                }).show();
        });
        labelSelector.setAddrClicked(v -> {
            new MaterialDialog.Builder(parent.getContext())
                .title(R.string.place)
                //.inputRangeRes(2, 20, R.color.material_red_500)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("Place", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        TagItem tagItem = new TagItem(1, input.toString());
                        addLabel(tagItem);
                    }
                }).show();
        });

        mImageView.setOnDrawableEventListener(new MyImageViewDrawableOverlay.OnDrawableEventListener() {
            @Override
            public void onMove(MyHighlightView view) {
            }

            @Override
            public void onFocusChange(MyHighlightView newFocus, MyHighlightView oldFocus) {
            }

            @Override
            public void onDown(MyHighlightView view) {

            }

            @Override
            public void onClick(MyHighlightView view) {
                labelSelector.hide();
                //mImageView.removeSticker(view); // deleteIcon instead
            }

            @Override
            public void onClick(final LabelView label) {
                if (label.equals(emptyLabelView)) {
                    return;
                }
                mImageView.removeLabelEditable(parent, label);
                labels.remove(label);
            }
        });
        mImageView.setSingleTapListener(() -> {
            emptyLabelView.updateLocation((int) mImageView.getmLastMotionScrollX(),
                    (int) mImageView.getmLastMotionScrollY());
            emptyLabelView.setVisibility(View.VISIBLE);
            emptyLabelView.wave();

            //labelSelector.showToTop();
            parent.postInvalidate();
        });

        labelSelector.setOnClickListener(v -> {
            labelSelector.hide();
            emptyLabelView.updateLocation((int) labelSelector.getmLastTouchX(),
                    (int) labelSelector.getmLastTouchY());
            emptyLabelView.setVisibility(View.VISIBLE);
        });
    }

    public MyHighlightView addSticker() {
        return addSticker(R.drawable.fb_smile);
    }

    public MyHighlightView addSticker(@DrawableRes int drawableId) {
        return addSticker(new Addon(drawableId));
    }

    //删除贴纸的回调接口
    public static interface StickerCallback {
        public void onRemoveSticker(Addon sticker);
    }

    public static interface OnAdd {
        public void onAdd(String id, String text, int left, int top);
    }

    public MyHighlightView addSticker(Addon sticker) {
        return addSticker(sticker, new StickerCallback() {
            @Override
            public void onRemoveSticker(Addon sticker) {
                labelSelector.hide();
            }
        });
    }

    public MyHighlightView addSticker(final Addon sticker, final StickerCallback stickerCallback) {
        return mImageView.addSticker(sticker.getId(), new MyHighlightView.OnDeleteClickListener() {
            @Override
            public void onDeleteClick() {
                stickerCallback.onRemoveSticker(sticker);
            }
        });
    }

    public MyImageViewDrawableOverlay imageView() {
        return mImageView;
    }

    // TODO highlight() { setSelectedHighlightView(getLastHighlightView())}

    public void offHighlight() {
        mImageView.setSelectedHighlightView(null);
        //emptyLabelView.clearAnimation();
        emptyLabelView.setVisibility(View.INVISIBLE);
        //mImageView.clearAnimation();
    }

    public void inactive() {
        labelSelector.hide();
        emptyLabelView.setVisibility(View.GONE);
    }

    public void unfocus() {
        labelSelector.hide();
        emptyLabelView.setVisibility(View.INVISIBLE);
    }

    public void onPause() {
        mImageView.setVisibility(View.INVISIBLE);
        emptyLabelView.setVisibility(View.INVISIBLE);
        hide();
        labelSelector.hide();
    }

    public void onResume() {
        unhide();
        mImageView.setVisibility(View.VISIBLE);
        emptyLabelView.setVisibility(View.VISIBLE);
    }

    public void addLabel() {
        labelSelector.showToTop();
    }

    public void hideLabelSelector() {
        labelSelector.hide();
    }

    public LabelSelector labelSelector() {
        return labelSelector;
    }

    public List<LabelView> labels() {
        return labels;
    }

    public void unhide() {
        for (LabelView v : labels) {
            v.setVisibility(View.VISIBLE);
        }
    }

    public void hide() {
        for (LabelView v : labels) {
            v.setVisibility(View.INVISIBLE);
        }
        emptyLabelView.setVisibility(View.INVISIBLE);
    }

    public void addTextLabel(String text, int left, int top) {
        addTextLabel(text, left, top, null);
    }

    public void addPlaceLabel(String text, int left, int top) {
        addPlaceLabel(text, left, top, null);
    }

    public void addTextLabel(String text, int left, int top, OnAdd onAdd) {
        TagItem tagItem = new TagItem(0, text);
        addLabel(tagItem, left, top, onAdd);
    }

    public void addPlaceLabel(String text, int left, int top, OnAdd onAdd) {
        TagItem tagItem = new TagItem(1, text);
        addLabel(tagItem, left, top, onAdd);
    }

    public void addLabel(TagItem tagItem, int left, int top, OnAdd onAdd) {
        if (labels.size() == 0 && left == 0 && top == 0) {
            left = mImageView.getWidth() / 2 - 10;
            top = mImageView.getWidth() / 2;
        }
        LabelView label = new LabelView(parent.getContext());
        label.init(tagItem);
        mImageView.addLabelEditable(parent, label, left, top);
        labels.add(label);
        if (onAdd != null) onAdd.onAdd(mId, tagItem.getName(), left, top);
        if (mOnAdd != null) mOnAdd.onAdd(mId, tagItem.getName(), left, top);
    }

    public void addLabel(TagItem tagItem) {
        labelSelector.hide();
        emptyLabelView.setVisibility(View.INVISIBLE);

        int left = emptyLabelView.getLeft();
        int top = emptyLabelView.getTop();
        if (labels.size() == 0 && left == 0 && top == 0) {
            left = mImageView.getWidth() / 2 - 10;
            top = mImageView.getWidth() / 2;
        }
        LabelView label = new LabelView(parent.getContext());
        label.init(tagItem);
        mImageView.addLabelEditable(parent, label, left, top);
        labels.add(label);
        if (mOnAdd != null) mOnAdd.onAdd(mId, tagItem.getName(), left, top);
    }

}
