package org.nekosaur.pathfinding.gui.presentation.maps;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Rotate;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.nekosaur.pathfinding.lib.common.Vertex;

import java.util.List;

/**
 * @author nekosaur
 */
@SuppressWarnings("restriction")
public class MapCanvas extends Canvas {

    
	private final GraphicsContext gc;

    public MapCanvas(double width, double height) {
        super(width, height);

        this.gc = this.getGraphicsContext2D();

    }

    public void drawImage(Image image) {
        //gc.drawImage(image, 0, 0, this.getWidth(), this.getHeight());
        gc.drawImage(image, 0, 0);
    }

    public void drawRect(double x, double y, double width, double height, Color color) {
        //gc.save();
        gc.setFill(color);
        gc.fillRect(snap(x), snap(y), snap(width), snap(height));
        //gc.restore();
    }

    public void drawLine(Vertex start, Vertex end, Paint color, double width, boolean dashed) {
        gc.setStroke(color);
        gc.setLineWidth(width);

        if (dashed)
            gc.setLineDashes(0.5f);

        gc.strokeLine(snap(start.x), snap(start.y), snap(end.x), snap(end.y));
    }

    public void drawLine(List<Vertex> path, Paint color, double width, boolean dashed) {
        for (int i = 1; i < path.size(); i++) {
            Vertex start = path.get(i - 1);
            Vertex goal = path.get(i);
            drawLine(start, goal, color, width, dashed);
        }
    }
    
    public void drawTriangle(Vertex position, double length, double angle, Color color) {
    	gc.save();
    	
    	double[] center = new double[] {position.x, position.y};
    	
    	Rotate rotate = new Rotate(angle, center[0], center[1]);
    	gc.transform(rotate.getMxx(), rotate.getMyx(), rotate.getMxy(), rotate.getMyy(), rotate.getTx(), rotate.getTy());
    	
    	gc.setFill(color);
    	
    	double[] xPoints = new double[] {center[0], center[0] + length/2, center[0] - length/2};
    	double[] yPoints = new double[] {center[1] - length /2, center[1] + length/2, center[1] + length/2};
    	
    	gc.fillPolygon(xPoints, yPoints, 3);
    	
    	gc.restore();
    }

    private double snap(double v) {
        return snap(v, 0f);
    }

    private double snap(double v, double snap) {
        return ((int) v) + snap;
    }


}
