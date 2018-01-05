package jthirgreen.reprobunnies.Objects.Shapes;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import jthirgreen.reprobunnies.MainGLRenderer;

/**
 * Created by JThirGreen on 9/11/2017.
 */

public class Square {

    private final int mProgram;

    public Square(float xPoint, float yPoint, float zPoint, float sideSize) {
        squareCoords[0] = xPoint - (sideSize * 0.5f);
        squareCoords[1] = yPoint + (sideSize * 0.5f);
        squareCoords[2] = zPoint;
        squareCoords[3] = xPoint - (sideSize * 0.5f);
        squareCoords[4] = yPoint - (sideSize * 0.5f);
        squareCoords[5] = zPoint;
        squareCoords[6] = xPoint + (sideSize * 0.5f);
        squareCoords[7] = yPoint - (sideSize * 0.5f);
        squareCoords[8] = zPoint;
        squareCoords[9] = xPoint + (sideSize * 0.5f);
        squareCoords[10] = yPoint + (sideSize * 0.5f);
        squareCoords[11] = zPoint;

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

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
    private ShortBuffer drawListBuffer;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    float squareCoords[] = {
            0.0f, 0.0f, 0.0f,   // top left
            0.0f, 0.0f, 0.0f,   // bottom left
            0.0f, 0.0f, 0.0f,   // bottom right
            0.0f, 0.0f, 0.0f }; // top right
    float[] quadTranslate = {0.0f, 0.0f, 0.0f};
    float[] quadRotate = new float[16];

    private short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

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
        quadTranslate[0] = x;
        quadTranslate[1] = y;
        quadTranslate[2] = z;
    }

    public void rotate(float angle) {
        Matrix.setRotateM(quadRotate, 0, angle, 0, 0, -1.0f);
    }

    private int mPositionHandle;
    private int mColorHandle;

    private final int vertexCount = squareCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public void draw(float[] mvpMatrix) {
        float[] quadTransform = new float[16];
        Matrix.translateM(quadTransform, 0, mvpMatrix, 0, quadTranslate[0], quadTranslate[1], quadTranslate[2]);
        Matrix.multiplyMM(quadTransform, 0, quadTransform, 0, quadRotate, 0);
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
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, quadTransform, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
