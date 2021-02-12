import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.*;

public class DisplayPanel extends JPanel implements MouseMotionListener, MouseListener {
    ArrayList<MyMesh> meshes = new ArrayList<>();
    float[][] projMat = new float[4][4];
    MyVector3D CameraVector = new MyVector3D(0.0f, 0.0f, 0.0f);

    public DisplayPanel(){
        setBackground(Color.gray);
        makeProjectionMatrix();
        meshInitializer();
    }

    public void paint(Graphics g){
        super.paint(g);

        //Rotation Matrices
        float[][] rotX, rotZ;
        rotX = new float[4][4];
        rotZ = new float[4][4];
        double fTheta = 1.0f * UniversalTimer.timeElapsed;

        // Z Rotation Matrix
        rotZ[0][0] = (float)Math.cos(fTheta);
        rotZ[0][1] = (float)Math.sin(fTheta);
        rotZ[1][0] = (float)-Math.sin(fTheta);
        rotZ[1][1] = (float)Math.cos(fTheta);
        rotZ[2][2] = 1;
        rotZ[3][3] = 1;

        // X Rotation Matrix
        rotX[0][0] = 1;
        rotX[1][1] = (float)Math.cos(fTheta * 0.5f);
        rotX[1][2] = (float)Math.sin(fTheta * 0.5f);
        rotX[2][1] = (float)-Math.sin(fTheta * 0.5f);
        rotX[2][2] = (float)Math.cos(fTheta * 0.5f);
        rotX[3][3] = 1;


        for(MyMesh mesh: meshes){
            for(MyTriangle triangle: mesh.triangles){
                MyTriangle translatedTriangle = new MyTriangle();
                MyTriangle projectedTriangle = new MyTriangle();
                MyTriangle ZRotatedTriangle = new MyTriangle();
                MyTriangle ZXRotatedTriangle = new MyTriangle();

                // translate the triangle
                ZRotatedTriangle.vectors[0] = MyHelper.multiplyMatrixVector(triangle.vectors[0], rotZ);
                ZRotatedTriangle.vectors[1] = MyHelper.multiplyMatrixVector(triangle.vectors[1], rotZ);
                ZRotatedTriangle.vectors[2] = MyHelper.multiplyMatrixVector(triangle.vectors[2], rotZ);

                ZXRotatedTriangle.vectors[0] = MyHelper.multiplyMatrixVector(ZRotatedTriangle.vectors[0], rotX);
                ZXRotatedTriangle.vectors[1] = MyHelper.multiplyMatrixVector(ZRotatedTriangle.vectors[1], rotX);
                ZXRotatedTriangle.vectors[2] = MyHelper.multiplyMatrixVector(ZRotatedTriangle.vectors[2], rotX);

                translatedTriangle = new MyTriangle(ZXRotatedTriangle);
                translatedTriangle.vectors[0].z = ZXRotatedTriangle.vectors[0].z + 3.0f;
                translatedTriangle.vectors[1].z = ZXRotatedTriangle.vectors[1].z + 3.0f;
                translatedTriangle.vectors[2].z = ZXRotatedTriangle.vectors[2].z + 3.0f;

                // deal with the normals
                MyVector3D normalLine, firstLine, secondLine;
                normalLine = new MyVector3D(1.0f, 1.0f, 1.0f);
                firstLine = new MyVector3D(1.0f, 1.0f, 1.0f);
                secondLine = new MyVector3D(1.0f, 1.0f, 1.0f);

                firstLine.x = translatedTriangle.vectors[1].x - translatedTriangle.vectors[0].x;
                firstLine.y = translatedTriangle.vectors[1].y - translatedTriangle.vectors[0].y;
                firstLine.z = translatedTriangle.vectors[1].z - translatedTriangle.vectors[0].z;

                secondLine.x = translatedTriangle.vectors[2].x - translatedTriangle.vectors[0].x;
                secondLine.y = translatedTriangle.vectors[2].y - translatedTriangle.vectors[0].y;
                secondLine.z = translatedTriangle.vectors[2].z - translatedTriangle.vectors[0].z;

                normalLine.x = firstLine.y * secondLine.z - firstLine.z * secondLine.y;
                normalLine.y = firstLine.z * secondLine.x - firstLine.x * secondLine.z;
                normalLine.z = firstLine.x * secondLine.y - firstLine.y * secondLine.x;

                float length = (float)Math.sqrt(normalLine.x*normalLine.x + normalLine.y*normalLine.y + normalLine.z*normalLine.z);
                normalLine.x /= length;
                normalLine.y /= length;
                normalLine.z /= length;

//                System.out.println(normalLine.z + " " + normalLine.y + " " + normalLine.x);

//                float dotProduct = (normalLine.x * (translatedTriangle.vectors[0].x - CameraVector.x) + normalLine.y
//                        * (translatedTriangle.vectors[0].y - CameraVector.y) + normalLine.z
//                        * (translatedTriangle.vectors[0].z - CameraVector.z));

                float dotProduct = MyHelper.dotProduct(normalLine.x, normalLine.y, normalLine.z,
                        (translatedTriangle.vectors[0].x - CameraVector.x),
                        (translatedTriangle.vectors[0].y - CameraVector.y),
                        (translatedTriangle.vectors[0].z - CameraVector.z));

//                System.out.println(dotProduct);

                if(dotProduct < 0.0f){
                    // Illumination
                    MyVector3D lightDirection = new MyVector3D(0.0f, 0.0f, -1.0f);
                    float lightLength = (float)Math.sqrt(lightDirection.x*lightDirection.x + lightDirection.y*lightDirection.y + lightDirection.z*lightDirection.z);
                    lightDirection.x /= lightLength;
                    lightDirection.y /= lightLength;
                    lightDirection.z /= lightLength;

                    float lightDotProduct = MyHelper.dotProduct(normalLine.x, normalLine.y, normalLine.z, lightDirection.x, lightDirection.y, lightDirection.z);
//                    System.out.println(lightDotProduct);
                    float lightModifier = 50.0f;
                    float redElement = Color.green.getRed() - lightDotProduct*lightModifier;
                    float greenElement = Color.green.getGreen() - lightDotProduct*lightModifier;
                    float blueElement = Color.green.getBlue() - lightDotProduct*lightModifier;
                    if(redElement < 0) { redElement = 1; }
                    if(greenElement < 0) { greenElement = 1; }
                    if(blueElement < 0) { blueElement = 1; }
                    if(redElement > 254) { redElement = 254; }
                    if(greenElement > 254) { greenElement = 254; }
                    if(blueElement > 254) { blueElement = 254; }
                    System.out.println(greenElement);
                    Color newGreen = new Color((int)redElement, (int)greenElement, (int)blueElement);


                    // project the triangle
                    projectedTriangle.vectors[0] = MyHelper.multiplyMatrixVector(translatedTriangle.vectors[0], projMat);
                    projectedTriangle.vectors[1] = MyHelper.multiplyMatrixVector(translatedTriangle.vectors[1], projMat);
                    projectedTriangle.vectors[2] = MyHelper.multiplyMatrixVector(translatedTriangle.vectors[2], projMat);

                    // scale the triangle
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    projectedTriangle.vectors[0].x += 1.0f;
                    projectedTriangle.vectors[0].y += 1.0f;
//                  projectedTriangle.vectors[0].z += 1.0f;

                    projectedTriangle.vectors[1].x += 1.0f;
                    projectedTriangle.vectors[1].y += 1.0f;
//                  projectedTriangle.vectors[1].z += 1.0f;

                    projectedTriangle.vectors[2].x += 1.0f;
                    projectedTriangle.vectors[2].y += 1.0f;
//                  projectedTriangle.vectors[2].z += 1.0f;

                    projectedTriangle.vectors[0].x *= 0.5f * (float)screenSize.width;
                    projectedTriangle.vectors[0].y *= 0.5f * (float)screenSize.height;
                    projectedTriangle.vectors[1].x *= 0.5f * (float)screenSize.width;
                    projectedTriangle.vectors[1].y *= 0.5f * (float)screenSize.height;
                    projectedTriangle.vectors[2].x *= 0.5f * (float)screenSize.width;
                    projectedTriangle.vectors[2].y *= 0.5f * (float)screenSize.height;

                    MyHelper.fillMyTriangle(g, projectedTriangle, newGreen);
                }
            }
        }


    }

