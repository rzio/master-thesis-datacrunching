package il.ac.mta.wiki;

import il.ac.mta.wiki.raw.RawStatisticsGatherer;
import il.ac.mta.wiki.raw.WikiRawStatisticsMySqlWriter;
import il.ac.mta.wiki.raw.WikiRawStatisticsWriter;
import org.mediawiki.dumper.ProgressFilter;
import org.mediawiki.dumper.Tools;
import org.mediawiki.importer.DumpWriter;
import org.mediawiki.importer.XmlDumpReader;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

/**
 * @author AlexeyR
 * @since 8/4/12 12:04 PM
 */

public class DumpConvertor
{
    static final int IN_BUF_SZ = 1024 * 1024;
    public static void main(String[] args) throws IOException, ParseException
        {
            InputStream input = Tools.openInputFile("C:\\data\\MTA\\enwiki-20120702-pages-meta-history1.xml-p000000010p000002593.bz2");

                    /*new LzmaInputStream(new BufferedInputStream(
                    new FileInputStream("C:\\data\\mtaData\\enwiki-20120702-pages-meta-history1.xml-p000000010p000002593.7z")
                    ,IN_BUF_SZ));
*/
            //WikiStatisticsWriter writer = new WikiStatisticsCsvWriter();
            //WikiWeeklyStatisticsWriter writer = new WikiWeeklyStatisticsMySqlWriter("jdbc:mysql://localhost:3306/","wiki","test","test");
            WikiRawStatisticsWriter writer = new WikiRawStatisticsMySqlWriter("jdbc:mysql://localhost:3306/","wiki","test","test");
            DumpWriter sink = new ProgressFilter(new RawStatisticsGatherer(writer),1000);


            XmlDumpReader reader = new XmlDumpReader(input, sink);
            reader.readDump();
        }

}
