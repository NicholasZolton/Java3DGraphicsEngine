import java.awt.*;

public class MyHelper {

    static public MyVector3D multiplyMatrixVector(MyVector3D i, float[][] m){
        MyVector3D o = new MyVector3D(0.0f, 0.0f, 0.0f);
        o.x = i.x * m[0][0] + i.y * m[1][0] + i.z * m[2][0] + m[3][0];
        o.y = i.x * m[0][1] + i.y * m[1][1] + i.z * m[2][1] + m[3][1];
        o.z = i.x * m[0][2] + i.y * m[1][2] + i.z * m[2][2] + m[3][2];
        float w = i.x * m[0][3] + i.y * m[1][3] + i.z *m[2][3] + m[3][3];

        if (w != 0.0f){
            o.x /= w;
            o.y /= w;
            o.z /= w;
        }

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
}
