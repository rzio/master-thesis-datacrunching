package il.ac.mta.wiki.processing;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author AlexeyR
 * @since 11/24/12 12:11 PM
 */

public class RevisionDataProcessor implements Runnable
{
    private final int pageId;
    private final WikiRawStatisticsMySqlDao dao;

    public RevisionDataProcessor(int pageId, WikiRawStatisticsMySqlDao dao)
    {
        this.pageId = pageId;
        this.dao = dao;
    }

    @Override
    public void run()
    {
        System.out.println(String.format("reading page: %s from db",pageId));
        List<RevisionAdjustData> revisions = dao.getRevisions(pageId);
        Timestamp prevTimestamp = null;

        for (RevisionAdjustData revision : revisions)
        {
            if (prevTimestamp != null)
                revision.millisSincePreviousRevision = revision.revisionTimestamp.getTime() - prevTimestamp.getTime();

            prevTimestamp = revision.revisionTimestamp;
        }

        dao.updateRevisions(revisions);
        System.out.println(String.format("written page: %s from db",pageId));
    }
}
