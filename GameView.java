package edu.msu.beemanch.exambeemanch;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.core.view.GestureDetectorCompat;

public class GameView extends View {

    public GameView(Context context) {
        super(context);
        init(null, 0);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        game = new Game(getContext());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        game.draw(canvas);
        if (game.checkGameOver()) {
            this.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(),
                            R.string.game_over,
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    /**
     * Perform a swipe move
     * @param move Direction of swipe
     */
    public void move(int move) {
        if (game.move(move)) {

            game.addNewNode();
            game.nextTurn();
            game.setMoved();

        }
        invalidate();

    }

    /**
     * Start a new game
     */
    public void newGame() {
        this.game = new Game(getContext());
        invalidate();
    }

    /**
     * The current game
     */
    private Game game;
}
