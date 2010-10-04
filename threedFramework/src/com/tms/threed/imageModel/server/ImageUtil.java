package com.tms.threed.imageModel.server;

import com.google.common.io.Files;
import com.tms.threed.imageModel.shared.ImPng;
import com.tms.threed.imageModel.shared.Jpg;
import com.tms.threed.util.lang.server.StringUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ImageUtil {

//    private static final int IMAGE_WIDTH = 599;
//    private static final int IMAGE_HEIGHT = 366;

    public static boolean isEmpty(File imageFile) {
        if (imageFile == null) throw new IllegalArgumentException();
        try {
            BufferedImage image = readImage(imageFile);
            int h = image.getHeight();
            int w = image.getWidth();
            final Raster raster = image.getTile(0, 0);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int[] pixelData = new int[4];
                    raster.getPixel(x, y, pixelData);
                    final Pixel p = new Pixel(x, y, pixelData);
//                    p.print();
                    if (p.hasContent()) return false;
                }
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException(imageFile.toString(), e);
        }
    }

    public static class Pixel {

        int x;
        int y;

        int r;
        int g;
        int b;
        int a;

        public Pixel(int x, int y, int[] pixel) {
            this.x = x;
            this.y = y;
            r = pixel[0];
            g = pixel[1];
            b = pixel[2];
            a = pixel[3];
        }

        public void print() {
            System.out.println("[" + x + "," + y + "]  = (" + r + "," + g + "," + b + ") - alpha: " + a);
        }


        public boolean threeZeros() {
            return r == 0 && g == 0 && b == 0;
        }

        public void printAlphaIfNonZero() {
            if (a != 0) System.out.println("a: " + a);
        }

        public boolean hasContent() {
            return r != 0 || g != 0 || b != 0 || a != 0;
        }
    }


    private static BufferedImage readImage(File file) {
        if (file == null) throw new IllegalArgumentException("file must be non-null");
        try {
            BufferedImage image = ImageIO.read(file);
            if (image == null) throw new IllegalStateException("Why is image null?");
            return image;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getSHAFingerPrint(Jpg jpg) throws NoSuchAlgorithmException {
        StringBuilder cattedPngs = new StringBuilder();
        String fp = null;

        for (ImPng png : jpg.getPngs()) {
            cattedPngs.append(png.getPath().toString()).append("|");
        }

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(cattedPngs.toString().getBytes());
        md.update(jpg.getPath().toString().getBytes());
        byte[] hashedBytes = md.digest();

        fp = StringUtil.byteArray2Hex(hashedBytes).replaceAll(" ", "");
        return fp;
    }

    public static String getFingerprint(File imageFile) throws IOException {
        byte[] digest;
        try {
            digest = Files.getDigest(imageFile, MessageDigest.getInstance("MD5"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return toBase62(digest);
    }

    /**
     * Convert a byte array to base64 string
     */
    public static String toBase64(byte[] byteArray) {
        return new sun.misc.BASE64Encoder().encode(byteArray);
    }


     /**
     * Convert a byte array to base62 string
     */
    public static String toBase62(byte[] byteArray) {
         String base64 = toBase64(byteArray);
         return toBase62(base64);
    }

    /**
	 * Takes a base64 encoded string and eliminates the '+' and '/'.
	 * Also eliminates any CRs.
	 *
	 * Having tokens that are a seamless string of letters and numbers
	 * means that MUAs are less likely to linebreak a long token.
	 */
	protected static String toBase62(String base64)
	{
		StringBuffer buf = new StringBuffer(base64.length() * 2);

		for (int i=0; i<base64.length(); i++)
		{
			char ch = base64.charAt(i);
			switch (ch)
			{
				case 'i':
					buf.append("ii");
					break;

				case '+':
					buf.append("ip");
					break;

				case '/':
					buf.append("is");
					break;

				case '=':
					buf.append("ie");
					break;

				case '\n':
					// Strip out
					break;

				default:
					buf.append(ch);
			}
		}


		return buf.toString();
	}


}