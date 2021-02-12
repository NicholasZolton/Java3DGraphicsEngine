public class MyTriangle {
    MyVector3D[] vectors = new MyVector3D[3];

    public MyTriangle(MyVector3D first, MyVector3D second, MyVector3D third){
        vectors[0] = first;
        vectors[1] = second;
        vectors[2] = third;
    }

    public MyTriangle(){
        vectors[0] = new MyVector3D();
        vectors[1] = new MyVector3D();
        vectors[2] = new MyVector3D();
    }

    public MyTriangle(MyTriangle copyThis){
        this.vectors = copyThis.vectors;
    }
}
