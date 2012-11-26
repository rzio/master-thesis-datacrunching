package il.ac.mta.wiki.processing;

import java.sql.Timestamp;

/**
 * @author AlexeyR
 * @since 11/24/12 11:57 AM
 */

public class RevisionAdjustData
{
    public int pageId;
    public Timestamp revisionTimestamp;
    public Long millisSincePreviousRevision;

    public RevisionAdjustData(int pageId, Timestamp revisionTimestamp, Long millisSincePreviousRevision)
    {
        this.pageId = pageId;
        this.revisionTimestamp = revisionTimestamp;
        this.millisSincePreviousRevision = millisSincePreviousRevision;
    }
}
