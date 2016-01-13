package com.karacasoft.cardboardui.objimport;

import android.content.Context;

import com.karacasoft.cardboardui.CardboardUIActivity;
import com.karacasoft.cardboardui.view.View3D;
import com.karacasoft.cardboardui.view.ViewData;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class currently takes only the vertices from the .obj file.
 *
 * EXPERIMENTAL, USE AT YOUR OWN RISK!
 *
 * Created by Karaca on 5/24/2015.
 */
//TODO implement materials normals and colors
public class ObjFile {

    private class Vertex
    {
        float x;
        float y;
        float z;
        float w;
    }

    private class Texture
    {
        float x;
        float y;
    }

    private class Normal
    {
        float x;
        float y;
        float z;
    }

    private class Face
    {
        int[] v = new int[4];
        int[] vt = new int[4];
        int[] vn = new int[4];
    }

    private ArrayList<Vertex> vertices = new ArrayList<>();
    private ArrayList<Normal> normals = new ArrayList<>();
    private ArrayList<Texture> textures = new ArrayList<>();
    private ArrayList<Face> faces = new ArrayList<>();


    private Context mContext;
    private String mFileName;
    private int resourceId;

    private boolean useResource = false;

    public ObjFile(Context context, int resourceId)
    {
        this.mContext = context;
        this.resourceId = resourceId;
        useResource = true;
        read();
    }

    public ObjFile(Context context, String name)
    {
        this.mContext = context;
        this.mFileName = name;
        read();
    }

    public void writeBuffersToView(View3D v)
    {
        ArrayList<Float> verticesData = new ArrayList<>();
        ArrayList<Short> drawOrder = new ArrayList<>();


        for(Vertex ve : vertices)
        {
            verticesData.add(ve.x);
            verticesData.add(ve.y);
            verticesData.add(ve.z);
        }

        float[] texturesDataArray = new float[faces.size() * 2];
        float[] normalsDataArray = new float[faces.size() * 3];
        int indexTex = 0;
        int indexNormal = 0;
        for(Face f : faces)
        {
            drawOrder.add((short)(f.v[0] - 1));
            drawOrder.add((short)(f.v[1] - 1));
            drawOrder.add((short)(f.v[2] - 1));

            if(!textures.isEmpty()) {
                if (indexTex < texturesDataArray.length) {
                    texturesDataArray[indexTex] = textures.get(f.vt[0] - 1).x;
                    texturesDataArray[indexTex + 1] = textures.get(f.vt[0] - 1).y;
                    indexTex += 2;
                }
            }

            if(!normals.isEmpty()) {
                if (indexNormal < normalsDataArray.length) {
                    normalsDataArray[indexNormal] = normals.get(f.vn[0] - 1).x;
                    normalsDataArray[indexNormal + 1] = normals.get(f.vn[0] - 1).y;
                    normalsDataArray[indexNormal + 2] = normals.get(f.vn[0] - 1).z;
                    indexNormal += 3;
                }
            }

        }

        float[] verticesDataArray = new float[verticesData.size()];
        for(int i = 0; i < verticesData.size(); i++)
        {
            verticesDataArray[i] = verticesData.get(i);
        }

        short[] drawOrderArray = new short[drawOrder.size()];
        for(int i = 0; i < drawOrder.size(); i++)
        {
            drawOrderArray[i] = drawOrder.get(i);
        }

        ViewData data = new ViewData();

        data.setVerticesData(verticesDataArray);
        data.setNormalData(normalsDataArray);
        data.setVerticesDrawOrder(drawOrderArray);
        data.setTextureData(texturesDataArray);

        v.setViewData(data);

        v.initializeBuffers();

    }

    public ImportedObjView3D constructView()
    {
        ImportedObjView3D v = new ImportedObjView3D((CardboardUIActivity) mContext);
        writeBuffersToView(v);
        return v;
    }

    private void read()
    {
        InputStream inStream;
        if(useResource) {
             inStream = mContext.getResources().openRawResource(this.resourceId);
        }else{
            throw new UnsupportedOperationException("Not yet implemented.");
        }

        Scanner s = new Scanner(inStream);
        String newLine;

        while(s.hasNextLine())
        {
            newLine = s.nextLine();
            if(newLine.startsWith("v "))
            {
                Vertex v = new Vertex();
                String[] coords = newLine.split(" ");
                v.x = Float.valueOf(coords[1]);
                v.y = Float.valueOf(coords[2]);
                v.z = Float.valueOf(coords[3]);
                vertices.add(v);
            }
            if(newLine.startsWith("vt "))
            {
                Texture t = new Texture();
                String[] coords = newLine.split(" ");
                t.x = Float.valueOf(coords[1]);
                t.y = Float.valueOf(coords[2]);
                textures.add(t);
            }
            if(newLine.startsWith("vn "))
            {
                Normal n = new Normal();
                String[] coords = newLine.split(" ");
                n.x = Float.valueOf(coords[1]);
                n.y = Float.valueOf(coords[2]);
                n.z = Float.valueOf(coords[3]);
                normals.add(n);
            }
            if(newLine.startsWith("f "))
            {
                Face f = new Face();
                String[] vertices = newLine.split(" ");
                for(int i = 1; i < vertices.length; i++)
                {
                    String[] indices = vertices[i].split("/");
                    if(!indices[0].equals("")) {
                        f.v[i - 1] = Integer.valueOf(indices[0]);
                    }
                    if(!indices[1].equals(""))
                    {
                        f.vt[i - 1] = Integer.valueOf(indices[1]);
                    }
                    if(!indices[2].equals(""))
                    {
                        f.vn[i - 1] = Integer.valueOf(indices[2]);
                    }
                }
                faces.add(f);
            }
        }
        try {
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
