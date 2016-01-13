package com.karacasoft.cardboardui.view;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.google.vrtoolkit.cardboard.Eye;
import com.karacasoft.cardboardui.CardboardUIActivity;
import com.karacasoft.cardboardui.Util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * <p>The most basic component of the UI API for Cardboard.</p>
 *
 * <p>View3D components have two methods that has importance on the lifecycle of the
 * activity. {@link View3D#update()} method is called on every frame of the activity.
 * It does the necessary changes on the view(create textures, create vertex buffers)
 * and change view flags according to the results. A {@link TextView3D} has a setText
 * method which changes the text inside the View3D. This is done by 4 steps:</p>
 * <div>
 *     <ul>
 *         <li>Change text on class field.</li>
 *         <li>Change the valid flag of the {@link View3D#invalidate()}</li>
 *         <li>Update method checks if the view is valid or not.</li>
 *         <li>It updates the texture for the text</li>
 *     </ul>
 * </div>
 * <p>The other important method is {@link View3D#draw(Eye)}.</p>
 *
 * <p>View3D has 2 callback methods to check if it is being interacted by the user.</p>
 *
 * <p>There's an experimental feature named "Focus Mode" which makes the user trigger
 * views without using the Cardboard trigger. Although it is experimental, it works pretty
 * well and it is suggested to implement this feature on your app.</p>
 *
 * @author Karaca
 */
public abstract class View3D {

    private OnLookAtListener lookAtCallback;
    private OnTriggerListener triggerCallback;

    protected boolean isLookingAt = false;
    protected boolean visible = true;

    private float[] mModelMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private float x = 0f;
    private float y = 0f;
    private float z = 0f;

    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    private float scaleZ = 1.0f;

    private float width;
    private float height;
    private float depth;

    private ViewData data;

    private int textureHandle = -1;

    private FloatBuffer verticesBuffer;
    private FloatBuffer colorBuffer;
    private FloatBuffer normalBuffer;
    private FloatBuffer textureBuffer;
    private ShortBuffer drawOrderBuffer;

    protected static final int BYTES_PER_FLOAT = 4;
    protected static final int BYTES_PER_SHORT = 2;

    private CardboardUIActivity mContext;

    protected boolean initialized = false;
    protected boolean valid = true;

    private boolean triggered = false;

    private boolean focusModeFocusable = true;
    private boolean clickable = true;

    private boolean focusModeTextureUsed = false;

    private long focusModeTimerStart = -1;
    private long focusModeTime;

    /**
     * Standard constructor for View3D.
     * @param context The CardboardUIActivity which this object will be displayed
     *                on.
     */
    public View3D(CardboardUIActivity context)
    {
        this.mContext = context;
        this.data = new ViewData();
        Matrix.setIdentityM(mModelMatrix, 0);
    }

    /**
     * Standard constructor with an extra ViewData parameter.
     * @param context The CardboardUIActivity itself.
     * @param data    Vertex, Texture, Color, and some other data
     *                to hold on view.
     */
    public View3D(CardboardUIActivity context, ViewData data)
    {
        this.mContext = context;
        this.data = data;
        Matrix.setIdentityM(mModelMatrix, 0);
    }

    /**
     * Converts view data into ByteBuffers which are understandable by OpenGL.
     * It sets the initialized flag of this view true.
     */
    public void initializeBuffers()
    {

        if(data.getVerticesData() != null)
        {
            verticesBuffer = ByteBuffer.allocateDirect(data.getVerticesData().length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            verticesBuffer.put(data.getVerticesData()).position(0);
        }
        if(data.getColorData() != null)
        {
            colorBuffer = ByteBuffer.allocateDirect(data.getColorData().length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            colorBuffer.put(data.getColorData()).position(0);
        }
        if(data.getNormalData() != null)
            {
            normalBuffer = ByteBuffer.allocateDirect(data.getNormalData().length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            normalBuffer.put(data.getNormalData()).position(0);
        }
        if(data.getTextureData() != null)
        {
            textureBuffer = ByteBuffer.allocateDirect(data.getTextureData().length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            textureBuffer.put(data.getTextureData()).position(0);
        }
        if(data.getVerticesDrawOrder() != null)
        {
            drawOrderBuffer = ByteBuffer.allocateDirect(data.getVerticesDrawOrder().length * BYTES_PER_SHORT)
                    .order(ByteOrder.nativeOrder()).asShortBuffer();
            drawOrderBuffer.put(data.getVerticesDrawOrder()).position(0);
        }
        this.initialized = true;
    }

    /**
     * Draws the View on GLES context. Called once for each eye.
     *
     * Draw method, sends the view data to OpenGL program every frame. Theoretically,
     * there will be significant performance issues if the user tries to draw too many
     * objects on the screen. It can be solved by using the graphics memory to hold
     * view data. But drawing different objects will still impact the performance.
     * So the best solution is simply avoiding drawing a lot of objects on screen.
     * I never tested for the limits though.
     *
     * The View should be initialized first. Or it will print a warning level log message
     * that says "View draw skipped. (View not ready)"
     *
     * @param eye is given by the CardboardActivity context.
     */
    public void draw(Eye eye)
    {
        if(initialized) {
            getVerticesBuffer().position(0);
            GLES20.glVertexAttribPointer(Util.ATTRIBUTE_POSITION, 3, GLES20.GL_FLOAT, false, 3 * 4, getVerticesBuffer());

            GLES20.glEnableVertexAttribArray(Util.ATTRIBUTE_POSITION);

            getColorBuffer().position(0);
            GLES20.glVertexAttribPointer(Util.ATTRIBUTE_COLOR, 4, GLES20.GL_FLOAT, false, 4 * 4, getColorBuffer());

            GLES20.glEnableVertexAttribArray(Util.ATTRIBUTE_COLOR);

            getNormalBuffer().position(0);
            GLES20.glVertexAttribPointer(Util.ATTRIBUTE_NORMAL, 3, GLES20.GL_FLOAT, false, 3 * 4, getNormalBuffer());

            GLES20.glEnableVertexAttribArray(Util.ATTRIBUTE_NORMAL);

            getTextureBuffer().position(0);
            GLES20.glVertexAttribPointer(Util.ATTRIBUTE_TEXTURE, 2, GLES20.GL_FLOAT, false, 2 * 4, getTextureBuffer());

            GLES20.glEnableVertexAttribArray(Util.ATTRIBUTE_TEXTURE);


            Matrix.multiplyMM(getMVPMatrix(), 0, getContext().getViewMatrix(), 0, getModelMatrix(), 0);
            //MVP matrix is actually MV matrix at this point.
            GLES20.glUniformMatrix4fv(getContext().getMVMatrixHandle(), 1, false, getMVPMatrix(), 0);

            Matrix.multiplyMM(getMVPMatrix(), 0, getContext().getProjectionMatrix(), 0, getMVPMatrix(), 0);

            GLES20.glUniformMatrix4fv(getContext().getMVPMatrixHandle(), 1, false, getMVPMatrix(), 0);

            GLES20.glUniform3f(getContext().getLightPosHandle(), getContext().getLightPosInEyeSpace()[0],
                    getContext().getLightPosInEyeSpace()[1], getContext().getLightPosInEyeSpace()[2]);

            if(getTextureHandle() != -1)
            {
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, getTextureHandle());

                GLES20.glUniform1i(getContext().getTextureHandle(), 0);
            }else{
                GLES20.glActiveTexture(GLES20.GL_TEXTURE1);

                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, Util.getEmptyTexture());

                GLES20.glUniform1i(getContext().getTextureHandle(), 1);
            }

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, getViewData().getVerticesDrawOrder().length, GLES20.GL_UNSIGNED_SHORT, getDrawOrderBuffer());
        }else{
            Log.w("View3D", "View draw skipped. (View not ready)");
        }
    }

    /**
     * Checks if the User is looking at an object. Doesn't work very well when the user looks
     * tilted on left or right. It is checked on every frame to make trigger callbacks work.
     *
     * Warning: do not call this method to check if the user is looking at the view or
     * not. There's a field {@link View3D#isLookingAt} on View3D marks the result of this
     * method every frame. You should check that field to decide what you want to do.
     * It is even better to use the {@link com.karacasoft.cardboardui.view.View3D.OnLookAtListener}.
     *
     * @return true if the User is looking at this object.
     */
    public boolean isLookingAt()
    {
        if(!this.visible)
            return false;
        float[] camPosition = getContext().getCameraPosition();
        float[] forward = getContext().getForwardVector();

        float multiplier = (camPosition[2] - getZ()) / forward[2];

        float lookX = forward[0] * multiplier;
        float lookY = forward[1] * multiplier;

        if (lookX < (getWidth() + getX()) * scaleX && lookX > getX() * scaleX &&
                lookY < (getHeight() + getY()) * scaleY && lookY > getY() * scaleY) {
            isLookingAt = true;
            if(this.lookAtCallback != null) lookAtCallback.onLookAt(this, lookX, lookY);
            return true;
        }

        return false;
    }

    /**
     * Updates the View.
     *
     * Checks if the user is looking at the view. Handles Focus Mode.
     * Performs the trigger if the Cardboard trigger was used. It should
     * be extended on every View3D subclass to handle changes on the
     * particular View.
     */
    public void update()
    {
        if(isLookingAt)
        {
            if(!isLookingAt()) {
                if (this.lookAtCallback != null) lookAtCallback.onHoverExit(this);
                isLookingAt = false;
                focusModeTimerStart = -1;
            } else {
                if (mContext.isFocusModeOn()) {
                    if (focusModeTimerStart == -1) {
                        focusModeTimerStart = System.currentTimeMillis();
                    }
                    focusModeTime = System.currentTimeMillis();
                    if (focusModeTime - focusModeTimerStart > 1500) {
                        triggered = true;
                        focusModeTimerStart += 100000;
                    }
                }
            }
        }else{
            isLookingAt();
        }
        if(triggered)
        {
            performTrigger();
            triggered = false;
        }
    }

    /**
     * Utility method for the Focus Mode to work.
     */
    public void performTriggerIfLookingAt()
    {
        if(isLookingAt()) {
            triggered = true;
        }
    }

    /**
     * Fires the triggerCallback if it is not null.
     */
    protected void performTrigger()
    {
        if(triggerCallback != null) {
            triggerCallback.onTrigger(this);
        }
    }

    /**
     * A view's width, height and depth, depends on the View itself. So
     * every specialized view subclass should implement its own measure method to calculate
     * its width, height and depth.
     *
     * It is possible to leave these fields empty. But for convenience,
     * subclasses should implement a measure method for each View.
     */
    public abstract void measure();

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getScaleX() {
        return scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public float getScaleZ() {
        return scaleZ;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getDepth() {
        return depth;
    }

    public void setWidth(float width) {
        this.width = width;
        valid = false;
    }

    public void setHeight(float height) {
        this.height = height;
        valid = false;
    }

    public void setDepth(float depth) {
        this.depth = depth;
        valid = false;
    }

    /**
     * Translate a View by given values. The values will be added to the class fields.
     * Then the model matrix will be changed.
     *
     * The Y axis is inverted, beware of that!
     *
     * Most views use 1.0 height to make calculations easier. If you translate a view
     * in Y axis by 1.0, it will move up by its height.
     *
     * It is important to order the translate and scale right. Using translate after scale
     * will move the view according to the previously given scale.
     *
     * @param dx X translation.
     * @param dy Y translation.
     * @param dz Z translation.
     */
    public void translate(float dx, float dy, float dz)
    {
        this.x += dx;
        this.y += dy;
        this.z += dz;
        Matrix.translateM(this.getModelMatrix(), 0, dx, dy, dz);
    }


    /**
     * Scales the view by given values. The class fields will be multiplied
     * by the given values. Then the model matrix will be changed.
     *
     * Using view.scale(0,2,0); Will make the View have twice as big height.
     *
     * It is important to order the translate and scale right. Using translate
     * after scale will move the view according to the previously given scale.
     *
     * @param x X scale
     * @param y Y scale
     * @param z Z scale
     */
    public void scale(float x, float y, float z)
    {
        this.scaleX *= x;
        this.scaleY *= y;
        this.scaleZ *= z;
        Matrix.scaleM(this.getModelMatrix(), 0, x, y, z);
    }

    public ViewData getViewData() {
        return data;
    }

    public void setViewData(ViewData data) {
        this.data = data;
    }

    public FloatBuffer getVerticesBuffer() {
        return verticesBuffer;
    }

    public FloatBuffer getColorBuffer() {
        return colorBuffer;
    }

    public FloatBuffer getNormalBuffer() {
        return normalBuffer;
    }

    public FloatBuffer getTextureBuffer() {
        return textureBuffer;
    }

    public ShortBuffer getDrawOrderBuffer() {
        return drawOrderBuffer;
    }


    public CardboardUIActivity getContext() {
        return mContext;
    }

    public float[] getModelMatrix() {
        return mModelMatrix;
    }

    public float[] getMVPMatrix() {
        return mMVPMatrix;
    }

    public int getTextureHandle() {
        return textureHandle;
    }

    public void setTextureHandle(int textureHandle) {
        this.textureHandle = textureHandle;
    }


    public boolean isVisible() {
        return visible;
    }

    /**
     * Makes the view invisible. Invisible views can call trigger events.
     *
     * You can remove the view from ViewContent to make the View invisible and
     * don't call trigger events.
     *
     * @param visible Visibility
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void invalidate()
    {
        valid = false;
    }

    public void setOnLookAtListener(OnLookAtListener lookAtCallback) {
        this.lookAtCallback = lookAtCallback;
    }

    public void setOnTriggerListener(OnTriggerListener triggerCallback) {
        this.triggerCallback = triggerCallback;
    }

    public long getFocusAnimTime()
    {
        return Math.max(Math.min(focusModeTime - focusModeTimerStart, 1500), 0);
    }

    public boolean isClickable() {
        return clickable;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    public boolean isFocusModeFocusable() {
        return focusModeFocusable;
    }

    public void setFocusModeFocusable(boolean focusModeFocusable) {
        this.focusModeFocusable = focusModeFocusable;
    }

    public boolean isFocusModeTextureUsed() {
        return focusModeTextureUsed;
    }

    public void setFocusModeTextureUsed(boolean focusModeTextureUsed) {
        this.focusModeTextureUsed = focusModeTextureUsed;
    }

    /**
     * A hover listener for View.
     */
    public interface OnLookAtListener
    {
        /**
         * Fires if the user is looking at the object. It fires on each frame,
         * so be careful with your implementation.
         * @param v The View3D object
         * @param x X coordinate of the SCREEN.(Not the object)
         * @param y Y coordinate of the SCREEN.(Not the object)
         */
        void onLookAt(View3D v, float x, float y);

        /**
         * Fires once when the user stops looking at an object.
         * @param v The View3D object.
         */
        void onHoverExit(View3D v);
    }

    /**
     * Trigger callback
     */
    public interface OnTriggerListener
    {
        /**
         * Fires if the user uses the Cardboard trigger while looking at the view.
         * @param v The View3D object.
         */
        void onTrigger(View3D v);
    }

}
