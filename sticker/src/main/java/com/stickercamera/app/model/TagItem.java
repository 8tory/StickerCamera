package com.stickercamera.app.model;



import java.io.Serializable;

public class TagItem implements Serializable {
    private static final long serialVersionUID = 2685507991821634905L;
    private long              id;
    private int               type;
    private String            name;
    private double            x                = -1;
    private double            y                = -1;

    private int recordCount;
    private boolean           left             = true;
    
    
    public boolean isLeft() {
        return left;
    }
    public void setLeft(boolean left) {
        this.left = left;
    }
    public int getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}

	public TagItem() {

    }

    public TagItem(int type, String label) {
        this.type = type;
        this.name = label;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getX() {
        return x;
    }

    /**
     * DONT setX && setY(), setPosition(x, y) instead for OnMoveListener.call(x, y)
     */
    public void setX(double x) {
        boolean posChanged = !(this.x == x);
        this.x = x;
        if (posChanged && onMove != null) onMove.call(this, x, y);
    }

    public double getY() {
        return y;
    }

    /**
     * DONT setX && setY(), setPosition(x, y) instead for OnMoveListener.call(x, y)
     */
    public void setY(double y) {
        boolean posChanged = !(this.y == y);
        this.y = y;
        if (posChanged && onMove != null) onMove.call(this, x, y);
    }

    public void setPosition(double x, double y) {
        android.util.Log.d("Log8", "TagItem.setPosition:");
        boolean posChanged = !(this.x == x && this.y == y);
        android.util.Log.d("Log8", "TagItem.setPosition: posChanged: " + posChanged);
        this.x = x;
        this.y = y;
        android.util.Log.d("Log8", "TagItem.setPosition: onMove: " + onMove);
        android.util.Log.d("Log8", "TagItem.setPosition: this: " + this);
        if (posChanged && onMove != null) {
            android.util.Log.d("Log8", "TagItem.setPosition: onMove: " + x);
            android.util.Log.d("Log8", "TagItem.setPosition: onMove: " + y);
            onMove.call(this, x, y);
        }
    }

    public interface Action3<T, T2, T3> {
        void call(T t, T2 t2, T3 t3);
    }

    public interface Action1<T> {
        void call(T t);
    }

    public interface ItemAction3 extends Action3<TagItem, Double, Double> {
    }

    public interface ItemAction1 extends Action1<TagItem> {
    }

    public interface OnItemMoveListener extends ItemAction3 {
    }

    public interface OnItemUpListener extends ItemAction3 {
    }

    public interface OnItemRemoveListener extends ItemAction1 {
    }

    OnItemMoveListener onMove;

    public void onMove(OnItemMoveListener onMove) {
        this.onMove = onMove;
    }

    OnItemUpListener onUp;

    public void onUp(OnItemUpListener onUp) {
        this.onUp = onUp;
    }

    public void onUp(double x, double y) {
        if (onUp == null) return;
        onUp.call(this, x, y);
    }

    OnItemRemoveListener onRemove;

    public void onRemove(OnItemRemoveListener onRemove) {
        this.onRemove = onRemove;
    }

    public void onRemove() {
        if (onRemove != null) {
            onRemove.call(this);
        }
    }

}
