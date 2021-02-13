import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.FileNotFoundException;
import java.util.*;

public class DisplayPanel extends JPanel implements MouseMotionListener, MouseListener {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    ArrayList<MyMesh> meshes = new ArrayList<>();
    float[][] projMat = new float[4][4];
    MyVector3D CameraVector = new MyVector3D(0.0f, 0.0f, 0.0f);

    public DisplayPanel() throws FileNotFoundException {
        setBackground(Color.gray);
        makeProjectionMatrix();
        meshInitializer();
    }

    public void paint(Graphics g){
        super.paint(g);

        //Rotation Matrices
        float[][] rotX, rotZ, matworld, rotTranslate;
        rotX = new float[4][4];
        rotZ = new float[4][4];
        double fTheta = 1.0f * UniversalTimer.timeElapsed;

        // Z Rotation Matrix
        rotZ = MyHelper.makeZRotationMatrix((float)fTheta);

        // X Rotation Matrix
        rotX = MyHelper.makeXRotationMatrix((float)fTheta);

        // make translation matrix
        rotTranslate = MyHelper.makeTranslationMatrix(0f, 0f, 10.0f);

        // make world matrix
        matworld = MyHelper.multiplyMatrix(rotZ, rotX);
        matworld = MyHelper.multiplyMatrix(matworld, rotTranslate);

        ArrayList<MyTriangle> trianglesToDraw = new ArrayList<>();

        for(MyMesh mesh: meshes){
            for(MyTriangle triangle: mesh.triangles){
                MyTriangle translatedTriangle = new MyTriangle();
                MyTriangle projectedTriangle = new MyTriangle();
                
                translatedTriangle.vectors[0] = MyHelper.multiplyMatrixVector(triangle.vectors[0], matworld);
                translatedTriangle.vectors[1] = MyHelper.multiplyMatrixVector(triangle.vectors[1], matworld);
                translatedTriangle.vectors[2] = MyHelper.multiplyMatrixVector(triangle.vectors[2], matworld);
                
                // deal with the normals
                MyVector3D normalLine, firstLine, secondLine;
                normalLine = new MyVector3D(1.0f, 1.0f, 1.0f);
                firstLine = new MyVector3D(1.0f, 1.0f, 1.0f);
                secondLine = new MyVector3D(1.0f, 1.0f, 1.0f);

                firstLine = MyHelper.subtractVectors(translatedTriangle.vectors[1], translatedTriangle.vectors[0]);
                secondLine = MyHelper.subtractVectors(translatedTriangle.vectors[2], translatedTriangle.vectors[0]);

                normalLine = MyHelper.vectorCrossProduct(firstLine, secondLine);

                normalLine = MyHelper.normalizeVector(normalLine);

                MyVector3D vCamRay = MyHelper.subtractVectors(translatedTriangle.vectors[0], CameraVector);

                // float dotProduct = MyHelper.dotProduct(normalLine.x, normalLine.y, normalLine.z,
                //         (translatedTriangle.vectors[0].x - CameraVector.x),
                //         (translatedTriangle.vectors[0].y - CameraVector.y),
                //         (translatedTriangle.vectors[0].z - CameraVector.z));

                float dotProduct = MyHelper.vectorDotProduct(normalLine, vCamRay);

//                System.out.println(dotProduct);

                if(dotProduct < 0.0f){
                    // Illumination
                    MyVector3D lightDirection = new MyVector3D(0.0f, 0.0f, -1.0f);
                    float lightLength = (float)Math.sqrt(lightDirection.x*lightDirection.x + lightDirection.y*lightDirection.y + lightDirection.z*lightDirection.z);
                    lightDirection.x /= lightLength;
                    lightDirection.y /= lightLength;
                    lightDirection.z /= lightLength;

                    float lightDotProduct = MyHelper.vectorDotProduct(normalLine, lightDirection);
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
                    projectedTriangle.triangleColor = newGreen;

                    // project the triangle
                    projectedTriangle.vectors[0] = MyHelper.multiplyMatrixVector(translatedTriangle.vectors[0], projMat);
                    projectedTriangle.vectors[1] = MyHelper.multiplyMatrixVector(translatedTriangle.vectors[1], projMat);
                    projectedTriangle.vectors[2] = MyHelper.multiplyMatrixVector(translatedTriangle.vectors[2], projMat);

                    // normalize into cartesian space
                    projectedTriangle.vectors[0] = MyHelper.divideVector(projectedTriangle.vectors[0], projectedTriangle.vectors[0].w);
                    projectedTriangle.vectors[1] = MyHelper.divideVector(projectedTriangle.vectors[1], projectedTriangle.vectors[1].w);
                    projectedTriangle.vectors[2] = MyHelper.divideVector(projectedTriangle.vectors[2], projectedTriangle.vectors[2].w);

                    // scale the triangle
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    MyVector3D vOffsetView = new MyVector3D(1.0f, 1.0f, 0.0f);

                    projectedTriangle.vectors[0] = MyHelper.addVectors(projectedTriangle.vectors[0], vOffsetView);
                    projectedTriangle.vectors[1] = MyHelper.addVectors(projectedTriangle.vectors[1], vOffsetView);
                    projectedTriangle.vectors[2] = MyHelper.addVectors(projectedTriangle.vectors[2], vOffsetView);

                    projectedTriangle.vectors[0].x *= 0.5f * (float)screenSize.width;
                    projectedTriangle.vectors[0].y *= 0.5f * (float)screenSize.height;
                    projectedTriangle.vectors[1].x *= 0.5f * (float)screenSize.width;
                    projectedTriangle.vectors[1].y *= 0.5f * (float)screenSize.height;
                    projectedTriangle.vectors[2].x *= 0.5f * (float)screenSize.width;
                    projectedTriangle.vectors[2].y *= 0.5f * (float)screenSize.height;

//                    MyHelper.fillMyTriangle(g, projectedTriangle, newGreen);
                    trianglesToDraw.add(projectedTriangle);
                }
            }
        }

        trianglesToDraw.sort(new Comparator<MyTriangle>(){
            @Override
            public int compare(MyTriangle triangle1, MyTriangle triangle2) {
                float firstZAverage = (triangle1.vectors[0].z + triangle1.vectors[1].z + triangle1.vectors[2].z) / 3.0f;
                float secondZAverage = (triangle2.vectors[0].z + triangle2.vectors[1].z + triangle2.vectors[2].z) / 3.0f;
                if(secondZAverage > firstZAverage){ return 1; }
                if(secondZAverage < firstZAverage){ return -1; }
                return 0;
            }
        });
        for(int i = 0; i < trianglesToDraw.size(); i++){
            MyHelper.fillMyTriangle(g, trianglesToDraw.get(i), trianglesToDraw.get(i).triangleColor);
        }

    }

