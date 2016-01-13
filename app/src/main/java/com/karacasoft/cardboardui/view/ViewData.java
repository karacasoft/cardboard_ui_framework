package com.karacasoft.cardboardui.view;

/**
 * Created by Karaca on 5/28/2015.
 */
public class ViewData
{
    public ViewData(){}

    private float[] verticesData;
    private short[] verticesDrawOrder;
    private float[] normalData;
    private float[] colorData;
    private float[] textureData;

    public float[] getVerticesData() {
        return verticesData;
    }

    public short[] getVerticesDrawOrder() {
        return verticesDrawOrder;
    }

    public float[] getNormalData() {
        return normalData;
    }

    public float[] getColorData() {
        return colorData;
    }

    public float[] getTextureData() {
        return textureData;
    }

    public void setVerticesData(float[] verticesData) {
        this.verticesData = verticesData;
    }
    public void setVerticesDrawOrder(short[] verticesDrawOrder) {
        this.verticesDrawOrder = verticesDrawOrder;
    }
    public void setColorData(float[] colorData) {
        this.colorData = colorData;
    }

    public void setNormalData(float[] normalData) {
        this.normalData = normalData;
    }

    public void setTextureData(float[] textureData) {
        this.textureData = textureData;
    }

    public ViewData copy()
    {
        ViewData data = new ViewData();
        data.setVerticesData(this.getVerticesData());
        data.setTextureData(this.getTextureData());
        data.setColorData(this.getColorData());
        data.setNormalData(this.getNormalData());
        data.setVerticesDrawOrder(this.getVerticesDrawOrder());
        return data;
    }

}