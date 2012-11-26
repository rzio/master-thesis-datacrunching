package il.ac.mta.wiki;

import il.ac.mta.wiki.processing.RevisionRawDataAdjuster;
import il.ac.mta.wiki.processing.WikiRawStatisticsMySqlDao;

import java.io.IOException;
import java.text.ParseException;

/**
 * @author AlexeyR
 * @since 8/4/12 12:04 PM
 */

public class DBProcessor
{
    public static void main(String[] args) throws IOException, ParseException
    {
        WikiRawStatisticsMySqlDao dao = new WikiRawStatisticsMySqlDao("jdbc:mysql://localhost:3306/", "wiki", "test", "test");

        RevisionRawDataAdjuster adj = new RevisionRawDataAdjuster(dao);

        try
        {
            adj.process();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

    }

}
