package il.ac.mta.wiki.processing;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author AlexeyR
 * @since 11/23/12 11:38 AM
 */

public class WikiRawStatisticsMySqlDao
{
    private DataSource dataSource;
    String selectRevisions;
    String updateRevisions;

    public WikiRawStatisticsMySqlDao(String url, String dbName, String userName, String password)
    {
        System.out.println("Connecting to MySql.");
        dataSource = null;
        try
        {
            ComboPooledDataSource cpds = new ComboPooledDataSource();
            cpds.setDriverClass("com.mysql.jdbc.Driver");
            cpds.setJdbcUrl(url + dbName);
            cpds.setUser(userName);
            cpds.setPassword(password);
            cpds.setMaxStatements(180);
            cpds.setMinPoolSize(20);
            cpds.setInitialPoolSize(20);
            cpds.setMinPoolSize(50);
            cpds.setAcquireIncrement(5);
            dataSource = cpds;
            dataSource.getConnection(); // initializing connection pool
            System.out.println("Connected to the database");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        selectRevisions = new StringBuilder().
                append("select  `page_id`,")
                .append("  `page_title`,")
                .append("  `revision_time`,")
                .append("  `user_id`,")
                .append("  `is_anonymous`,")
                .append("  `is_bot`,")
                .append("  `millis_since_previous_revision`")
                .append("  from `wiki`.`pages_raw_statistics` where page_id = ? order by page_id, revision_time")
                .toString();

        updateRevisions = new StringBuilder()
                .append("update `wiki`.`pages_raw_statistics`")
                .append("set `millis_since_previous_revision` = ?  ")
                .append("where `page_id` = ? and `revision_time` = ?")
                .toString();
    }

    /**
     * gets the min and the max page ids in the DB
     * @return [min_page_id, max_page_id]
     */
    public int[] getPageIdsRange()

    {
        int ret[] = new int[]{0, 0};
        String query = "SELECT min(page_id) as min, max(page_id) as max from `wiki`.`pages_raw_statistics`";

        Connection connection = null;
        try
        {
            connection = dataSource.getConnection();
            PreparedStatement s = connection.prepareStatement(query);
            ResultSet rs = s.executeQuery();
            if (rs.next())
            {
                ret[0] = rs.getInt("min");
                ret[1] = rs.getInt("max");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        return ret;
    }

    public List<RevisionAdjustData> getRevisions(int pageId)
    {
        Connection connection = null;
        try
        {
            connection = dataSource.getConnection();
            PreparedStatement s = connection.prepareStatement(selectRevisions);
            s.setInt(1, pageId);
            ResultSet rs = s.executeQuery();

            List<RevisionAdjustData> ret = new ArrayList<RevisionAdjustData>();
            while (rs.next())
            {
//                int pageId = rs.getInt(1);
//                String pageTitle = rs.getString("page_title");
                Timestamp revisionTimestamp = rs.getTimestamp("revision_time");
                ret.add(new RevisionAdjustData(pageId, revisionTimestamp, null));
            }
            return ret;

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (connection != null)
                try
                {
                    connection.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
        }
        return null;
    }


    public List<RevisionAdjustData> updateRevisions(List<RevisionAdjustData> revisions)
    {
        Connection connection = null;
        try
        {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement s = connection.prepareStatement(updateRevisions);
            for (RevisionAdjustData d : revisions)
            {
                if (d.millisSincePreviousRevision != null)
                    s.setLong(1, d.millisSincePreviousRevision);
                else
                    s.setObject(1, null);
                s.setInt(2, d.pageId);
                s.setTimestamp(3, d.revisionTimestamp);
                s.addBatch();
            }

            s.executeBatch();
            connection.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (connection != null)
                try
                {
                    connection.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
        }
        return null;
    }


    public void close()
    {
        try
        {
            DataSources.destroy(dataSource);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
