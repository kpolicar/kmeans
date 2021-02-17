import java.awt.Color;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

public class Waypoint extends DefaultWaypoint
{
    private Color color;

    public Waypoint(Color color, GeoPosition coord)
    {
        super(coord);
        this.color = color;
    }

    public Color getColor()
    {
        return color;
    }
    public Waypoint setColor(Color value)
    {
        color = value;
        return this;
    }

}