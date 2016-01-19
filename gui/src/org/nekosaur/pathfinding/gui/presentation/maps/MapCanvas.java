package org.nekosaur.pathfinding.gui.presentation.maps;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.nekosaur.pathfinding.lib.common.Point;

import java.util.List;

/**
 * @author nekosaur
 */
@SuppressWarnings("restriction")
public class MapCanvas extends Pane {

    private final Canvas bottom;
    private final Canvas top;
	private final GraphicsContext gcBottom;
    private final GraphicsContext gcTop;

    private final double width;
    private final double height;

    public MapCanvas(double width, double height) {
        //super(width, height);
        this.width = width;
        this.height = height;

        //this.gc = this.getGraphicsContext2D();
        bottom = new Canvas(width, height);
        top = new Canvas(width, height);
        gcBottom = bottom.getGraphicsContext2D();
        gcTop = top.getGraphicsContext2D();

        this.getChildren().addAll(bottom, top);

    }

    public void reset() {
        gcTop.clearRect(0, 0, width, height);
        gcBottom.clearRect(0, 0, width, height);
    }

    public void drawImage(Image image) {
        //gc.drawImage(image, 0, 0, this.getWidth(), this.getHeight());
        gcBottom.drawImage(image, 0, 0);
    }

    public void drawRect(double x, double y, double width, double height, Color color) {
        //gc.save();
        gcBottom.setFill(color);
        gcBottom.fillRect(snap(x), snap(y), snap(width), snap(height));
        //gc.restore();
    }

    public void drawLine(Point start, Point end, Paint color, double width, boolean dashed) {
        gcTop.setStroke(color);
        gcTop.setLineWidth(width);

        if (dashed) {
            gcTop.setLineDashes(20f);
        }

        gcTop.strokePolyline(new double[] {start.x, end.x}, new double[] {start.y, end.y}, 2);
        //gc.strokeLine(snap(start.x), snap(start.y), snap(end.x), snap(end.y));
    }

    public void drawLine(List<Point> path, Paint color, double width, boolean dashed) {
        for (int i = 1; i < path.size(); i++) {
            Point start = path.get(i - 1);
            Point goal = path.get(i);
            drawLine(start, goal, color, width, dashed);
        }
    }
    
    public void drawTriangle(Point position, double length, double angle, Color color) {
        gcTop.save();
    	
    	double[] center = new double[] {position.x, position.y};
    	
    	Rotate rotate = new Rotate(angle, center[0], center[1]);
        gcTop.transform(rotate.getMxx(), rotate.getMyx(), rotate.getMxy(), rotate.getMyy(), rotate.getTx(), rotate.getTy());

        gcTop.setFill(color);
    	
    	double[] xPoints = new double[] {center[0], center[0] + length/2, center[0] - length/2};
    	double[] yPoints = new double[] {center[1] - (length / 3) * 2, center[1] + (length/3), center[1] + length/3};

        gcTop.fillPolygon(xPoints, yPoints, 3);

        gcTop.restore();
    }

    public void drawTriangle(double[] xPoints, double[] yPoints, Color color) {
        gcTop.save();
        gcTop.setFill(color);

        gcTop.fillPolygon(xPoints, yPoints, 3);
        gcTop.setStroke(Color.CYAN);
        gcTop.setLineWidth(2);
        gcTop.strokeLine(xPoints[0], yPoints[0], xPoints[1], yPoints[1]);
        gcTop.strokeLine(xPoints[1], yPoints[1], xPoints[2], yPoints[2]);
        gcTop.strokeLine(xPoints[2], yPoints[2], xPoints[0], yPoints[0]);

        gcTop.restore();
    }

    private double snap(double v) {
        return snap(v, 0f);
    }

    private double snap(double v, double snap) {
        return ((int) v) + snap;
    }


}
