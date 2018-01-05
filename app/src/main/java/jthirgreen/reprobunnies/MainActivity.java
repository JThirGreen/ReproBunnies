package jthirgreen.reprobunnies;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import jthirgreen.reprobunnies.Utilities.jToast;

public class MainActivity extends AppCompatActivity {

    private MainGLSurfaceView mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainView = new MainGLSurfaceView(this);
        setContentView(mainView);
    }

    public void thisToast(String message) {
        jToast.showToast(message,getApplicationContext());
    }

    class MainGLSurfaceView extends GLSurfaceView {

        private final MainGLRenderer mRenderer;

        public MainGLSurfaceView(Context context){
            super(context);

            // Create an OpenGL ES 2.0 context
            setEGLContextClientVersion(2);

            mRenderer = new MainGLRenderer();

            // Set the Renderer for drawing on the GLSurfaceView
            setRenderer(mRenderer);
        }

        float lastXTouch = 0.0f;
        float lastYTouch = 0.0f;

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            // MotionEvent reports input details from the touch screen
            // and other input controls. In this case, you are only
            // interested in events where the touch position changed.

            float[] color = {0.0f, 0.0f, 0.0f};

            float x = e.getX();
            float y = e.getY();

            int width = this.getWidth();
            int height = this.getHeight();

            //thisToast("X = " + String.valueOf(x/width) + ", Y = " + String.valueOf(1 - y/height));

            switch (e.getAction()) {
                case MotionEvent.ACTION_UP:
                    lastXTouch = x;
                    lastYTouch = y;
                    mRenderer.setMotionMoment();
                    break;
                case MotionEvent.ACTION_DOWN:
                    lastXTouch = x;
                    lastYTouch = y;
                    mRenderer.setMotionMoment();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float deltaX = x - lastXTouch;
                    float deltaY = y - lastYTouch;

                    color[0] = x/width;
                    color[1] = 0.0f;
                    color[2] = 1 - y/height;

                    float absColor = (color[0] + color[1])/2;

                    mRenderer.setRed(color[0]);
                    mRenderer.setGreen(color[1]);
                    mRenderer.setBlue(color[2]);

                    mRenderer.changeMotionMoment(deltaY/height);

                    requestRender();
                    break;
            }
            return true;
        }
    }
}
