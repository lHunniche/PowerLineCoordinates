import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class Main
{

    public static void main(String[] args) throws ExecutionException, InterruptedException
    {
        long startId = 2232529700L;
        long endId = startId + 100;
        AtomicInteger arbejde = new AtomicInteger(1);
        Collection<Future<?>> futures = new LinkedList<Future<?>>();

        System.out.println("Begynder " + (endId - startId) + " HTTP kald...");
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        for (long i = startId; i < endId; i++)
        {
            long finalI = i;
            futures.add(executor.submit(() ->
            {
                HttpCoordinateManager.getInstance().cacheCoordinatesFromId(finalI);
                System.out.println(arbejde.getAndIncrement() + " kald lavet...");
            }));


        }

        for (Future<?> future : futures)
        {
            future.get();
        }

        for (Coordinate c : HttpCoordinateManager.getInstance().coordinates)
        {
            System.out.println("-------------------------------------------");
            System.out.println("Lat: " + c.getLatitude());
            System.out.println("Lon: " + c.getLongitude());
            System.out.println("Denmark: " + c.isDenmark());
        }
        System.out.println("-------------------------------------------\"");
        System.out.println("Antal fejl: " + HttpCoordinateManager.getInstance().errorCount);
        System.out.println("Antal danske noder: " + HttpCoordinateManager.getInstance().danishNodes);

        executor.shutdown();
    }
}
