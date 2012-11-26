package il.ac.mta.wiki.raw;

/**
 * @author alexeyr
 * @since 9/14/12 4:18 PM
 */

public abstract class WikiRawStatisticsWriter
{
    private boolean firstLine = true;

    public final void addStatistic(RevisionDataPoint dataPoint)
    {
        if (firstLine)
        {

            writeHeader();
            firstLine = false;
        }
        writeLine(dataPoint);
    }

    protected abstract void writeHeader();

    protected abstract void writeLine(RevisionDataPoint pageStatistic);

    public abstract void flush();

    public abstract void close();


}
