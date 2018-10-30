import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GeoJSONReader
{
    public static ArrayList<Coordinate> coordinates = new ArrayList<>();

    public static void readAndExtractPowerLines(String docUrl) throws FileNotFoundException, ExecutionException, InterruptedException
    {
        File file = new File(docUrl);
        Scanner scanner = new Scanner(file);
        StringBuffer content = new StringBuffer();
        long counter = 0;
        while(scanner.hasNextLine())
        {
            content.append(scanner.nextLine());
        }


        JSONObject jsonObject = new JSONObject(content.toString());
        JSONArray features = (JSONArray) jsonObject.get("features");


        long timeStart = System.currentTimeMillis();
        AtomicInteger workComplete = new AtomicInteger(0);
        Collection<Future<?>> futures = new LinkedList<Future<?>>();

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);

        for(int i = 0; i < features.length(); i++)
        {
            int finalI = i;
            futures.add(executor.submit(() ->
            {
                System.out.println("Beginning work on index: " + workComplete.getAndIncrement());
                JSONArray jsonCoords = (JSONArray) features.getJSONObject(finalI).getJSONObject("geometry").get("coordinates");
                if(jsonCoords != null)
                {
                    new Coordinate(){{

                        setLongitude(jsonCoords.get(0) + "");
                        setLatitude(jsonCoords.get(1) + "");
                        if(DenmarkChecker.isThisDenmark(this))
                        {
                            coordinates.add(this);
                        }
                    }};
                }
            }));
        }

        for (Future<?> future : futures)
        {
            future.get();
        }
        executor.shutdown();
        long timeEnd = System.currentTimeMillis();
        long timeTaken = timeEnd - timeStart;

        System.out.println("-------------------------------------------");
        System.out.println("Time taken: " + String.format("%d min %d sec",
                TimeUnit.MILLISECONDS.toMinutes(timeTaken),
                TimeUnit.MILLISECONDS.toSeconds(timeTaken) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeTaken))
        ));
        System.out.println("DenmarkChecker Errors: " + DenmarkChecker.errorCounter);
        System.out.println("-------------------------------------------");

        System.out.println("Generating CSV file...");
        CSVGenerator.writeDataLineByLine(coordinates);
        System.out.println("Done!");
    }


    public static void main(String[] args) throws FileNotFoundException, ExecutionException, InterruptedException
    {
        readAndExtractPowerLines("export.geojson");
    }

}
