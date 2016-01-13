package com.karacasoft.cardboardui;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;

import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;
import com.karacasoft.cardboardui.view.View3D;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * Main component of the UI for Cardboard API.
 *
 * <p>You can think of this as the Canvas of your application.</p>
 *
 * <p>It has a very simple lifecycle inside. The View3D objects you add will be updated first.
 * (via {@link View3D#update()}). Then they will be drawn with their {@link View3D#draw(Eye)} method.</p>
 *
 * <p>You have to use @link ViewContent to add your views to your "canvas". Here's a simple
 *  view initialization inside a CardboardUIActivity:</p>
 * <code>
 *     TextView3D txt = new TextView3D(this, "Text, test");<br />
 *     ViewContent content = new ViewContent(this);<br />
 *     <br />
 *     content.addView(txt);<br />
 *     this.setCurrentContent(content);<br />
 * </code>
 *
 * <p>Check {@link com.karacasoft.cardboardui.test_activities.MainActivity}.</p>
 *
 * @see View3D
 * @see ViewContent
 *
 * @author Karaca
 */
public class CardboardUIActivity extends CardboardActivity implements CardboardView.StereoRenderer {

    public static final float Z_FAR = 100f;
    public static final float Z_NEAR = 1f;

    private float[] mCameraPosition = new float[4];

    private float[] mCameraMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mHeadViewMatrix = new float[16];

    private float[] mForwardVector = new float[4];

    private float[] mPointModelMatrix = new float[16];

    private int mMVPMatrixHandle;
    private int mMVMatrixHandle;
    private int mLightPosHandle;
    private int mTextureHandle;
    private int mProgramHandle;

    private int mLightProgramHandle;
    private int mLightMVPMatrixHandle;

    private float[] mLightPosInModelSpace = {0.0f, 0.0f, 0.0f, 1.0f};
    private float[] mLightModelMatrix = new float[16];
    private float[] mLightPosInWorldSpace = new float[4];
    private float[] mLightPosInEyeSpace = new float[4];

    private ViewContent currentContent;

    private int screenWidth;
    private int screenHeight;

    private boolean resetCameraFlag = false;

    private boolean lockCameraFlag = false;

    private boolean drawRedPoint = true;

    private boolean focusModeOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardboardui);
        CardboardView cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
        cardboardView.setRenderer(this);
        setCardboardView(cardboardView);
        setConvertTapIntoTrigger(true);
    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {
        headTransform.getHeadView(mHeadViewMatrix, 0);
        headTransform.getForwardVector(mForwardVector, 0);
        currentContent.update();
        Util.checkGLError("Error On New Frame");
    }

    @Override
    public void onDrawEye(Eye eye) {
        if(resetCameraFlag)
        {
            float[] invertedEye = new float[16];
            Matrix.invertM(invertedEye, 0, eye.getEyeView(), 0);
            setUpCamera();
            Matrix.multiplyMM(mCameraMatrix, 0, invertedEye, 0, mCameraMatrix, 0);
            resetCameraFlag = false;
        }
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if(lockCameraFlag) {
            mViewMatrix = mCameraMatrix.clone();
        }else{
            Matrix.multiplyMM(mViewMatrix, 0, eye.getEyeView(), 0, mCameraMatrix, 0);
        }



        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);

        mProjectionMatrix = eye.getPerspective(Z_NEAR, Z_FAR);

        GLES20.glUseProgram(mProgramHandle);
        currentContent.draw(eye);

        if(drawRedPoint) {
            drawTargetingPoint();
        }
        Util.checkGLError("Error Draw Eye");
    }

    private void drawTargetingPoint()
    {
        GLES20.glUseProgram(mLightProgramHandle);

        GLES20.glVertexAttrib3f(Util.ATTRIBUTE_POSITION, 0.0f, 0.0f, 0.0f);

        GLES20.glDisableVertexAttribArray(Util.ATTRIBUTE_POSITION);

        float[] MVPMatrix = new float[16];


        Matrix.setIdentityM(mPointModelMatrix, 0);
        Matrix.translateM(mPointModelMatrix, 0, 0.0f, 0.0f, 2.0f);

        Matrix.multiplyMM(MVPMatrix, 0, mCameraMatrix, 0, mPointModelMatrix, 0);
        Matrix.multiplyMM(MVPMatrix, 0, mProjectionMatrix, 0, MVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mLightMVPMatrixHandle, 1, false, MVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
    }

    @Override
    public void onFinishFrame(Viewport viewport) {

    }

    @Override
    public void onSurfaceChanged(int i, int i1) {

    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {
        Matrix.setIdentityM(mLightModelMatrix, 0);
        Matrix.translateM(mLightModelMatrix, 0, -2.0f, 1.0f, -2.0f);

        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        setUpCamera();

        mProgramHandle = Util.createProgram();
        mMVMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVMatrix");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        mLightPosHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_LightPos");
        mTextureHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Texture");

        mLightProgramHandle = Util.createLightProgram();
        mLightMVPMatrixHandle = GLES20.glGetUniformLocation(mLightProgramHandle, "u_MVPMatrix");

        screenWidth = getCardboardView().getWidth();
        screenHeight = getCardboardView().getHeight();
    }

    private void setUpCamera()
    {
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 7.0f;

        mCameraPosition[0] = eyeX;
        mCameraPosition[1] = eyeY;
        mCameraPosition[2] = eyeZ;

        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -1.0f;

        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        Matrix.setLookAtM(mCameraMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

    }

    @Override
    public void onRendererShutdown() {

    }

    @Override
    public void onCardboardTrigger() {
        if(lockCameraFlag)
        {
            lockCameraFlag = false;
            return;
        }
        for(View3D v : currentContent.getViews())
        {
            v.performTriggerIfLookingAt();
        }
    }

    public void resetCamera()
    {
        resetCameraFlag = true;
    }

    public void setCameraLock(boolean locked)
    {
        lockCameraFlag = locked;
    }

    public boolean isCameraLocked() {
        return lockCameraFlag;
    }

    public void setCurrentContent(ViewContent content)
    {
        this.currentContent = content;
        content.onContentSelected();
    }

    public float[] getCameraMatrix() {
        return mCameraMatrix;
    }

    public float[] getCameraPosition() {
        return mCameraPosition;
    }

    public float[] getViewMatrix() {
        return mViewMatrix;
    }

    public float[] getProjectionMatrix() {
        return mProjectionMatrix;
    }

    public float[] getHeadViewMatrix() {
        return mHeadViewMatrix;
    }

    public float[] getForwardVector() {
        return mForwardVector;
    }

    public int getMVPMatrixHandle() {
        return mMVPMatrixHandle;
    }

    public int getMVMatrixHandle() {
        return mMVMatrixHandle;
    }

    public int getLightPosHandle() {
        return mLightPosHandle;
    }

    public float[] getLightPosInEyeSpace() {
        return mLightPosInEyeSpace;
    }

    public int getTextureHandle() {
        return mTextureHandle;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public int getProgramHandle() {
        return mProgramHandle;
    }

    public void setDrawRedPoint(boolean drawRedPoint) {
        this.drawRedPoint = drawRedPoint;
    }

    public boolean isFocusModeOn() {
        return focusModeOn;
    }

    public void setFocusModeOn(boolean focusModeOn) {
        this.focusModeOn = focusModeOn;
    }
}
