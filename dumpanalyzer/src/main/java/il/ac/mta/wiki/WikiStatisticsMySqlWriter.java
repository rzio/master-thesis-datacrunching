package il.ac.mta.wiki;

import java.sql.*;
import java.util.List;
import java.util.Map;

/**
 * @author alexeyr
 * @since 9/14/12 4:23 PM
 */

public class WikiStatisticsMySqlWriter extends WikiStatisticsWriter
{

    private Connection connection;
    StringBuilder insert = new StringBuilder();

    public WikiStatisticsMySqlWriter(String url, String dbName, String userName, String password)
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
    public void writeHeader(List<PageStatistic.EditUniquenessCaclulationStrategy> ucss)
    {
        try
        {
            Statement s = connection.createStatement();
            s.executeUpdate("DROP TABLE IF EXISTS `pages_statistics`");
//            connection.commit();

            StringBuilder table = new StringBuilder();
            table.append("CREATE TABLE `pages_statistics` (\n");
            table.append("  `page_id` int(11) NOT NULL,\n");
            table.append("  `week` int(11) NOT NULL,\n");
            table.append("  `page_title` varchar(512) COLLATE utf8_bin NOT NULL,\n");
            table.append("  `total_edits` int(11) NOT NULL,\n");
            table.append("  `running_avg` double NOT NULL");
            for (PageStatistic.EditUniquenessCaclulationStrategy strategy : ucss)
            {
                table.append(",\n").append("  `").append(strategy.toString()).append("` int(11) NOT NULL");
            }
            table.append(",\n");
            table.append("  PRIMARY KEY (`page_id`,`week`),\n");
            table.append("  KEY `week` (`week`)\n");
            table.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;");
            s.executeUpdate(table.toString());
        }
        catch (SQLException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        insert.append("insert into `wiki`.`pages_statistics`\n");
        insert.append("            (`page_id`,\n");
        insert.append("             `week`,\n");
        insert.append("             `page_title`,\n");
        insert.append("             `total_edits`,\n");
        insert.append("             `running_avg`");
        StringBuilder values = new StringBuilder().append("VALUES (?,?,?,?,?");
        for (PageStatistic.EditUniquenessCaclulationStrategy strategy : ucss)
        {
            insert.append(",\n").append("  `").append(strategy.toString()).append("`");
            values.append(",?");
        }
        values.append(")");
        insert.append(")\n").append(values.toString());
    }

    @Override
    public void writeLine(PageStatistic pageStatistic)
    {
        try
        {
            for (Map.Entry<Integer, PageStatistic.RevisionWeekStatistic> entry : pageStatistic.revisionStatistics.entrySet())
            {


                PreparedStatement s = connection.prepareStatement(insert.toString());
                s.setInt(1, pageStatistic.pageId);
                s.setInt(2, entry.getKey());// week
                s.setString(3, pageStatistic.pageTitle);
                s.setInt(4, entry.getValue().edits);
                updateRunningAverage(pageStatistic, entry);
                s.setDouble(5, runningAverages.get(pageStatistic.pageId));
                int index = 6;
                for (Map.Entry<PageStatistic.EditUniquenessCaclulationStrategy, Integer> uniqueTotal : entry.getValue().uniqueTotals.entrySet())
                    s.setInt(index++, uniqueTotal.getValue());

                s.executeUpdate();
                s.close();
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public void flush()
    {
//        try
//        {
//            //connection.commit();
//        }
//        catch (SQLException e)
//        {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
    }

    @Override
    public void close()
    {
        try
        {
//            connection.commit();
            connection.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
