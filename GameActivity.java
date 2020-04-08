package edu.msu.beemanch.exambeemanch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class GameActivity extends Activity implements  GestureDetector.OnGestureListener {

    private GestureDetectorCompat gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gestureDetector = new GestureDetectorCompat(this, this);
    }

    /**
     * Start a new game
     * @param view Current view
     */
    public void onNewGame(View view) {
        getGameView().newGame();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        if (e1.getX() - e2.getX() > 100f) {
            // Left
            getGameView().move(0);
        } else if (e1.getX() - e2.getX() < -100f) {
            // Right
            getGameView().move(1);
        } else if (e1.getY() - e2.getY() > 100f) {
            // Up
            getGameView().move(2);
        } else if (e1.getY() - e2.getY() < -100f) {
            // Down
            getGameView().move(3);
        }

        return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onDown(MotionEvent event) {
        return false;
    }

    public GameView getGameView() { return (GameView)this.findViewById(R.id.gameView); }

}
