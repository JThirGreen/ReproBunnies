package jthirgreen.reprobunnies.Objects.Shapes;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import jthirgreen.reprobunnies.MainGLRenderer;

/**
 * Created by JThirGreen on 9/11/2017.
 */

public class Triangle {

    private final int mProgram;

    // Ratios of sideSize to the distance between the center and corner/side (equilateral triangles only)
    public static float toCornerRatio = (float) (Math.sin(Math.PI/6.0f)/Math.sin(Math.PI * 2.0f/3.0f));
    public static float toSideRatio = (float) (Math.sin(Math.PI/6.0f)/(2.0f * Math.sin(Math.PI/3.0f)));

    public Triangle(float xPoint, float yPoint, float zPoint, float sideSize) {

        float toCorner = sideSize * toCornerRatio;
        float toSide = sideSize * toSideRatio;
        triangleCoords[0] = xPoint;
        triangleCoords[1] = yPoint + toCorner;
        triangleCoords[2] = zPoint;
        triangleCoords[3] = xPoint - (sideSize/2.0f);
        triangleCoords[4] = yPoint - toSide;
        triangleCoords[5] = zPoint;
        triangleCoords[6] = xPoint + (sideSize/2.0f);
        triangleCoords[7] = yPoint - toSide;
        triangleCoords[8] = zPoint;

        // Set the camera position (View matrix)
        //Matrix.setLookAtM(triTranslate, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.setIdentityM(triRotate, 0);

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(triangleCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        int vertexShader = MainGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MainGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);
    }

    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    // Use to access and set the view transformation
    private int mMVPMatrixHandle;

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private FloatBuffer vertexBuffer;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    float triangleCoords[] = {   // in counterclockwise order:
            0.0f, 0.0f, 0.0f, // top
            0.0f, 0.0f, 0.0f, // bottom left
            0.0f, 0.0f, 0.0f  // bottom right
    };
    float[] triTranslate = {0.0f, 0.0f, 0.0f};
    float[] triRotate = new float[16];

    // Set color with red, green, blue and alpha (opacity) values
    public volatile float Red = 1.0f;
    public volatile float Green = 1.0f;
    public volatile float Blue = 1.0f;

    public float getRed() { return color[0]; }

    public void setRed(float red) { color[0] = (red > 0) ? red : 0.0f; }

    public float getGreen() {
        return color[1];
    }

    public void setGreen(float green) {
        color[1] = (green > 0) ? green : 0.0f;
    }

    public float getBlue() { return color[2]; }

    public void setBlue(float blue) {
        color[2] = (blue > 0) ? blue : 0.0f;
    }

    public void setColor(float red, float green, float blue) {
        setRed(red);
        setBlue(blue);
        setGreen(green);
    }

    float color[] = { 1.0f, 1.0f, 1.0f, 1.0f };

    public void translate(float x, float y, float z) {
        triTranslate[0] = x;
        triTranslate[1] = y;
        triTranslate[2] = z;
    }

    public void rotate(float angle) {
        Matrix.setRotateM(triRotate, 0, angle, 0, 0, -1.0f);
    }

    private int mPositionHandle;
    private int mColorHandle;

    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public void draw(float[] mvpMatrix) {
        float[] triTransform = new float[16];
        Matrix.translateM(triTransform, 0, mvpMatrix, 0, triTranslate[0], triTranslate[1], triTranslate[2]);
        Matrix.multiplyMM(triTransform, 0, triTransform, 0, triRotate, 0);

        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, triTransform, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