    public void meshInitializer(){

        // Put your meshes here
        MyMesh firstCube = new MyMesh();
        // South Face
        firstCube.addTriangle(new MyTriangle(new MyVector3D(0.0f, 0.0f, 0.0f), new MyVector3D(0.0f, 1.0f, 0.0f), new MyVector3D(1.0f, 1.0f, 0.0f)));
        firstCube.addTriangle(new MyTriangle(new MyVector3D(0.0f, 0.0f, 0.0f), new MyVector3D(1.0f, 1.0f, 0.0f), new MyVector3D(1.0f, 0.0f, 0.0f)));

        // East Face
        firstCube.addTriangle(new MyTriangle(new MyVector3D(1.0f, 0.0f, 0.0f), new MyVector3D(1.0f, 1.0f, 0.0f), new MyVector3D(1.0f, 1.0f, 1.0f)));
        firstCube.addTriangle(new MyTriangle(new MyVector3D(1.0f, 0.0f, 0.0f), new MyVector3D(1.0f, 1.0f, 1.0f), new MyVector3D(1.0f, 0.0f, 1.0f)));

        // North Face
        firstCube.addTriangle(new MyTriangle(new MyVector3D(1.0f, 0.0f, 1.0f), new MyVector3D(1.0f, 1.0f, 1.0f), new MyVector3D(0.0f, 1.0f, 1.0f)));
        firstCube.addTriangle(new MyTriangle(new MyVector3D(1.0f, 0.0f, 1.0f), new MyVector3D(0.0f, 1.0f, 1.0f), new MyVector3D(0.0f, 0.0f, 1.0f)));

        // West Face
        firstCube.addTriangle(new MyTriangle(new MyVector3D(0.0f, 0.0f, 1.0f), new MyVector3D(0.0f, 1.0f, 1.0f), new MyVector3D(0.0f, 1.0f, 0.0f)));
        firstCube.addTriangle(new MyTriangle(new MyVector3D(0.0f, 0.0f, 1.0f), new MyVector3D(0.0f, 1.0f, 0.0f), new MyVector3D(0.0f, 0.0f, 0.0f)));

        //Top Face
        firstCube.addTriangle(new MyTriangle(new MyVector3D(0.0f, 1.0f, 0.0f), new MyVector3D(0.0f, 1.0f, 1.0f), new MyVector3D(1.0f, 1.0f, 1.0f)));
        firstCube.addTriangle(new MyTriangle(new MyVector3D(0.0f, 1.0f, 0.0f), new MyVector3D(1.0f, 1.0f, 1.0f), new MyVector3D(1.0f, 1.0f, 0.0f)));

        //Bottom Face
        firstCube.addTriangle(new MyTriangle(new MyVector3D(1.0f, 0.0f, 1.0f), new MyVector3D(0.0f, 0.0f, 1.0f), new MyVector3D(0.0f, 0.0f, 0.0f)));
        firstCube.addTriangle(new MyTriangle(new MyVector3D(1.0f, 0.0f, 1.0f), new MyVector3D(0.0f, 0.0f, 0.0f), new MyVector3D(1.0f, 0.0f, 0.0f)));

        meshes.add(firstCube);
        // end of where you put meshes
    }

    public void makeProjectionMatrix(){
        float fNear = 0.1f;
        float fFar = 1000.0f;
        float fFov = 90.0f;
        //find screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        float fAspectRatio = ((float)height)/((float)width);
        //
        float fFovRad = 1.0f / (float)Math.tan((fFov * 0.5f / 180.0f * 3.14159f));
        projMat[0][0] = fAspectRatio * fFovRad;
        projMat[1][1] = fFovRad;
        projMat[2][2] = fFar / (fFar - fNear);
        projMat[3][2] = (-fFar * fNear) / (fFar - fNear);
        projMat[2][3] = 1.0f;
        projMat[3][3] = 0.0f;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {}

    @Override
    public void mousePressed(MouseEvent mouseEvent) {}

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {}

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {}

    @Override
    public void mouseExited(MouseEvent mouseEvent) {}

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {}

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {}
}
