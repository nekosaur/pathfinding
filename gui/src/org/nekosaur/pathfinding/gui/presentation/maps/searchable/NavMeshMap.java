package org.nekosaur.pathfinding.gui.presentation.maps.searchable;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.nekosaur.pathfinding.gui.presentation.maps.AbstractSearchableMap;
import org.nekosaur.pathfinding.lib.common.AABB;
import org.nekosaur.pathfinding.lib.common.MapData;
import org.nekosaur.pathfinding.lib.common.Vertex;
import org.nekosaur.pathfinding.lib.node.Node;
import org.nekosaur.pathfinding.lib.node.NodeState;
import org.nekosaur.pathfinding.lib.node.NodeStatus;
import org.nekosaur.pathfinding.lib.searchspaces.navmesh.NavMesh;
import org.nekosaur.pathfinding.lib.searchspaces.quadtree.QuadTree;
import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author nekosaur
 */
public class NavMeshMap extends AbstractSearchableMap {

    private double scaleFactor = 1f;
    private Random rand = new Random();

    public NavMeshMap(double width, double height, MapData data) {
        super(width, height);

        this.searchSpace = NavMesh.create(data, null);
        this.scaleFactor = searchSpace.getHeight() / height;
        double size = width / searchSpace.getWidth();
        this.lineWidth = 1 + size / 10;

        this.canvas.drawImage(searchSpace.draw((int)width));

        this.canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            System.out.println("Clicked on point [x=" + event.getX() + ", y=" + event.getY() + "]");
            System.out.println("Searching for point [x=" + (int)(event.getX()*scaleFactor) + ", y=" + (int)(event.getY()*scaleFactor) + "]");
            Node n = searchSpace.getNode(event.getX() * scaleFactor, event.getY() * scaleFactor);

            System.out.println("Clicked on " + n);

            if (n.state == NodeState.WALL)
                return;

            if (event.isPopupTrigger()) {
                if (n.equals(start.get()))
                    return;

                if (goal.isNotNull().get()) {
                    Node old = searchSpace.getNode(goal.get().x, goal.get().y);
                    goal.set(n);
                    update(old);
                } else {
                    goal.set(n);
                }

                //trueGoal = new Vertex((int)(event.getX()), (int)(event.getY()));
                update(n);
            } else {
                if (n.equals(goal.get()))
                    return;

                if (start.isNotNull().get()) {
                    Node old = searchSpace.getNode(start.get().x, start.get().y);
                    start.set(n);
                    update(old);
                } else {
                    start.set(n);
                }

                //trueStart = new Vertex((int)(event.getX()), (int)(event.getY()));
                update(n);
            }

        });

    }

    @Override
    public void update(Node node) {
        DelaunayTriangle triangle = ((NavMesh)searchSpace).getTriangle(node);

        Color cstate = NodeState.color(node.state);
        Color cstatus = NodeStatus.color(node.status);

        Color c = cstate.interpolate(cstatus, 0.5);

        double b = 0.7;
        //c = c.deriveColor(0, 1, b, 1);

        c = Color.WHITESMOKE;

        if (node.equals(goal.get()))
            c = Color.ORANGERED;
        else if (node.equals(start.get()))
            c = Color.LIMEGREEN;

        Vertex[] points = new Vertex[3];

        double[] xPoints = new double[3];
        double[] yPoints = new double[3];
        for (int i = 0; i < triangle.points.length; i++) {
            xPoints[i] = triangle.points[i].getX() / scaleFactor;
            yPoints[i] = triangle.points[i].getY() / scaleFactor;
        }

        canvas.drawTriangle(xPoints, yPoints, c);

    }

    @Override
    public void drawPath(List<Vertex> path) {

        List<Vertex> scaledPath = new ArrayList<>();

        path.forEach(v -> {
            DelaunayTriangle dt = ((NavMesh)searchSpace).getTriangle(v.x, v.y);
            scaledPath.add(new Vertex((int)(dt.centroid().getX() / scaleFactor), (int)(dt.centroid().getY() / scaleFactor)));
        });

        canvas.drawLine(scaledPath, Color.YELLOW, lineWidth, false);
    }
}
