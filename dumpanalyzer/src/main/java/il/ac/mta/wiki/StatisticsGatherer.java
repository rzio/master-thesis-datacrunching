package il.ac.mta.wiki;

import org.mediawiki.importer.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * @author alex
 * @since 4/22/12 9:41 AM
 */

public class StatisticsGatherer implements DumpWriter
{

    protected static final String version = "0.3";
    protected static final String ns = "http://www.mediawiki.org/xml/export-" + version + "/";
    protected static final String schema = "http://www.mediawiki.org/xml/export-" + version + ".xsd";
    protected static final DateFormat dateFormat = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'");

    private final Charset utf8 = Charset.forName("UTF8");

    WikiStatisticsWriter statistics;
    private PageStatistic pageStatistic;

    public StatisticsGatherer(WikiStatisticsWriter writer) throws IOException
    {
        statistics = writer;
    }

    static
    {
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public void close() throws IOException
    {

    }

    public void writeStartWiki() throws IOException
    {

    }

    public void writeEndWiki() throws IOException
    {
        statistics.close();
    }

    public void writeSiteinfo(Siteinfo info) throws IOException
    {

    }

    public void writeStartPage(Page page) throws IOException
    {
        pageStatistic = new PageStatistic(page.Title.toString(), page.Id);
    }

    public void writeEndPage() throws IOException
    {
        statistics.addStatistic(pageStatistic);
        statistics.flush();

    }

    public void writeRevision(Revision rev) throws IOException
    {

        try
        {
            if (rev.Contributor.Username != null)
            {
                pageStatistic.addRevision(rev.Contributor.Username,
                        rev.Contributor.isIP,
                        rev.Contributor.Username.contains("bot") || rev.Contributor.Username.contains("Bot"),
                        rev.Timestamp.getTimeInMillis());
            }
        }
        catch (Exception e)
        {
            int i = 10;
        }

    }


}
