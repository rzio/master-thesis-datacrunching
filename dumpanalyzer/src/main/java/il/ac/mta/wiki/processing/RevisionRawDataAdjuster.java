package il.ac.mta.wiki.processing;

import java.util.concurrent.*;

/**
 * @author AlexeyR
 * @since 11/24/12 12:29 PM
 */

public class RevisionRawDataAdjuster
{
    private WikiRawStatisticsMySqlDao dao;
    private final ExecutorService threadPool;

    public RevisionRawDataAdjuster(WikiRawStatisticsMySqlDao dao){
        this.dao = dao;
        threadPool = Executors.newFixedThreadPool(20);
    }

    public void process() throws InterruptedException
    {

        int[] range = dao.getPageIdsRange();
        if (range[0] < range[1])
        {
            for (int id = range[0]; id <= range[1]; id++)
            {
                threadPool.execute(new RevisionDataProcessor(id, dao));
            }
        }

        // Wait until all tasks have finished
        threadPool.shutdown();
        threadPool.awaitTermination(5,TimeUnit.DAYS);
    }
}
