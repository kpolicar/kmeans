import java.awt.Color;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

/**
 * A waypoint that also has a color and a label
 * @author Martin Steiger
 */
public class MyWaypoint extends DefaultWaypoint
{
    private Color color;

    /**
     * @param color the color
     * @param coord the coordinate
     */
    public MyWaypoint(Color color, GeoPosition coord)
    {
        super(coord);
        this.color = color;
    }

    /**
     * @return the color
     */
    public Color getColor()
    {
        return color;
    }

    public MyWaypoint setColor(Color value)
    {
        color = value;
        return this;
    }

}