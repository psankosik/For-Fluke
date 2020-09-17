package th.co.scbprotect.util;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class SwipeListener implements View.OnTouchListener {
    private GestureDetector gestureDetector = null;

    private class GestureListener extends android.view.GestureDetector.SimpleOnGestureListener
    {
        private final int SWIPE_THRESHOLD = 100;
        private final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            onClick();
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            onDoubleClick();
            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            onLongClick();
            super.onLongPress(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) onSwipeRight();
                        else onSwipeLeft();
                    }
                } else {
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY < 0) onSwipeUp();
                        else onSwipeDown();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    public void onClick() {}
    public void onDoubleClick() {}
    public void onLongClick() {}
    public void onSwipeRight() {}
    public void onSwipeLeft() {}
    public void onSwipeUp() {}
    public void onSwipeDown() {}

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }

    public SwipeListener(Context context)
    {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }
}
