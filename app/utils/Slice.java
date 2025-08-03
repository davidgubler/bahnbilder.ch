package utils;

public class Slice implements CharSequence {
    private final String string;

    private final int start;

    private final int end;

    public static final Slice EMPTY = new Slice("");

    public Slice(String string) {
        if (string == null) {
            string = "";
        }
        this.string = string;
        start = 0;
        end = string.length();
    }

    private Slice(String string, int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException("start " + start + " is larger than end " + end);
        }
        this.string = string;
        this.start = start;
        this.end = end;
    }

    public Slice sub(int start) {
        if (start == 0) {
            return this;
        }
        return new Slice(string, this.start + start, this.end);
    }

    public Slice sub(int start, int end) {
        if (start == 0 && end == this.end - this.start) {
            return this;
        }
        return new Slice(string, this.start + start, this.start + end);
    }

    public int indexOf(String search) {
        int pos = string.indexOf(search, start);
        if (pos < 0 || pos + search.length() > end) {
            return -1;
        }
        return pos - start;
    }

    /*
    public Pair<Integer, String> firstMatch(String... search) {
        Pair<Integer, String> firstMatch = null;
        for (String s : search) {
            int pos = indexOf(s);
            if (pos >= 0 && (firstMatch == null || pos < firstMatch.getLeft())) {
                firstMatch = Pair.of(pos, s);
            }
        }
        return firstMatch;
    }
    */

    public int indexOf(String search, int startOffset) {
        int pos = string.indexOf(search, start + startOffset);
        if (pos < 0 || pos + search.length() > end) {
            return -1;
        }
        return pos - start;
    }

    public boolean startsWith(String other) {
        if (start + other.length() > end) {
            return false;
        }
        return string.regionMatches(this.start, other, 0, other.length());
    }

    public Slice trim() {
        int start = this.start;
        int end = this.end;
        while (start < end && Character.isWhitespace(string.charAt(start))) {
            start++;
        }
        while (start < end && Character.isWhitespace(string.charAt(end - 1))) {
            end--;
        }
        if (start == this.start && end == this.end) {
            return this;
        }
        return new Slice(string, start, end);
    }

    public Slice trimStartLines() {
        int pos = start;
        while (pos < end) {
            if (!Character.isWhitespace(string.charAt(pos))) {
                return this;
            }
            if (string.charAt(pos) == '\n') {
                return this.sub(pos - start + 1).trimStartLines();
            }
            pos++;
        }
        return this;
    }

    public boolean isEmpty() {
        return start == end;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return sub(start, end);
    }

    public int length() {
        return end - start;
    }

    @Override
    public char charAt(int i) {
        return string.charAt(i + start);
    }

    @Override
    public String toString() {
        return string.substring(start, end);
    }
}
