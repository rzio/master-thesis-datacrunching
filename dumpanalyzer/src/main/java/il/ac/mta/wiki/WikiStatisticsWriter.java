package il.ac.mta.wiki;

import com.google.common.collect.ImmutableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author alexeyr
 * @since 9/14/12 4:18 PM
 */

public abstract class WikiStatisticsWriter
{
    protected Map<Integer, Double> runningAverages = new HashMap<Integer, Double>();
    protected Map<Integer, Integer> runningCount = new HashMap<Integer, Integer>();

    private boolean firstLine = true;

    public final void addStatistic(PageStatistic pageStatistic)
    {
        if (firstLine)
        {

            writeHeader(ImmutableList.<PageStatistic.EditUniquenessCaclulationStrategy>builder()
                    .addAll(pageStatistic.revisionStatistics.values().iterator().next().uniqueTotals.keySet())
                    .build());
            firstLine = false;
        }
        writeLine(pageStatistic);
    }

    protected abstract void writeHeader(List<PageStatistic.EditUniquenessCaclulationStrategy> ucss);

    protected abstract void writeLine(PageStatistic pageStatistic);

    protected void updateRunningAverage(PageStatistic pageStatistic, Map.Entry<Integer, PageStatistic.RevisionWeekStatistic> entry)
    {
        if (!runningAverages.containsKey(pageStatistic.pageId))
        {
            runningAverages.put(pageStatistic.pageId, (double) entry.getValue().edits);
            runningCount.put(pageStatistic.pageId, 1);
        }
        else
        {
            int count = runningCount.get(pageStatistic.pageId);
            double avg = (runningAverages.get(pageStatistic.pageId) * count + entry.getValue().edits) / (count + 1);
            runningAverages.put(pageStatistic.pageId, avg);
            runningCount.put(pageStatistic.pageId, count + 1);
        }
    }

    public abstract void flush();

    public abstract void close();


}
