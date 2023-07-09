/*
 * [Town.java]
 * @author Alan Tang, Jaeyong Lee
 * @version Apr 21, 2022
 */

import java.awt.Point;
import java.util.Objects;

public class Town {
    private final String name;
    private boolean hasFireStation;
    private Point origin;

    public Town(String name, Point origin) {
        this.name = name;
        this.origin = origin;
        setHasFireStation(false);
    }

    public Town(String name) {
        this.name = name;
        setHasFireStation(false);
    }

    public Point getOrigin() {
        return origin;
    }

    public void setOrigin(Point origin) {
        this.origin = origin;
    }

    public boolean hasFireStation() {
        return hasFireStation;
    }

    public void setHasFireStation(boolean hasFireStation) {
        this.hasFireStation = hasFireStation;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Town)) {
            return false;
        }

        Town other = (Town) obj;

        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    @Override
    public String toString() {
        if (hasFireStation) {
            return "FS:" + name;
        } else {
            return "T:" + name;
        }
    }
}
