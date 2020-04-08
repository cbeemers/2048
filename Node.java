package edu.msu.beemanch.exambeemanch;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.os.Bundle;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Node {

    /**
     * Current color of the node
     */
    private int color = -26317;

    /**
     * Possible colors
     */
    private Integer[] colors = new Integer[] {-26317, -52, -16711731, -10027213, -39169, -3276851, -6749953, -16737844, -3355393, -16122615, -16711681};

    /**
     * -10027213
     */

    /**
     * If node has been added to this swipe
     */
    private boolean added = false;

    /**
     * Row index in the grid
     */
    private int row;

    /**
     * Column index in the grid
     */
    private int col;

    /**
     * Draws the node box
     */
    private Paint paint;

    /**
     * Draws the node value
     */
    private Paint textPaint;

    /**
     * Value of the node
     */
    private int value;
    /**
     * X location of node
     */
    private float x;
    /**
     * Y location of node
     */
    private float y;

    /**
     * Constructor
     * @param value Value of the node
     */
    public Node(int value, int row, int col) {
        this.value = value;
        setColor();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColorFilter(new LightingColorFilter(color, 0));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(2);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(70f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        x = .125f * (2*col+1);
        y = .125f * (2*row+1);

        this.row = row;
        this.col = col;

    }

    /**
     * Set the value of the node
     * @param value value being added on existing value
     */
    public void setValue(int value) {
        this.value = value;
        setColor();
    }


    public void setColor () {
        this.color = colors[((int)(Math.log(value) / Math.log(2))-1)%11];
    }

    /**
     * Get current value of node
     * @return
     */
    public int getValue() { return value; }

    /**
     * Set x and y location of node based on row, col index
     * @param row
     * @param col
     */
    public void setLocation(int row, int col) {
        this.row = row;
        this.col = col;

        x = .125f * (2*col+1);
        y = .125f * (2*row+1);
    }

    /**
     * Get the column this node is in
     * @return
     */
    public int getCol() {
        return col;
    }

    /**
     * Get the column this node is in
     * @return
     */
    public int getRow() { return row; }

    /**
     * Set
     * @param add
     */
    public void setAdded(boolean add) { added = add; }

    /**
     * Whether or not this node has been added on this turn
     * @return
     */
    public boolean getAdded() { return added; }

    /**
     * Draw the puzzle piece
     * @param canvas Canvas we are drawing on
     * @param marginX Margin x value in pixels
     * @param marginY Margin y value in pixels
     * @param gameSize Size we draw the puzzle in pixels
     * @param scaleFactor Amount we scale the puzzle pieces when we draw them
     */
    public void draw(Canvas canvas, int marginX, int marginY,
                     int gameSize, float scaleFactor, float wid, float hit) {

        canvas.save();

        // Convert x,y to pixels and add the margin, then draw
        canvas.translate(marginX + x * gameSize, marginY + y * gameSize);

        // Scale it to the right size
        canvas.scale(scaleFactor, scaleFactor);

        // This magic code makes the center of the piece at 0, 0
        canvas.translate(-wid / 2f, -hit / 2f);

        // Draw the actual node
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(x, y, x+wid, y+hit, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        canvas.drawRect(x, y, x+wid, y+hit, paint);

        canvas.drawText(Integer.toString(value), x+wid/2, y+hit/2, textPaint);

        canvas.restore();
    }


}
