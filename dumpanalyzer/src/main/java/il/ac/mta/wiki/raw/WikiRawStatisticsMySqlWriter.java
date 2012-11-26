package il.ac.mta.wiki.raw;

import java.sql.*;

/**
 * @author AlexeyR
 * @since 11/23/12 11:38 AM
 */

public class WikiRawStatisticsMySqlWriter extends WikiRawStatisticsWriter
{
    private Connection connection;
    StringBuilder insert = new StringBuilder();

    public WikiRawStatisticsMySqlWriter(String url, String dbName, String userName, String password)
    {
        System.out.println("Connecting to MySql.");
        connection = null;
        try
        {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection(url + dbName, userName, password);
            System.out.println("Connected to the database");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    protected void writeHeader()
    {
        try
        {
            Statement s = connection.createStatement();
            s.executeUpdate("DROP TABLE IF EXISTS `pages_raw_statistics`");


            StringBuilder table = new StringBuilder();
            table.append("CREATE TABLE `pages_raw_statistics` (\n");
            table.append("  `page_id` int(11) NOT NULL,\n");
            table.append("  `page_title` varchar(512) COLLATE utf8_bin NOT NULL,\n");
            table.append("  `revision_time` DATETIME  NOT NULL,\n");
            table.append("  `user_id` varchar(512) NOT NULL,\n");
            table.append("  `is_anonymous` tinyint(1) NOT NULL,\n");
            table.append("  `is_bot` tinyint(1) NOT NULL,\n");
            table.append("  `millis_since_previous_revision` bigint(20) DEFAULT NULL,\n");
            table.append("  PRIMARY KEY (`page_id`,`revision_time`),\n");
            table.append("  KEY `page_id` (`page_id`)\n");
            table.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;");
            s.executeUpdate(table.toString());
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        insert.append("insert into `wiki`.`pages_raw_statistics`\n");
        insert.append("            (`page_id`,\n");
        insert.append("             `page_title`,\n");
        insert.append("             `revision_time`,\n");
        insert.append("             `user_id`,\n");
        insert.append("             `is_anonymous`,\n");
        insert.append("             `is_bot`)\n");
        insert.append("VALUES (?,?,?,?,?,?)");
    }

    @Override
    protected void writeLine(RevisionDataPoint dataPoint)
    {
        try
        {

            PreparedStatement s = connection.prepareStatement(insert.toString());
            s.setInt(1, dataPoint.pageId);
            s.setString(2, dataPoint.pageTitle);
            s.setTimestamp(3, dataPoint.revisionTime);
            s.setString(4, dataPoint.userId);
            s.setBoolean(5, dataPoint.isAnonymous);
            s.setBoolean(6, dataPoint.isBot);

            s.executeUpdate();
            s.close();

        }
        catch (SQLException e)
        {
            if (e.getErrorCode() == 1062)
            {
                dataPoint.revisionTime = new Timestamp(dataPoint.revisionTime.getTime() + 1);
                writeLine(dataPoint);
            }
            else
                e.printStackTrace();
        }
    }


    @Override
    public void flush()
    {

    }

    @Override
    public void close()
    {
        try
        {
            connection.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
