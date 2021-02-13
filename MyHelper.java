import java.awt.*;

public class MyHelper {

    static public MyVector3D multiplyMatrixVector(MyVector3D i, float[][] m){
        MyVector3D o = new MyVector3D(0.0f, 0.0f, 0.0f);
        o.x = i.x * m[0][0] + i.y * m[1][0] + i.z * m[2][0] + m[3][0];
        o.y = i.x * m[0][1] + i.y * m[1][1] + i.z * m[2][1] + m[3][1];
        o.z = i.x * m[0][2] + i.y * m[1][2] + i.z * m[2][2] + m[3][2];
        o.w = i.x * m[0][3] + i.y * m[1][3] + i.z * m[2][3] + m[3][3];

        return o;
    }

    static public void drawTriangle(Graphics g, int x1, int y1, int x2, int y2, int x3, int y3, Color color){
        Color previousColor = g.getColor();
        g.setColor(color);
        g.drawLine(x1, y1, x2, y2);
        g.drawLine(x2, y2, x3, y3);
        g.drawLine(x3, y3, x1, y1);
        g.setColor(previousColor);
    }

    static public void fillMyTriangle(Graphics g, int x1, int y1, int x2, int y2, int x3, int y3, Color color){
        Color previousColor = g.getColor();
        g.setColor(color);
        g.fillPolygon(new int[]{x1, x2, x3}, new int[]{y1, y2, y3}, 3);
        g.setColor(previousColor);
    }

    static public void fillMyTriangle(Graphics g, MyTriangle triangle, Color color){
        Color previousColor = g.getColor();
        g.setColor(color);
        g.fillPolygon(new int[]{(int)triangle.vectors[0].x, (int)triangle.vectors[1].x, (int)triangle.vectors[2].x}, new int[]{(int)triangle.vectors[0].y, (int)triangle.vectors[1].y, (int)triangle.vectors[2].y}, 3);
        g.setColor(previousColor);
    }

    static public void drawMyTriangle(Graphics g, MyTriangle triangle, Color color){
        drawTriangle(g, (int)triangle.vectors[0].x, (int)triangle.vectors[0].y, (int)triangle.vectors[1].x, (int)triangle.vectors[1].y, (int)triangle.vectors[2].x, (int)triangle.vectors[2].y, color);
    }

    static public float dotProduct(float x1, float y1, float z1, float x2, float y2, float z2){
        float myDotProduct = x1 * x2 + y1 * y2 + z1 * z2;
        return myDotProduct;
    }

    static public float[][] makeIdentityMatrix(){
        float[][] matrix = new float[4][4];
        matrix[0][0] = 1.0f;
        matrix[1][1] = 1.0f;
        matrix[2][2] = 1.0f;
        matrix[3][3] = 1.0f;

        return matrix;
    }

    static public float[][] makeXRotationMatrix(float floatRadians){
        float[][] matrix = new float[4][4];
        matrix[0][0] = 1.0f;
        matrix[1][1] = (float)Math.cos(floatRadians);
        matrix[1][2] = (float)Math.sin(floatRadians);
        matrix[2][1] = -(float)Math.sin(floatRadians);
        matrix[2][2] = (float)Math.cos(floatRadians);
        matrix[3][3] = 1.0f;
        return matrix;
    }

    static public float[][] makeYRotationMatrix(float floatRadians){
        float[][] matrix = new float[4][4];
        matrix[0][0] = (float)Math.cos(floatRadians);
        matrix[0][2] = (float)Math.sin(floatRadians);
        matrix[2][0] = -(float)Math.sin(floatRadians);
        matrix[1][1] = 1.0f;
        matrix[2][2] = (float)Math.cos(floatRadians);
        matrix[3][3] = 1.0f;
        return matrix;
    }

    static public float[][] makeZRotationMatrix(float floatRadians){
        float[][] matrix = new float[4][4];
        matrix[0][0] = (float)Math.cos(floatRadians);
        matrix[0][1] = (float)Math.sin(floatRadians);
        matrix[1][0] = -(float)Math.sin(floatRadians);
        matrix[1][1] = (float)Math.cos(floatRadians);
        matrix[2][2] = 1.0f;
        matrix[3][3] = 1.0f;
        return matrix;
    }

