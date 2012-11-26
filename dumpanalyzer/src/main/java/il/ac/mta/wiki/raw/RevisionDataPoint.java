package il.ac.mta.wiki.raw;

import java.sql.Timestamp;

/**
 * @author AlexeyR
 * @since 11/23/12 11:27 AM
 */

public class RevisionDataPoint
{
    public String pageTitle;
    public int pageId;
    public Timestamp revisionTime;
    public String userId;
    public boolean isAnonymous;
    public boolean isBot;

    public RevisionDataPoint(String pageTitle, int pageId, Timestamp revisionTime, String userId, boolean anonymous, boolean bot)
    {
        this.pageTitle = pageTitle;
        this.pageId = pageId;
        this.revisionTime = revisionTime;
        this.userId = userId;
        isAnonymous = anonymous;
        isBot = bot;
    }
}
