import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class MyMesh {
    ArrayList<MyTriangle> triangles = new ArrayList<>();

    public void addTriangle(MyTriangle newTriangle) {
        triangles.add(newTriangle);
    }

    public boolean loadMyObject(String filename) throws FileNotFoundException {
        
        Scanner input = new Scanner(new File(filename));
        ArrayList<MyVector3D> localvecs = new ArrayList<>();

        while(input.hasNextLine()){
            String line = input.nextLine();

            try{
                if(line.charAt(0) == 'v'){
                    line = line.replaceFirst("v ", "");
                    String[] vectorsCoords = line.split(" ");
                    MyVector3D newVec = new MyVector3D(Float.parseFloat(vectorsCoords[0]), Float.parseFloat(vectorsCoords[1]), Float.parseFloat(vectorsCoords[2]));
                    localvecs.add(newVec);
                }
    
                if(line.charAt(0) == 'f'){
                    line = line.replaceFirst("f ", "");
                    String[] fNums = line.split(" ");
                    int[] fNumsParsed = Arrays.stream(fNums).mapToInt(i -> Integer.parseInt(i)).toArray();
                    triangles.add(new MyTriangle(localvecs.get(fNumsParsed[0] - 1), localvecs.get(fNumsParsed[1] - 1), localvecs.get(fNumsParsed[2] - 1)));
                }
            } catch(Exception e){}
        }

        return true;
    }
}
