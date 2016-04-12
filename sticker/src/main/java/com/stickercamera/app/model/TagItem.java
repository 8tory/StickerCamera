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
     * DONT setX && setY(), setPosition(x, y) instead for OnMoveListener.onMove(x, y)
     */
    public void setX(double x) {
        boolean posChanged = !(this.x == x);
        this.x = x;
        if (posChanged) onMove.onMove(this, x, y);
    }

    public double getY() {
        return y;
    }

    /**
     * DONT setX && setY(), setPosition(x, y) instead for OnMoveListener.onMove(x, y)
     */
    public void setY(double y) {
        boolean posChanged = !(this.y == y);
        this.y = y;
        if (posChanged) onMove.onMove(this, x, y);
    }

    public void setPosition(double x, double y) {
        boolean posChanged = !(this.x == x && this.y == y);
        this.x = x;
        this.y = y;
        if (posChanged) onMove.onMove(this, x, y);
    }

    public interface OnMoveListener {
        void onMove(TagItem item, double x, double y);
    }

    OnMoveListener onMove;

    public void setOnMoveListener(OnMoveListener onMove) {
        this.onMove = onMove;
    }

    public void onMove(OnMoveListener onMove) {
        this.onMove = onMove;
    }

}
