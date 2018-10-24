import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main
{

    public static void main(String[] args) throws ExecutionException, InterruptedException
    {
        long startId = 0;
        long endId = startId + 5000;
//        long startId = 0;
//        long endId = startId + 1000;
        long timeStart = System.currentTimeMillis();
        AtomicInteger workComplete = new AtomicInteger(1);
        Collection<Future<?>> futures = new LinkedList<Future<?>>();

        System.out.println("Beginning " + (endId - startId) + " HTTP calls...");
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
        for (long i = startId; i < endId; i++)
        {
            long finalI = i;
            futures.add(executor.submit(() ->
            {
                HttpCoordinateManager.cacheCoordinatesFromId(finalI);
                System.out.println(workComplete.getAndIncrement() + " calls made...");
            }));


        }

        for (Future<?> future : futures)
        {
            future.get();
        }

        for (Coordinate c : HttpCoordinateManager.coordinates)
        {
            System.out.println("-------------------------------------------");
            System.out.println("Lat: " + c.getLatitude());
            System.out.println("Lon: " + c.getLongitude());
            System.out.println("Denmark: " + c.isDenmark());
        }
        long timeEnd = System.currentTimeMillis();
        long timeTaken = timeEnd - timeStart;

        System.out.println("-------------------------------------------\"");
        System.out.println("Time taken: " + String.format("%d min %d sec",
                TimeUnit.MILLISECONDS.toMinutes(timeTaken),
                TimeUnit.MILLISECONDS.toSeconds(timeTaken) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeTaken))
        ));
        System.out.println("Errors: " + HttpCoordinateManager.errorCount);
        System.out.println("Danish nodes: " + HttpCoordinateManager.danishNodes);
        System.out.println("Other nodes: " + HttpCoordinateManager.notDanishNodes);
        System.out.println("Total nodes searched: " + HttpCoordinateManager.getTotalCount());

        CSVGenerator.writeDataLineByLine(HttpCoordinateManager.coordinates);

        executor.shutdown();
    }
}