    public void meshInitializer() throws FileNotFoundException {

        // Put your meshes here
        MyMesh firstCube = new MyMesh();
//        // South Face
//        firstCube.addTriangle(new MyTriangle(new MyVector3D(0.0f, 0.0f, 0.0f), new MyVector3D(0.0f, 1.0f, 0.0f), new MyVector3D(1.0f, 1.0f, 0.0f)));
//        firstCube.addTriangle(new MyTriangle(new MyVector3D(0.0f, 0.0f, 0.0f), new MyVector3D(1.0f, 1.0f, 0.0f), new MyVector3D(1.0f, 0.0f, 0.0f)));
//
//        // East Face
//        firstCube.addTriangle(new MyTriangle(new MyVector3D(1.0f, 0.0f, 0.0f), new MyVector3D(1.0f, 1.0f, 0.0f), new MyVector3D(1.0f, 1.0f, 1.0f)));
//        firstCube.addTriangle(new MyTriangle(new MyVector3D(1.0f, 0.0f, 0.0f), new MyVector3D(1.0f, 1.0f, 1.0f), new MyVector3D(1.0f, 0.0f, 1.0f)));
//
//        // North Face
//        firstCube.addTriangle(new MyTriangle(new MyVector3D(1.0f, 0.0f, 1.0f), new MyVector3D(1.0f, 1.0f, 1.0f), new MyVector3D(0.0f, 1.0f, 1.0f)));
//        firstCube.addTriangle(new MyTriangle(new MyVector3D(1.0f, 0.0f, 1.0f), new MyVector3D(0.0f, 1.0f, 1.0f), new MyVector3D(0.0f, 0.0f, 1.0f)));
//
//        // West Face
//        firstCube.addTriangle(new MyTriangle(new MyVector3D(0.0f, 0.0f, 1.0f), new MyVector3D(0.0f, 1.0f, 1.0f), new MyVector3D(0.0f, 1.0f, 0.0f)));
//        firstCube.addTriangle(new MyTriangle(new MyVector3D(0.0f, 0.0f, 1.0f), new MyVector3D(0.0f, 1.0f, 0.0f), new MyVector3D(0.0f, 0.0f, 0.0f)));
//
//        //Top Face
//        firstCube.addTriangle(new MyTriangle(new MyVector3D(0.0f, 1.0f, 0.0f), new MyVector3D(0.0f, 1.0f, 1.0f), new MyVector3D(1.0f, 1.0f, 1.0f)));
//        firstCube.addTriangle(new MyTriangle(new MyVector3D(0.0f, 1.0f, 0.0f), new MyVector3D(1.0f, 1.0f, 1.0f), new MyVector3D(1.0f, 1.0f, 0.0f)));
//
//        //Bottom Face
//        firstCube.addTriangle(new MyTriangle(new MyVector3D(1.0f, 0.0f, 1.0f), new MyVector3D(0.0f, 0.0f, 1.0f), new MyVector3D(0.0f, 0.0f, 0.0f)));
//        firstCube.addTriangle(new MyTriangle(new MyVector3D(1.0f, 0.0f, 1.0f), new MyVector3D(0.0f, 0.0f, 0.0f), new MyVector3D(1.0f, 0.0f, 0.0f)));

        firstCube.loadMyObject("utahteapot.obj");

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
