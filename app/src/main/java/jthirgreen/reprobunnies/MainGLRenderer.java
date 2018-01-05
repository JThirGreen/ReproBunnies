package jthirgreen.reprobunnies;

import android.opengl.EGLConfig;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import jthirgreen.reprobunnies.Objects.Shapes.Square;
import jthirgreen.reprobunnies.Objects.Shapes.Triangle;
import jthirgreen.reprobunnies.Utilities.jToast;


/**
 * Created by JThirGreen on 9/8/2017.
 */

public class MainGLRenderer implements GLSurfaceView.Renderer {

    private float triangleSides = 0.5f;
    private Triangle mTriangle1;
    private Triangle mTriangle2;
    private Triangle mTriangle3;
    private Triangle mTriangle4;
    private Square mSquare1;
    private Square mSquare2;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    private float lastTimeMoment = 0.0f;
    private float lastTouchMoment = 0.0f;
    private float motionMoment = 0.0f;
    private float time = SystemClock.uptimeMillis()/1000.0f;
    private boolean touchMode = false;
    private float maxMotionMoment = 8.0f;
    private float motionMomentDirection = 1.0f;

    void changeMotionMoment(float t) {
        if (!touchMode) {
            lastTimeMoment = motionMoment;
            touchMode = true;
            motionMomentDirection *= -1.0f;
        }
        time = SystemClock.uptimeMillis()/1000.0f;
        motionMoment = (lastTimeMoment + t) % maxMotionMoment;
    }

    void setMotionMoment() {
        lastTimeMoment = motionMoment;
    }

    float getMotionMoment() {
        return ((motionMoment - 4.0f) % 8.0f);
    }

    public void onSurfaceCreated(GL10 unused, javax.microedition.khronos.egl.EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        mTriangle1 = new Triangle(0.0f, 0.0f, 0.0f, triangleSides);
        mTriangle2 = new Triangle(0.0f, 0.0f, 0.0f, triangleSides);
        mTriangle3 = new Triangle(0.0f, 0.0f, 0.0f, triangleSides);
        mTriangle4 = new Triangle(0.0f, 0.0f, 0.0f, 0.0f - triangleSides);
        mSquare1 = new Square(0.0f, 0.0f, 0.0f, 0.7f);
        mSquare2 = new Square(0.0f, 0.0f, 0.0f, 0.7f);
        mTriangle1.setColor(1.0f, 1.0f, 0.0f);
        mTriangle2.setColor(1.0f, 1.0f, 0.0f);
        mTriangle3.setColor(1.0f, 1.0f, 0.0f);
        mTriangle4.setColor(0.0f, 0.0f, 0.0f);
        mSquare1.setColor(0.0f, 1.0f, 0.0f);
        mSquare2.setColor(0.0f, 1.0f, 0.0f);
    }

    public void onDrawFrame(GL10 unused) {
        float startTime = SystemClock.uptimeMillis()/1000.0f;

        // Redraw background color
        GLES20.glClearColor(getRed(), getGreen(), getBlue(), 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // Create a rotation transformation for the triangle
        if (touchMode && startTime > (time + 1.0f)) {
            touchMode = false;
            lastTouchMoment = motionMoment;
        }
        if (!touchMode) {
            motionMoment = (lastTouchMoment + motionMomentDirection*(startTime - (time + 1.0f))) % maxMotionMoment;
        }
        float angle = 90.0f * (getMotionMoment());

        if (motionMoment < 0.0f)
            motionMoment = maxMotionMoment + motionMoment;

        //mTriangle.get(0).translate(0.0f, -0.1f, 0.0f);
        mTriangle1.translate(0.25f, -1.0f * triangleSides * mTriangle1.toSideRatio, 0.0f);
        mTriangle1.rotate(1.0f * angle);
        mTriangle2.translate(-0.25f, -1.0f * triangleSides * mTriangle2.toSideRatio, 0.0f);
        mTriangle2.rotate(-1.0f * angle);
        mTriangle3.translate(0.0f, (triangleSides * mTriangle3.toCornerRatio) + (3.0f * triangleSides * mTriangle3.toSideRatio * getMotionMoment()), 0.0f);
        mTriangle3.rotate(1.0f * angle);
        mSquare1.translate(0.0f, 0.0f, 0.0f);
        mSquare1.rotate(angle);
        mSquare2.translate(0.0f, 0.0f, 0.0f);
        mSquare2.rotate(-1.0f * angle);
        //Matrix.setRotateM(mRotationMatrix, 0, angle, 0, 0, -1.0f);
        //Matrix.setRotateM(mRotationMatrixReverse, 0, -1.0f * angle, 0, 0, -1.0f);

        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        //Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);

        // Draw triangle
        mSquare1.draw(mMVPMatrix);
        mSquare2.draw(mMVPMatrix);
        mTriangle4.draw(mMVPMatrix);
        mTriangle1.draw(mMVPMatrix);
        mTriangle2.draw(mMVPMatrix);
        mTriangle3.draw(mMVPMatrix);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    volatile float Red = 0.0f;
    volatile float Green = 0.0f;
    volatile float Blue = 0.0f;

    public float getRed() {
        return Red;
    }

    public void setRed(float red) {
        Red = (red > 0) ? red : 0.0f;
    }

    public float getGreen() {
        return Green;
    }

    public void setGreen(float green) {
        Green = (green > 0) ? green : 0.0f;
    }

    public float getBlue() {
        return Blue;
    }

    public void setBlue(float blue) {
        Blue = (blue > 0) ? blue : 0.0f;
    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

}
