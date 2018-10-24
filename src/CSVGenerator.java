import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CSVGenerator {




    public static void writeDataLineByLine(ArrayList<Coordinate> coordinates)
    {
        // first create file object for file placed at location
        // specified by filepath
        File file = new File("Pylon.csv");
        try {
            // create FileWriter object with file as parameter
            FileWriter writer = new FileWriter(file);


            // adding header to csv
            String header = "lat,long,name,color,note";
            writer.write(header);
            writer.write("\n");


            // add data to csv
            for (Coordinate coordinate:coordinates) {
                String data1 = coordinate.getLatitude()+","+coordinate.getLongitude()+","+coordinate.getNodeId()+",#FF0000,Pylon";
                writer.write(data1);
                writer.write("\n");
            }


            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
