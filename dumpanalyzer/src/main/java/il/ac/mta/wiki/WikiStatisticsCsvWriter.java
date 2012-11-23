package il.ac.mta.wiki;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * @author AlexeyR
 * @since 8/4/12 2:04 PM
 */

public class WikiStatisticsCsvWriter extends WikiStatisticsWriter
{

    private final PrintWriter out;
    private static DateTimeFormatter fmt = ISODateTimeFormat.dateTime();


    public WikiStatisticsCsvWriter() throws IOException
    {
        FileWriter fw = new FileWriter("out.csv", false);
        out = new PrintWriter(fw);

    }

    @Override
    public void writeHeader(List<PageStatistic.EditUniquenessCaclulationStrategy> ucss)
    {
        out.print("pageId\tpageTitle\tweek\ttotalEdits\trunningAvg");
        for (PageStatistic.EditUniquenessCaclulationStrategy strategy :ucss)
            out.format("\t%s", strategy.toString());
        out.print("\n");
    }

    @Override
    public void writeLine(PageStatistic pageStatistic)
    {
        for (Map.Entry<Integer, PageStatistic.RevisionWeekStatistic> entry : pageStatistic.revisionStatistics.entrySet())
        {


            out.format("%s\t%s\t%s\t%s", pageStatistic.pageId, pageStatistic.pageTitle, entry.getKey(),
                    entry.getValue().edits);
            updateRunningAverage(pageStatistic, entry);
            out.format("\t%s", runningAverages.get(pageStatistic.pageId));

            for (Map.Entry<PageStatistic.EditUniquenessCaclulationStrategy, Integer> uniqueTotal : entry.getValue().uniqueTotals.entrySet())
                out.format("\t%s", uniqueTotal.getValue());
            out.print("\n");
        }
        out.flush();
    }

    @Override
    public void flush()
    {
        out.flush();
    }

    @Override
    public void close()
    {
        out.close();
    }
}
