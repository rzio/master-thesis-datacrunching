package org.mediawiki.importer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author alex
 * @since 4/22/12 9:41 AM
 */

public class NullDumpWriter implements DumpWriter
{

    protected OutputStream stream;
    protected XmlWriter writer;
    protected StringWriter currentContent;
    protected String currentPageTitle;

    protected static final String version = "0.3";
    protected static final String ns = "http://www.mediawiki.org/xml/export-" + version + "/";
    protected static final String schema = "http://www.mediawiki.org/xml/export-" + version + ".xsd";
    protected static final DateFormat dateFormat = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'");

    private long count = 0;
    private long totalBytes = 0;
    private final Charset utf8 = Charset.forName("UTF8");

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

    }

    public void writeSiteinfo(Siteinfo info) throws IOException
    {

    }

    public void writeStartPage(Page page) throws IOException
    {
        currentContent = new StringWriter();
        writer = new XmlWriter(new OutputStream()
        {
            @Override
            public void write(int i) throws IOException
            {
                currentContent.write(i);
            }
        });
        writer.openElement("page");
        writer.textElement("title", page.Title.toString());
        currentPageTitle = page.Title.toString();
        if (page.Id != 0)
            writer.textElement("id", Integer.toString(page.Id));
        if (page.Restrictions != null && page.Restrictions.length() != 0)
            writer.textElement("restrictions", page.Restrictions);
    }

    public void writeEndPage() throws IOException
    {
        writer.closeElement();
        writer.close();
        currentContent.close();

        count++;
        totalBytes += currentContent.toString().getBytes(utf8).length;

        if (count % 100 == 0)
            System.out.println(String.format("%,d: %,d bytes %s", count, totalBytes, currentPageTitle));

    }


    public void writeRevision(Revision rev) throws IOException
    {
        XmlWriter writer = this.writer;
        writer.openElement("revision");
        if (rev.Id != 0)
            writer.textElement("id", Integer.toString(rev.Id));

        writer.textElement("timestamp", formatTimestamp(rev.Timestamp));

        writeContributor(rev.Contributor);

        if (rev.Minor)
        {
            writer.emptyElement("minor");
        }

        if (rev.Comment == null)
        {
            writer.emptyElement("comment", deletedAttrib);
        }
        else if (rev.Comment.length() != 0)
        {
            writer.textElement("comment", rev.Comment);
        }

        writer.textElement("text", rev.Text,
                rev.Text == null ? new String[][]{{"xml:space", "preserve"}, {"deleted", "deleted"}}
                        : new String[][]{{"xml:space", "preserve"}}
        );

        writer.closeElement();
    }

    static final String[][] deletedAttrib = new String[][]{{"deleted", "deleted"}};

    static String formatTimestamp(Calendar ts)
    {
        return dateFormat.format(ts.getTime());
    }

    void writeContributor(Contributor contrib) throws IOException
    {
        XmlWriter writer = this.writer;

        if (contrib.Username == null)
        {
            writer.emptyElement("contributor", deletedAttrib);
        }
        else
        {
            writer.openElement("contributor");
            if (contrib.isIP)
            {
                writer.textElement("ip", contrib.Username);
            }
            else
            {
                writer.textElement("username", contrib.Username);
                writer.textElement("id", Integer.toString(contrib.Id));
            }
            writer.closeElement();
        }
    }
//    private long pageCount = 0;
//    String currentPageTitle ;
//
//    @Override
//    public void close() throws IOException
//    {
//        //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    @Override
//    public void writeStartWiki() throws IOException
//    {
//        //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    @Override
//    public void writeEndWiki() throws IOException
//    {
//        //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    @Override
//    public void writeSiteinfo(Siteinfo info) throws IOException
//    {
//        //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    @Override
//    public void writeStartPage(Page page) throws IOException
//    {
//            currentPageTitle = page.Title.toString();
//    }
//
//    @Override
//    public void writeEndPage() throws IOException
//    {
//        pageCount++;
//        if (pageCount%100 == 0)
//            System.out.println(String.format("%d: %s", pageCount, currentPageTitle));
//    }
//
//    @Override
//    public void writeRevision(Revision revision) throws IOException
//    {
//        //To change body of implemented methods use File | Settings | File Templates.
//    }
}