    static public float[][] makeTranslationMatrix(float x, float y, float z){
        float[][] matrix = new float[4][4];
        matrix[0][0] = 1.0f;
        matrix[1][1] = 1.0f;
        matrix[2][2] = 1.0f;
        matrix[3][3] = 1.0f;
        matrix [3][0] = x;
        matrix[3][1] = y;
        matrix[3][2] = z;
        return matrix;
    }

    static public float[][] makeProjectionMatrix(float fFovDegrees, float fNear, float fFar){
        float[][] matrix = new float[4][4];
        //find screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        float fAspectRatio = ((float)height)/((float)width);
        //
        float fFovRad = 1.0f / (float)Math.tan((fFovDegrees * 0.5f / 180.0f * 3.14159f));
        matrix[0][0] = fAspectRatio * fFovRad;
        matrix[1][1] = fFovRad;
        matrix[2][2] = fFar / (fFar - fNear);
        matrix[3][2] = (-fFar * fNear) / (fFar - fNear);
        matrix[2][3] = 1.0f;
        matrix[3][3] = 0.0f;
        return matrix;
    }

    static public float[][] multiplyMatrix(float[][] m1, float[][] m2){
        float[][] matrix = new float[4][4];
        for(int i = 0; i <  4; i++){
            for(int j = 0; j < 4; j++){
                matrix[j][i] = m1[j][0] * m2[0][i] + m1[j][1] * m2[1][i] + m1[j][2] * m2[2][i] + m1[j][3] * m2[3][i];
            }
        }
        return matrix;
    }

    static public float[][] invertMatrix(float[][] m){
        float[][] matrix = new float[4][4];
        matrix[0][0] = m[0][0]; matrix[0][1] = m[1][0]; matrix[0][2] = m[2][0]; matrix[0][3] = 0.0f;
		matrix[1][0] = m[0][1]; matrix[1][1] = m[1][1]; matrix[1][2] = m[2][1]; matrix[1][3] = 0.0f;
		matrix[2][0] = m[0][2]; matrix[2][1] = m[1][2]; matrix[2][2] = m[2][2]; matrix[2][3] = 0.0f;
		matrix[3][0] = -(m[3][0] * matrix[0][0] + m[3][1] * matrix[1][0] + m[3][2] * matrix[2][0]);
		matrix[3][1] = -(m[3][0] * matrix[0][1] + m[3][1] * matrix[1][1] + m[3][2] * matrix[2][1]);
		matrix[3][2] = -(m[3][0] * matrix[0][2] + m[3][1] * matrix[1][2] + m[3][2] * matrix[2][2]);
		matrix[3][3] = 1.0f;
        return matrix;
    }

    public static MyVector3D addVectors(MyVector3D v1, MyVector3D v2){
        MyVector3D newVector = new MyVector3D(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
        return newVector;
    }

    public static MyVector3D subtractVectors(MyVector3D v1, MyVector3D v2){
        MyVector3D newVector = new MyVector3D(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
        return newVector;
    }

    public static MyVector3D multiplyVector(MyVector3D v1, float mod){
        MyVector3D newVector = new MyVector3D(v1.x * mod, v1.y * mod, v1.z * mod);
        return newVector;
    }

    public static MyVector3D divideVector(MyVector3D v1, float mod){
        MyVector3D newVector = new MyVector3D(v1.x / mod, v1.y / mod, v1.z / mod);
        return newVector;
    }

    public static float vectorDotProduct(MyVector3D v1, MyVector3D v2){
        float output = dotProduct(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z);
        return output;
    }

    public static float vectorLength(MyVector3D v1){
        return (float)Math.sqrt(vectorDotProduct(v1, v1));
    }

    public static MyVector3D normalizeVector(MyVector3D v1){
        float length = vectorLength(v1);
        float newVecX = v1.x/length;
        float newVecY = v1.y/length;
        float newVecZ = v1.z/length;
        MyVector3D newVec = new MyVector3D(newVecX, newVecY, newVecZ);
        return newVec;
    }

    public static MyVector3D vectorCrossProduct(MyVector3D v1, MyVector3D v2){
        MyVector3D vOut = new MyVector3D();
        vOut.x = v1.y * v2.z - v1.z * v2.y;
        vOut.y = v1.z * v2.x - v1.x * v2.z;
        vOut.z = v1.x * v2.y - v1.y * v2.x;
        return vOut;
    }
}
