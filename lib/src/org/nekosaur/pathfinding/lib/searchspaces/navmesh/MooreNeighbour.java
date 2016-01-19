package org.nekosaur.pathfinding.lib.searchspaces.navmesh;

import org.nekosaur.pathfinding.lib.common.Point;

import java.util.*;

/**
 * @author nekosaur
 */
public class MooreNeighbour {

    private static final Point[] mooreNeighbours = new Point[] { new Point(-1, -1), new Point(0, -1), new Point(1, -1), new Point(1, 0), new Point(1, 1), new Point(0, 1), new Point(-1, 1), new Point(-1, 0)};
    private static final Set<Point> contourVertices = new HashSet<>();

    public static Point getFirstVertex(int[][] data, Queue<Point> backtrack) {
        Point previous = null;
        for (int x = 0; x < data[0].length; x++) {
            for (int y = data.length - 1; y > 0; y--) {

                if (data[y][x] > 0 && data[y+1][x] <= 0 && !contourVertices.contains(new Point(x, y))) {
                    contourVertices.add(new Point(x, y));
                    System.out.println("Found first vertex " + x + " " + y);
                    backtrack.add(previous);
                    return new Point(x, y);
                } else {
                    previous = new Point(x, y);
                }
            }
        }
        return null;
    }

    public static List<Point> trace(int[][] data) {
        Set<Point> B = new LinkedHashSet<>();
        Queue<Point> backtrack = new ArrayDeque<>();
        Point s = MooreNeighbour.getFirstVertex(data, backtrack);

        if (s == null)
            return null;

        B.add(s);
        Point p = s;
        Iterator<Point> it = MooreNeighbour.getIterator(p, backtrack.peek());
        Point b = backtrack.peek();
        Point c = it.next();

        while (MooreNeighbour.isNotFinished(s, backtrack.peek(), c, b, p)) {
            if (data[(int)c.y][(int)c.x] > 0) {
                contourVertices.add(c);
                B.add(c);
                b = p;
                p = c;
                it = MooreNeighbour.getIterator(p, b);
                c = it.next();
            } else {
                b = c;
                c = it.next();
            }
        }

        /*
        List<Vertex> vertices = new LinkedList<>();
        for (Vertex v : B) {
            vertices.add(new Vertex(v.x + 1, v.y + 1));
        }
        vertices.add(vertices.get(0));
        */
        List<Point> vertices = new LinkedList<>(B);
        //vertices.add(s);


        return vertices;
    }

    private static boolean isNotFinished(Point start, Point startBacktrack, Point current, Point currentBacktrack, Point p) {
        return !((start.equals(current) && startBacktrack.equals(currentBacktrack)) || current.equals(startBacktrack));
    }

    public static Iterator<Point> getIterator(Point boundaryPoint, Point originPoint) {
        double dx = originPoint.x - boundaryPoint.x;
        double dy = originPoint.y - boundaryPoint.y;
        Point startNeighbour = new Point(dx, dy);

        return new NeighbourIterator(boundaryPoint, startNeighbour);
    }

    private static class NeighbourIterator implements Iterator<Point> {

        Queue<Point> neighbours = new ArrayDeque<>();

        public NeighbourIterator(Point boundaryPoint, Point startNeighbour) {
            for (int i = 0; i < mooreNeighbours.length; i++) {
                if (mooreNeighbours[i].equals(startNeighbour)) {
                    i = (i + 1) % mooreNeighbours.length;
                    while (neighbours.size() < 8) {
                        neighbours.add(new Point(boundaryPoint.x + mooreNeighbours[i].x, boundaryPoint.y + mooreNeighbours[i].y));
                        i = (i + 1) % mooreNeighbours.length;
                    }
                    break;
                }
            }
        }

        @Override
        public boolean hasNext() {
            return neighbours.size() > 0;
        }

        @Override
        public Point next() {
            return neighbours.remove();
        }
    }

}
