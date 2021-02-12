import java.util.ArrayList;

public class MyMesh {
    ArrayList<MyTriangle> triangles = new ArrayList<>();

    public void addTriangle(MyTriangle newTriangle){
        triangles.add(newTriangle);
    }
}
