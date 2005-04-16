package net.sf.wowc.util;

import java.io.*;
import java.util.zip.*;
import java.nio.*;

public class GZIP {
	public static void main(String args[]) {
		System.out.println("GZIP");
	}

	public static byte[] gunzipToBytes(byte []data) throws Exception {
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		GZIPInputStream gis = new GZIPInputStream(bis, data.length);
		ByteBuffer buffer = ByteBuffer.allocate(0);

		byte blah[] = {};
		byte []tmp = new byte[10000];
		int size = 0;
		while ((size = gis.read(tmp, 0, 10000)) > 0) {
			blah = buffer.array();
			buffer = ByteBuffer.allocate(blah.length + size);
			buffer.put(blah, 0, blah.length);
			buffer.put(tmp, 0, size);
		}

		gis.close();
		bis.close();

		return buffer.array();
	}

	public static byte[] gzipToBytes(byte []data) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		GZIPOutputStream gos = new GZIPOutputStream(bos);

		gos.write(data, 0, data.length);
		gos.close();
		bos.close();

		return bos.toByteArray();
	}

	public static void gunzip(String src, String dest) throws Exception {
		try {
			// decompress file
			File in = new File(src);
			FileInputStream fis = new FileInputStream(in);
			ByteBuffer buffer = ByteBuffer.allocate((int)in.length());

			byte []tmp = new byte[10000];
			int size = 0;
			while ((size = fis.read(tmp, 0, 10000)) > 0) {
				buffer.put(tmp, 0, size);
			}
			fis.close();

			byte []compressed = buffer.array();
			//System.out.println("compressed size is '"+compressed.length+"'");
			byte []uncompressed = GZIP.gunzipToBytes(compressed);
			//System.out.println("uncompressed size is '"+uncompressed.length+"'");

			FileOutputStream fos = new FileOutputStream(new File(dest));
			fos.write(uncompressed, 0, uncompressed.length);
			fos.close();
		} catch (Exception e) {
			//e.printStackTrace();
			throw e;
		}
	}

	public static void gzip(String src, String dest) throws Exception {
		try {
			// compress file
			File in = new File(src);
			FileInputStream fis = new FileInputStream(in);
			ByteBuffer buffer = ByteBuffer.allocate((int)in.length());

			byte []tmp = new byte[10000];
			int size = 0;

			while ((size = fis.read(tmp, 0, 10000)) > 0) {
				buffer.put(tmp, 0, size);
			}
			fis.close();

			byte []uncompressed = buffer.array();
			//System.out.println("uncompressed size is '"+uncompressed.length+"'");
			byte []compressed = GZIP.gzipToBytes(uncompressed);
			//System.out.println("compressed size is '"+compressed.length+"'");

			FileOutputStream fos = new FileOutputStream(new File(dest));
			fos.write(compressed, 0, compressed.length);
			fos.close();
		} catch (Exception e) {
			//e.printStackTrace();
			throw e;
		}
	}

	public static byte []gunzipFileToBytes(File in) throws Exception {
		try {
			// decompress file
			FileInputStream fis = new FileInputStream(in);
			ByteBuffer buffer = ByteBuffer.allocate((int)in.length());

			byte []tmp = new byte[10000];
			int size = 0;
			while ((size = fis.read(tmp, 0, 10000)) > 0) {
				buffer.put(tmp, 0, size);
			}
			fis.close();

			byte []compressed = buffer.array();
			//System.out.println("compressed size is '"+compressed.length+"'");
			byte []uncompressed = GZIP.gunzipToBytes(compressed);
			//System.out.println("uncompressed size is '"+uncompressed.length+"'");

			return uncompressed;
		} catch (Exception e) {
			//e.printStackTrace();
			throw e;
		}
	}

	public static byte []gzipFileToBytes(File in) throws Exception {
		try {
			// compress file
			FileInputStream fis = new FileInputStream(in);
			ByteBuffer buffer = ByteBuffer.allocate((int)in.length());

			byte []tmp = new byte[10000];
			int size = 0;

			while ((size = fis.read(tmp, 0, 10000)) > 0) {
				buffer.put(tmp, 0, size);
			}
			fis.close();

			byte []uncompressed = buffer.array();
			//System.out.println("uncompressed size is '"+uncompressed.length+"'");
			byte []compressed = GZIP.gzipToBytes(uncompressed);
			//System.out.println("compressed size is '"+compressed.length+"'");

			return compressed;
		} catch (Exception e) {
			//e.printStackTrace();
			throw e;
		}
	}
}
