package org.nekosaur.pathfinding.lib.searchspaces.navmesh;

import org.nekosaur.pathfinding.lib.common.Vertex;

import java.util.*;

/**
 * @author nekosaur
 */
public class MooreNeighbour {

    private static final Vertex[] mooreNeighbours = new Vertex[] { new Vertex(-1, -1), new Vertex(0, -1), new Vertex(1, -1), new Vertex(1, 0), new Vertex(1, 1), new Vertex(0, 1), new Vertex(-1, 1), new Vertex(-1, 0)};
    private static final Set<Vertex> contourVertices = new HashSet<>();

    public static Vertex getFirstVertex(int[][] data, Queue<Vertex> backtrack) {
        Vertex previous = null;
        for (int x = 0; x < data[0].length; x++) {
            for (int y = data.length - 1; y > 0; y--) {
                if (data[y][x] > 0 && !contourVertices.contains(new Vertex(x, y))) {
                    contourVertices.add(new Vertex(x, y));
                    System.out.println("Found first vertex " + x + " " + y);
                    backtrack.add(previous);
                    return new Vertex(x, y);
                } else {
                    previous = new Vertex(x, y);
                }
            }
        }
        return null;
    }

    public static List<Vertex> trace(int[][] data) {
        Set<Vertex> B = new LinkedHashSet<>();
        Queue<Vertex> backtrack = new ArrayDeque<>();
        Vertex s = MooreNeighbour.getFirstVertex(data, backtrack);

        if (s == null)
            return null;

        B.add(s);
        Vertex p = s;
        Iterator<Vertex> it = MooreNeighbour.getIterator(p, backtrack.peek());
        Vertex b = backtrack.peek();
        Vertex c = it.next();

        while (MooreNeighbour.isNotFinished(s, backtrack.peek(), c, b)) {
            if (data[c.y][c.x] > 0) {
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
        List<Vertex> vertices = new LinkedList<>(B);
        //vertices.add(s);


        return vertices;
    }

    private static boolean isNotFinished(Vertex start, Vertex startBacktrack, Vertex current, Vertex currentBacktrack) {
        return !(start.equals(current) && startBacktrack.equals(currentBacktrack));
    }

    public static Iterator<Vertex> getIterator(Vertex boundaryPoint, Vertex originPoint) {
        int dx = originPoint.x - boundaryPoint.x;
        int dy = originPoint.y - boundaryPoint.y;
        Vertex startNeighbour = new Vertex(dx, dy);

        return new NeighbourIterator(boundaryPoint, startNeighbour);
    }

    private static class NeighbourIterator implements Iterator<Vertex> {

        Queue<Vertex> neighbours = new ArrayDeque<>();

        public NeighbourIterator(Vertex boundaryPoint, Vertex startNeighbour) {
            for (int i = 0; i < mooreNeighbours.length; i++) {
                if (mooreNeighbours[i].equals(startNeighbour)) {
                    i = (i + 1) % mooreNeighbours.length;
                    while (neighbours.size() < 8) {
                        neighbours.add(new Vertex(boundaryPoint.x + mooreNeighbours[i].x, boundaryPoint.y + mooreNeighbours[i].y));
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
        public Vertex next() {
            return neighbours.remove();
        }
    }

}
