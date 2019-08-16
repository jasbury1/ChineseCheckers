

package others;

import java.util.Objects;

/**
 * Represents a width and height value.  May be used as a Map key.
 */

public final class Size {

    /**
     * The width
     */
    public final int width;

    /**
     * The height
     */
    public final int height;

    /**
     * Create a new, immutable Size object
     *
     * @param width     The width
     * @param height    The height
     */
    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Give a hash code value based on the width and height.
     *
     * @return the hash code
     */
    public int hashCode() {
        return (31 + width * 31 + height) * 31;
        // What java.util.Arrays ends up doing, without all the autoboxing
    }

    /**
     * Determine if two size values are equivalent.
     *
     * @param other     The object to compare against.
     * @return true if other is a Size object with the same height and width.
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof Size) {
            Size so = (Size) other;
            return so.width == width && so.height == height;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
	return getClass().getName() + "(" + width + ", " + height + ")";
    }
}
