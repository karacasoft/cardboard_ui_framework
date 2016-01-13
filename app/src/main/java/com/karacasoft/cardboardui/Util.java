package com.karacasoft.cardboardui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLUtils;

/**
 * Created by Karaca on 5/23/2015.
 */
public class Util {

    public static final int ATTRIBUTE_POSITION = 0;
    public static final int ATTRIBUTE_COLOR = 1;
    public static final int ATTRIBUTE_NORMAL = 2;
    public static final int ATTRIBUTE_TEXTURE = 3;

    private static int emptyTexture = -1;

    public static final String vertex_shader = "uniform mat4 u_MVPMatrix;\n" +
            "uniform mat4 u_MVMatrix;\n" +
            "\n" +
            "attribute vec4 a_Position;\n" +
            "attribute vec3 a_Normal;\n" +
            "attribute vec4 a_Color;\n" +
            "attribute vec2 a_TexCoord;\n" +
            "\n" +
            "varying vec3 v_Position;\n" +
            "varying vec4 v_Color;\n" +
            "varying vec3 v_Normal;\n" +
            "varying vec2 v_TexCoord;\n" +
            "\n" +
            "void main(){\n" +
            "\n" +
            "   v_Position = vec3(u_MVMatrix * a_Position);\n" +
            "   v_Color = a_Color;\n" +
            "   v_TexCoord = a_TexCoord;\n" +
            "   \n" +
            "   v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));\n" +
            "   \n" +
            "   gl_Position = u_MVPMatrix * a_Position;\n" +
            "}\n";

    public static final String fragment_shader = "precision mediump float;\n" +
            "\n" +
            "uniform vec3 u_LightPos;\n" +
            "uniform sampler2D u_Texture;\n" +
            "\n" +
            "varying vec4 v_Color;\n" +
            "varying vec3 v_Position;\n" +
            "varying vec3 v_Normal;\n" +
            "varying vec2 v_TexCoord;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "   float distance = length(u_LightPos - v_Position);\n" +
            "   \n" +
            "   vec3 lightVector = normalize(u_LightPos - v_Position);\n" +
            "   \n" +
            "   float diffuse = max(dot(v_Normal, lightVector), 0.1);\n" +
            "   \n" +
            "   diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance)));\n" +
            "   diffuse = diffuse + 0.3;\n" +
            "   \n" +
            "   gl_FragColor = v_Color * diffuse + texture2D(u_Texture, v_TexCoord);\n" +
            "}\n";


    public static final String point_vertex_shader =
            "uniform mat4 u_MVPMatrix;\n" +
            "attribute vec4 a_Position;\n" +
            "\n" +
            "void main(){\n" +
            "   gl_Position = u_MVPMatrix * a_Position;\n" +
            "   gl_PointSize = 7.0;\n" +
            "}\n";

    public static final String point_fragment_shader =
            "precision mediump float;\n" +
            "void main(){\n" +
            "   gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);\n" +
            "}";

    public static int createShader(String shaderCode, int shaderType)
    {
        int shaderHandle = GLES20.glCreateShader(shaderType);

        if(shaderHandle != 0)
        {
            GLES20.glShaderSource(shaderHandle, shaderCode);

            GLES20.glCompileShader(shaderHandle);

            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            if(compileStatus[0] == 0)
            {
                GLES20.glDeleteShader(shaderHandle);
                throw new RuntimeException("Error creating shader type:" + shaderType);
            }
        }else{
            throw new RuntimeException("Error creating shader type:" + shaderType);
        }

        return shaderHandle;
    }

    public static int createProgram()
    {
        int programHandle = GLES20.glCreateProgram();

        if(programHandle != 0)
        {
            GLES20.glAttachShader(programHandle, createShader(vertex_shader, GLES20.GL_VERTEX_SHADER));

            GLES20.glAttachShader(programHandle, createShader(fragment_shader, GLES20.GL_FRAGMENT_SHADER));

            GLES20.glBindAttribLocation(programHandle, ATTRIBUTE_POSITION, "a_Position");
            GLES20.glBindAttribLocation(programHandle, ATTRIBUTE_COLOR, "a_Color");
            GLES20.glBindAttribLocation(programHandle, ATTRIBUTE_NORMAL, "a_Normal");
            GLES20.glBindAttribLocation(programHandle, ATTRIBUTE_TEXTURE, "a_TexCoord");

            GLES20.glLinkProgram(programHandle);

            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            if(linkStatus[0] == 0)
            {
                GLES20.glDeleteProgram(programHandle);
                throw new RuntimeException("Error creating program");
            }
        }else{
            throw new RuntimeException("Error creating program");
        }
        return programHandle;
    }

    public static int createLightProgram()
    {
        int programHandle = GLES20.glCreateProgram();
        if(programHandle != 0)
        {

            GLES20.glAttachShader(programHandle, createShader(point_vertex_shader, GLES20.GL_VERTEX_SHADER));

            GLES20.glAttachShader(programHandle, createShader(point_fragment_shader, GLES20.GL_FRAGMENT_SHADER));

            GLES20.glBindAttribLocation(programHandle, ATTRIBUTE_POSITION, "a_Position");

            GLES20.glLinkProgram(programHandle);

            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            if(linkStatus[0] == 0)
            {
                GLES20.glDeleteProgram(programHandle);
                throw new RuntimeException("Error creating program");
            }

        }else{
            throw new RuntimeException("Error creating program");
        }

        return programHandle;
    }

    public static int loadTexture(final Context context, final int resourceId)
    {
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), resourceId);

        return loadTexture(bmp);
    }

    public static void loadTexture(final int textureHandle, final Bitmap bmp)
    {
        if(textureHandle != 0)
        {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
        }else{
            throw new RuntimeException("Error loading texture");
        }
    }

    public static int loadTexture(final Bitmap bmp)
    {
        int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if(textureHandle[0] != 0)
        {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
        }else{
            throw new RuntimeException("Error loading texture");
        }
        return textureHandle[0];
    }

    public static int getEmptyTexture() {
        if (emptyTexture != -1) {
            return emptyTexture;
        } else {
            Bitmap bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_4444);
            Canvas c = new Canvas(bmp);
            c.drawColor(Color.BLACK);

            return emptyTexture = loadTexture(bmp);
        }
    }

    public static void checkGLError(String message)
    {
        if(GLES20.glGetError() != GLES20.GL_NO_ERROR)
        {
            throw new RuntimeException("GL Error: " + message + " : " + GLES20.glGetError());
        }
    }


}
