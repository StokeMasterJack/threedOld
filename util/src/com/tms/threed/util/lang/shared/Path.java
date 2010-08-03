package com.tms.threed.util.lang.shared;

import java.io.Serializable;

/**
 * Represents a path that may be used on multiple platforms. i.e. file systems (Mac, Windows, Unix) and web URLs
 */
public class Path implements Comparable<Path>, Serializable {

    private String url;

    public Path() {
        assert isValid();
    }

    public Path(String u) {
        url = fixUp(u);
        assert isValid();
    }

    public Path(Path context, Path localPath) {
        this(context == null ? null : context.url, localPath == null ? null : localPath.url);
        assert isValid();
    }

    public Path(Path context, String url) {
        this(context == null ? null : context.url, url);
        assert isValid();
    }

    public Path(String context, String localPath) {
        String u1 = fixUp(context);
        String u2 = fixUp(localPath);

        if (u1 == null && u2 == null) {
            url = null;
        } else if (u1 != null && u2 == null) {
            url = u1;
        } else if (u1 == null && u2 != null) {
            url = u2;
        } else if (u1 != null && u2 != null) {
            url = u1 + "/" + u2;
        } else {
            throw new IllegalStateException();
        }

        assert isValid();
    }


    private void fixUp() {
        this.url = fixUp(this.url);
    }

    private void check() {

    }

    private static void check(String u) {
        if (u == null) return;
        if (Strings.isEmpty(u)) throw new IllegalStateException("Failed isEmpty test for path: [" + u + "]");
        if (u.contains(" ")) throw new IllegalStateException("Failed containsSpace test for path: [" + u + "]");
        if (u.contains("\\")) throw new IllegalStateException("Failed containsBckSlash test for path: [" + u + "]");
        if (u.startsWith("//"))
            throw new IllegalStateException("Failed startsWithDoubleFwdSlash test for path: [" + u + "]");
    }

    private boolean isValid() {
        try {
            check();
            return true;
        } catch (IllegalStateException e) {
//            return false;
            throw e;
        }
    }

    public static String fixUp(String u) {
        if (Strings.isEmpty(u)) {
            return null;
        } else {
            u = " " + u + " ";
            u = u.trim();
            u = convertBackslashesToForward(u);
            u = removeWindowsDriveLetterPrefix(u);
            u = trimSlashes(u);
            check(u);
            return u;
        }
    }

    public static String removeWindowsDriveLetterPrefix(String u) {
        return u.replaceAll("c:", "");
    }

    public static String convertBackslashesToForward(String u) {
        return u.replaceAll("\\\\", "/");
    }

    public static boolean isUrl(String s) {
        if (s.startsWith("http:/")) return true;
        else if (s.startsWith("file:/")) return true;
        else return false;
    }

    public boolean isUrl() {
        return isUrl(url);
    }


    @Override
    public String toString() {
        assert isValid();
        if (url == null) return "";
        if (url.startsWith("http:")) return url;
        if (url.startsWith("file:")) return url;
        return "/" + url;
    }


    public static String trimSlashes(String s) {
        String a = trimLeadingSlash(s);
        String b = trimTrailingSlash(a);
        return b;
    }

    public static String trimTrailingSlash(String s) {
        if (s.endsWith("/")) return s.substring(0, s.length() - 1);
        return s + "";
    }

    public static String trimLeadingSlash(String s) {
        if (s.startsWith("/")) return s.substring(1);
        return s + "";
    }

    public Path copy() {
        return new Path(url);
    }

    public Path append(Path url) {
        return new Path(this, url);
    }

    public Path append(String url) {
        return new Path(this, new Path(url));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Path that = (Path) o;

        assert this.isValid();
        assert that.isValid();
        if (!url.equals(that.url)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

    public int compareTo(Path that) {
        if (that == null) throw new IllegalArgumentException("\"o\" is required");
        return this.url.compareTo(that.url);
    }

    public Path leftTrim(Path path) {
        String leftString = path.toString();
        String thisString = toString();
        return new Path(thisString.substring(leftString.length()));
    }

    public boolean endsWith(String s) {
        if (url == null) return false;
        return url.endsWith(s);
    }
}
