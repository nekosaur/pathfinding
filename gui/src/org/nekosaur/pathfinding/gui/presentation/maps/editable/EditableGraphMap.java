package org.nekosaur.pathfinding.gui.presentation.maps.editable;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.nekosaur.pathfinding.gui.presentation.maps.AbstractMap;
import org.nekosaur.pathfinding.lib.common.MapData;
import org.nekosaur.pathfinding.lib.common.Point;
import org.nekosaur.pathfinding.lib.node.NodeState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author nekosaur
 */
public class EditableGraphMap extends AbstractMap implements IEditableMap {

    private final List<EditableGraphNode> nodes = new ArrayList<>();
    private final List<EditableGraphEdge> edges = new ArrayList<>();

    public EditableGraphMap(double width, double height, MapData data) {

        this.width = width;
        this.height = height;

        this.setPrefSize(width, height);

        this.setBackground(new Background(new BackgroundFill(Color.CORNSILK, CornerRadii.EMPTY, Insets.EMPTY)));

        final ObjectProperty<EditableGraphNode> startNode = new SimpleObjectProperty<>();
        final ObjectProperty<EditableGraphNode> clickedNode = new SimpleObjectProperty<>();
        final ObjectProperty<Point> delta = new SimpleObjectProperty<>();

        this.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            int x = (int) event.getX();
            int y = (int) event.getY();

            Optional<EditableGraphNode> node = findNode(x, y);

            if (event.isSecondaryButtonDown()) {
                if (node.isPresent()) {
                    startNode.set(node.get());
                    return;
                }

                Optional<EditableGraphEdge> edge = findEdge(x, y);

                if (edge.isPresent()) {
                    removeEdge(edge.get());
                    return;
                }

                addNode(x, y);

            } else {
                if (!node.isPresent())
                    return;

                clickedNode.set(node.get());
                delta.set(new Point((int) (node.get().getCenterX() - x), (int) (node.get().getCenterY() - y)));
            }

        });

        this.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            //System.out.println(clickedNode.get());
            if (clickedNode.isNull().get())
                return;

            EditableGraphNode node = clickedNode.get();

            int x = (int)event.getX();
            int y = (int)event.getY();

            //if (x - NODE_SIZE / 2 < 0 || y - NODE_SIZE / 2 < 0)
                //return;

            Point d = delta.get();

            node.setPosition(new Point(x + d.x, y + d.y));
        });

        this.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            int x = (int)event.getX();
            int y = (int)event.getY();

            if (event.isPopupTrigger()) {
                Optional<EditableGraphNode> end = findNode(x, y);

                if (!end.isPresent())
                    return;

                if (end.get().equals(startNode.get())) {
                    removeNode(end.get());
                    return;
                }

                if (startNode.isNotNull().get()) {
                    addEdge(startNode.get(), end.get());
                }
            }

            clickedNode.set(null);
            startNode.set(null);

        });


    }

    private Optional<EditableGraphNode> findNode(int x, int y) {
        return nodes.stream().filter(node -> node.contains(x, y)).findFirst();
    }

    private Optional<EditableGraphEdge> findEdge(int x, int y) {
        return edges.stream().filter(edge -> edge.contains(x, y)).findFirst();
    }

    private void addNode(int x, int y) {
        EditableGraphNode n = new EditableGraphNode(new Point(x, y), NodeState.EMPTY);
        nodes.add(n);
        this.getChildren().add(n);
        n.toFront();
        System.out.println("Added node at " + x + " " + y);
    }

    private void removeNode(EditableGraphNode node) {
        nodes.remove(node);
        this.getChildren().remove(node);
        List<EditableGraphEdge> orphans = edges.stream().filter(edge -> edge.start.equals(node) || edge.end.equals(node)).collect(Collectors.toList());
        edges.removeAll(orphans);
        this.getChildren().removeAll(orphans);
    }

    private void addEdge(EditableGraphNode start, EditableGraphNode end) {
        System.out.println("Adding edge from " + start.getPosition() + " to " + end.getPosition());
        EditableGraphEdge se = new EditableGraphEdge(start, end);
        EditableGraphEdge es = new EditableGraphEdge(end, start);
        start.addEdge(se);
        end.addEdge(es);
        edges.add(se);
        this.getChildren().add(se);
        se.toBack();
    }

    private void removeEdge(EditableGraphEdge edge) {
        EditableGraphNode start = edge.start;
        EditableGraphNode end = edge.end;

        start.removeEdge(edge);
        end.removeEdge(edge);

        edges.remove(edge);
        this.getChildren().remove(edge);
    }

    @Override
    public MapData getData() {
        return null;
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    private class EditableGraphNode extends Circle {
        private Point position;
        private NodeState state;
        private final List<EditableGraphEdge> edges = new ArrayList<>();

        public EditableGraphNode(Point position, NodeState state) {
            super(position.x, position.y, 50f);

            this.setStroke(Color.DARKGRAY);
            this.setStrokeWidth(10f);

            this.position = position;
            this.state = state;

            draw();
        }

        public List<EditableGraphEdge> getEdges() {
            return edges;
        }

        public void addEdge(EditableGraphEdge edge) {
            edges.add(edge);
        }

        public void removeEdge(EditableGraphEdge edge) {
            edges.remove(edge);
        }

        public Point getPosition() {
            return position;
        }

        public void setPosition(Point position) {
            this.position = position;
            this.setCenterX(position.x);
            this.setCenterY(position.y);
        }

        public NodeState getState() {
            return state;
        }

        public void setState(NodeState state) {
            this.state = state;
        }

        private void draw() {
            switch (state) {
                case EMPTY:
                    this.setFill(Color.WHITESMOKE);
                    break;
                case WALL:
                    this.setFill(Color.DARKGRAY);
                    break;
            }
        }

    }

    private class EditableGraphEdge extends Line {

        private EditableGraphNode start;
        private EditableGraphNode end;

        public EditableGraphEdge(EditableGraphNode start, EditableGraphNode end) {
            super();

            this.start = start;
            this.end = end;

            startXProperty().bind(start.centerXProperty());
            startYProperty().bind(start.centerYProperty());
            endXProperty().bind(end.centerXProperty());
            endYProperty().bind(end.centerYProperty());

            this.setStrokeWidth(10);
            this.setStroke(Color.DARKGRAY);
        }


    }
}
