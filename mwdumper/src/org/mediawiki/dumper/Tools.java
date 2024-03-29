package org.mediawiki.dumper;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

import java.io.*;
import java.util.zip.GZIPInputStream;

public class Tools {
	static final int IN_BUF_SZ = 1024 * 1024;
	private static final int OUT_BUF_SZ = 1024 * 1024;

	public static InputStream openInputFile(String arg) throws IOException {
		if (arg.equals("-"))
			return openStandardInput();
		InputStream infile = new BufferedInputStream(new FileInputStream(arg), IN_BUF_SZ);
		if (arg.endsWith(".gz"))
			return new GZIPInputStream(infile);
		else if (arg.endsWith(".bz2"))
			return openBZip2Stream(infile);
		else
			return infile;
	}
	
	static InputStream openStandardInput() throws IOException {
		return new BufferedInputStream(System.in, IN_BUF_SZ);
	}

	static InputStream openBZip2Stream(InputStream infile) throws IOException {

//        int first = infile.read();
//		int second = infile.read();
//		if (first != 'B' || second != 'Z')
//			throw new IOException("Didn't find BZ file signature in .bz2 file");

		return new BZip2CompressorInputStream(infile);
	}

	static OutputStream openStandardOutput() {
		return new BufferedOutputStream(System.out, OUT_BUF_SZ);
	}

	static OutputStream createBZip2File(String param) throws IOException, FileNotFoundException {
		OutputStream outfile = createOutputFile(param);
		// bzip2 expects a two-byte 'BZ' signature header
		outfile.write('B');
		outfile.write('Z');
		return new BZip2CompressorOutputStream(outfile);
	}

	static OutputStream createOutputFile(String param) throws IOException, FileNotFoundException {
		File file = new File(param);
		file.createNewFile();
		return new BufferedOutputStream(new FileOutputStream(file), OUT_BUF_SZ);
	}
	
	
	// ----------------
	
}
