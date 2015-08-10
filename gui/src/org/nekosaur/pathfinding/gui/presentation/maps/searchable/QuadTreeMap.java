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
import org.nekosaur.pathfinding.lib.searchspaces.quadtree.QuadTree;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * @author nekosaur
 */
@SuppressWarnings("restriction")
public class QuadTreeMap extends AbstractSearchableMap {

    private final Random rand = new Random();

    private double scaleFactor = 1;

    private Vertex trueStart;
    private Vertex trueGoal;
    
	public QuadTreeMap(double width, double height, MapData data) {
        super(width, height);

        this.searchSpace = QuadTree.create(data, null);
        this.scaleFactor = searchSpace.getHeight() / height;
        double size = width / searchSpace.getWidth();
        this.lineWidth = 1 + size / 10;

        canvas.drawImage(searchSpace.draw((int)width));

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            Node n = searchSpace.getNode((int)(event.getX() * scaleFactor), (int)(event.getY() *scaleFactor));

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

                trueGoal = new Vertex((int)(event.getX()), (int)(event.getY()));
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

                trueStart = new Vertex((int)(event.getX()), (int)(event.getY()));
                update(n);
            }

        });

    }

    @Override
    public void update(Node node) {
        QuadTree.QuadNode qn = ((QuadTree)searchSpace).getQuadNode(node.x, node.y);

        AABB region = qn.getRegion();

        Color cstate = NodeState.color(node.state);
        Color cstatus = NodeStatus.color(node.status);

        Color c = cstate.interpolate(cstatus, 0.5);

        double b = 0.6 + Math.max(rand.nextDouble(), 0.2);
        c = c.deriveColor(0, 1, b, 1);

        if (node.equals(goal.get()))
            c = Color.ORANGERED;
        else if (node.equals(start.get()))
            c = Color.LIMEGREEN;

        canvas.drawRect(region.x / scaleFactor, region.y / scaleFactor, region.width / scaleFactor, region.height / scaleFactor, c);
    }

    @Override
    public void drawPath(List<Vertex> path) {

        LinkedList<Vertex> centeredPath = new LinkedList<>();

        path.forEach(v -> {
            QuadTree.QuadNode qn = ((QuadTree)searchSpace).getQuadNode(v.x, v.y);
            AABB region = qn.getRegion();
            int x = (int)(region.x / scaleFactor) + (int)(region.width / scaleFactor) / 2;
            int y = (int)(region.y / scaleFactor) + (int)(region.height / scaleFactor) / 2;
            centeredPath.add(new Vertex(x, y));
        });

        centeredPath.addFirst(trueGoal);
        centeredPath.addLast(trueStart);

        canvas.drawLine(centeredPath, Color.YELLOW, lineWidth, false);
    }

}
