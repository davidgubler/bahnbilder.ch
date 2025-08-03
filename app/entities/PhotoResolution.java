package entities;

import play.libs.F;

public class PhotoResolution implements Comparable<PhotoResolution> {
    public enum Size {
        small(200, 150), medium(900, 675), large(1280, 960), xlarge(1600, 1200), xxlarge(2400, 1800), original(0, 0);

        private int maxWidth, maxHeight;

        Size(int maxWidth, int maxHeight) {
            this.maxWidth = maxWidth;
            this.maxHeight = maxHeight;
        }

        public F.Tuple<Integer, Integer> getScaledSize(int originalWidth, int originalHeight) {
            if (this == original) {
                return new F.Tuple(originalWidth, originalHeight);
            }
            int width = originalWidth > maxWidth ? maxWidth : originalWidth;
            int height = width == originalWidth ? originalHeight : Math.round((float)originalHeight * (float)width / (float)originalWidth);

            if (height > maxHeight) {
                // image is height constrained, not width constrained
                height = originalHeight > maxHeight ? maxHeight : originalHeight;
                width = height == originalHeight ? originalWidth : Math.round((float)originalWidth * (float)height / (float)originalHeight);
            }
            return new F.Tuple(width, height);
        }

        public int getMaxWidth() {
            return maxWidth;
        }
    }

    public String getName() {
        return getSize().name();
    }

    private PhotoResolution.Size size;
    private int width;
    private int height;

    public PhotoResolution(PhotoResolution.Size size, int width, int height) {
        this.size = size;
        this.width = width;
        this.height = height;
    }

    public PhotoResolution(PhotoResolution resolution) {
        this.size = resolution.getSize();
        this.width = resolution.getWidth();
        this.height = resolution.getHeight();
    }

    public PhotoResolution.Size getSize() {
        return size;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int compareTo(PhotoResolution other) {
        return Integer.compare(getWidth(), other.getWidth());
    }

    @Override
    public String toString() {
        return getName() + " " + getWidth() + "x" + getHeight();
    }
}
