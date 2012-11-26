package il.ac.mta.wiki.weekly;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.util.*;

/**
 * @author AlexeyR
 * @since 8/4/12 4:34 PM
 */

public class PageWeeklyStatistic
{
    public String pageTitle;
    public int pageId;

    public Map<Integer, RevisionWeekStatistic> revisionStatistics = new TreeMap<Integer, RevisionWeekStatistic>();

    public PageWeeklyStatistic(String pageTitle, int pageId)
    {
        this.pageTitle = pageTitle;
        this.pageId = pageId;
    }

    public void addRevision(String userId, boolean isAnonymous, boolean isBot, long timestamp)
    {
        if (isBot)
            return;
        int week = new Period(new DateTime(2001, 1, 1, 0, 0, 0, 0), new DateTime(timestamp), PeriodType.weeks()).getWeeks();

        if (!revisionStatistics.containsKey(week))
            revisionStatistics.put(week, new RevisionWeekStatistic());


        revisionStatistics.get(week).addEdit(userId, isAnonymous, isBot, timestamp);
    }

    public static class RevisionWeekStatistic
    {
        public int edits = 0;
        public final Map<EditUniquenessCaclulationStrategy, Integer> uniqueTotals = new TreeMap<EditUniquenessCaclulationStrategy, java.lang.Integer>();


        public RevisionWeekStatistic()
        {
            uniqueTotals.put(new OncePerPeriodUniquenessCalculationStrategy(), 0);
            uniqueTotals.put(new ConsecutiveEditsUniquenessCalculationStrategy(), 0);
        }

        public void addEdit(String userId, boolean isAnonymous, boolean isBot, long timestamp)
        {
            edits++;
            for (EditUniquenessCaclulationStrategy strategy : uniqueTotals.keySet())
            {
                if (strategy.isUniqueEdit(userId, isAnonymous, isBot, timestamp))
                {
                    uniqueTotals.put(strategy, uniqueTotals.get(strategy) + 1);
                }
            }
        }
    }

    public static interface EditUniquenessCaclulationStrategy extends Comparable<EditUniquenessCaclulationStrategy>
    {
        boolean isUniqueEdit(String userId, boolean isAnonymous, boolean isBot, long timestamp);
    }

    public static class OncePerPeriodUniquenessCalculationStrategy implements EditUniquenessCaclulationStrategy
    {

        Set<String> userIds = new HashSet<String>();

        @Override
        public boolean isUniqueEdit(String userId, boolean isAnonymous, boolean isBot, long timestamp)
        {
            if (userIds.contains(userId))
                return false;

            userIds.add(userId);

            return true;
        }

        @Override
        public String toString()
        {
            return "OncePerPeriodUnique";
        }


        @Override
        public int hashCode()
        {
            return 1;
        }

        @Override
        public int compareTo(EditUniquenessCaclulationStrategy o)
        {
            return this.hashCode() - o.hashCode();
        }
    }

    public static class ConsecutiveEditsUniquenessCalculationStrategy implements EditUniquenessCaclulationStrategy
    {
        String lastUserId;
        long lastEditTimestamp;

        @Override
        public boolean isUniqueEdit(String userId, boolean isAnonymous, boolean isBot, long timestamp)
        {
            boolean retVal = false;
            if (!userId.equals(lastUserId) ||
                    (userId.equals(lastUserId) && timestamp > lastEditTimestamp + (1000 * 60 * 60)))
            {

                retVal = true;
            }

            lastUserId = userId;
            lastEditTimestamp = timestamp;

            return retVal;
        }

        @Override
        public String toString()
        {
            return "ConsecutiveEditsUnique";
        }

        @Override
        public int hashCode()
        {
            return 2;
        }

        @Override
        public int compareTo(EditUniquenessCaclulationStrategy o)
        {
            return this.hashCode() - o.hashCode();
        }
    }
}
