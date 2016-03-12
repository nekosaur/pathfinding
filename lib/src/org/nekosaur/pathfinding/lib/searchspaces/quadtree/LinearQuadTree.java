package org.nekosaur.pathfinding.lib.searchspaces.quadtree;

import org.nekosaur.pathfinding.lib.common.AABB;
import org.nekosaur.pathfinding.lib.common.MapData;

import java.util.*;

/**
 * @author nekosaur
 */
public class LinearQuadTree {

    private enum QuadrantStatus {
        MIXED, EMPTY, FULL,
    };

    private enum QuadrantDirection {
        E, NE, N, NW, W, SW, S, SE;
    }

    private final int EAST_NEIGHBOUR = 0x01;
    private final int NORTH_EAST_NEIGHBOUR = 0x03;
    private final int NORTH_NEIGHBOUR = 0x02;
    private final int NORTH_WEST_NEIGHBOUR;
    private final int WEST_NEIGHBOUR;
    private final int SOUTH_WEST_NEIGHBOUR;
    private final int SOUTH_NEIGHBOUR;
    private final int SOUTH_EAST_NEIGHBOUR;

    //private final int[] NEIGHBOURS;
    private final Map<QuadrantDirection, Integer> NEIGHBOURS = new HashMap<>();

    private final int TX;
    private final int TY;

    private final int R;

    private PriorityQueue<Quadrant> qtlcld;
    private Quadrant[] lookup;

    private int size;

    public LinearQuadTree(MapData mapData) {

        size = mapData.width;
        R = (int)(Math.log(size) / Math.log(2));

        StringBuilder tx = new StringBuilder();
        StringBuilder ty = new StringBuilder();
        StringBuilder ff = new StringBuilder();
        for (int i = 0; i < R; i++) {
            tx.append("01");
            ty.append("10");
            ff.append("11");
        }

        TX = Integer.parseUnsignedInt(tx.toString(), 2);
        TY = Integer.parseUnsignedInt(ty.toString(), 2);

        NORTH_WEST_NEIGHBOUR = TX + 2;
        WEST_NEIGHBOUR = TX;
        SOUTH_WEST_NEIGHBOUR = Integer.parseUnsignedInt(ff.toString(), 2);
        SOUTH_NEIGHBOUR = TY;
        SOUTH_EAST_NEIGHBOUR = TY + 1;

        /*
        NEIGHBOURS = new int[] {
                EAST_NEIGHBOUR,
                NORTH_EAST_NEIGHBOUR,
                NORTH_NEIGHBOUR,
                NORTH_WEST_NEIGHBOUR,
                WEST_NEIGHBOUR,
                SOUTH_WEST_NEIGHBOUR,
                SOUTH_NEIGHBOUR,
                SOUTH_EAST_NEIGHBOUR
        };*/

        NEIGHBOURS.put(QuadrantDirection.E, EAST_NEIGHBOUR);
        NEIGHBOURS.put(QuadrantDirection.NE, NORTH_EAST_NEIGHBOUR);
        NEIGHBOURS.put(QuadrantDirection.N, NORTH_NEIGHBOUR);
        NEIGHBOURS.put(QuadrantDirection.NW, NORTH_WEST_NEIGHBOUR);
        NEIGHBOURS.put(QuadrantDirection.W, WEST_NEIGHBOUR);
        NEIGHBOURS.put(QuadrantDirection.SW, SOUTH_WEST_NEIGHBOUR);
        NEIGHBOURS.put(QuadrantDirection.S, SOUTH_NEIGHBOUR);
        NEIGHBOURS.put(QuadrantDirection.SE, SOUTH_EAST_NEIGHBOUR);

        Comparator<Quadrant> c = (q1, q2) -> {
            return q1.status.ordinal() - q2.status.ordinal();
        };
        qtlcld = new PriorityQueue<Quadrant>(c);
        lookup = new Quadrant[(int)Math.pow(2, 2*R)];

        qtlcld.add(new Quadrant(0, 0, QuadrantStatus.MIXED, new int[] {0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF}, new AABB(0, 0, size, size)));

        while (qtlcld.peek().status == QuadrantStatus.MIXED) {
            Quadrant q = qtlcld.remove();
            for (Quadrant neighbour : findNeighbours(q)) {
                adjustLevelDifference(q, neighbour, 1);
            }

            double cx = q.region.x + (size/2), cy = q.region.y + (size/2);
            double nsz = q.region.width / 2;

            List<Quadrant> children = new LinkedList<>();

            // SW
            children.add(new Quadrant(q.position, q.level+1, new AABB(0, cy, nsz, nsz), mapData.data));
            // SE
            children.add(new Quadrant(q.position, q.level+1, new AABB(0, cy, nsz, nsz), mapData.data));
            // NW
            children.add(new Quadrant(q.position, q.level+1, new AABB(0, cy, nsz, nsz), mapData.data));
            // NE
            children.add(new Quadrant(q.position, q.level+1, new AABB(0, cy, nsz, nsz), mapData.data));

            qtlcld.addAll(children);

            for (Quadrant child : children) {
                for (Quadrant neighbour : findNeighbours(child)) {
                    if (!children.contains(neighbour))
                        adjustLevelDifference(child, neighbour, 1);
                }
            }

        }
        /*
        while (qtlcld includes GREY areas) {
            for (each equal size neighbour of first GREY area) {
                Corresponding level differences of neighbours added by 1;
            }
            First GREY quadrant is replaced by its four children;
            (ADD CHILDREN TO LOOKUP TABLE);
            Newly introduced four children are added to the head of queue qltlcd;
            for (each equal size neighbour of child (other than their brothers)) {
                Corresponding level differences are added by 1;
            }
        }
        */

    }

    private List<Quadrant> findNeighbours(Quadrant q) {
        List<Quadrant> neighbours = new LinkedList<>();

        for (Map.Entry<QuadrantDirection, Integer> entry : NEIGHBOURS.entrySet()) {
            Quadrant n = findNeighbour(q, entry.getKey());

            if (n != null)
                neighbours.add(n);
        }

        /*
        int nq = q.position, dn, nl;
        for (int i = 0; i < NEIGHBOURS.length; i++) {
            dn = NEIGHBOURS[i];
            nl = quadrantLocationAdd(nq, dn);

            if (lookup[nl] != null)
                neighbours.add(lookup[nl]);
        }*/

        return neighbours;
    }

    private Quadrant findNeighbour(Quadrant q, QuadrantDirection d) {
        int dd = q.levelDifference(d);

        if (dd != 0xFF) {
            int nq = q.position, l = q.level, shift = 2*(R - l - dd), mq;
            if (dd < 0) {
                mq = quadrantLocationAdd((nq >> shift) << shift, NEIGHBOURS.get(d) << shift);
            } else {
                mq = quadrantLocationAdd(nq, NEIGHBOURS.get(d) << 2*(R - l));
            }

            return lookup[mq];

        } else {
            // no neighbour in direction d;
            return null;
        }
    }

    private void adjustLevelDifference(Quadrant q, Quadrant n, int diff) {

    }

    /**
     *
     * @param nq Quadrant location code
     * @param dn Quadrant neighbour direction increment
     * @return Quadrant location of neighbour
     */
    private int quadrantLocationAdd(int nq, int dn) {
        return (((nq | TY) + (dn & TX)) & TX) | (((nq | TX) + (dn & TY)) & TY);
    }

    class Quadrant {

        AABB region;
        int position;
        int level;
        int[] levelDifferences = new int[8];
        Quadrant parent;
        QuadrantStatus status;

        public Quadrant(int position, int level, AABB region, byte[] data) {
            this.position = position;
            this.level = level;

            this.status = QuadrantStatus.EMPTY;
            if (region.width <= 1 && data[(int)(region.y * LinearQuadTree.this.size * region.x)] > 0) {
                this.status = QuadrantStatus.FULL;
            } else {
                for (int y = (int) region.y; y < region.y + region.width; y++) {
                    for (int x = (int) region.x; x < region.x + region.height; x++) {
                        if (data[y * LinearQuadTree.this.size + x] > 0) {
                            this.status = QuadrantStatus.MIXED;
                            break;
                        }
                    }
                    if (this.status == QuadrantStatus.MIXED)
                        break;
                }
            }
        }

        public Quadrant(int position, int level, QuadrantStatus status, int[] levelDifferences, AABB region) {
            this.position = position;
            this.level = level;
            this.status = status;
            this.levelDifferences = levelDifferences;
            this.region = region;
        }

        public int levelDifference(LinearQuadTree.QuadrantDirection direction) {
            return levelDifferences[direction.ordinal()];
        }

        public String toString() {
            return String.format("(%0" + R + "d, %d, %s, [%d, %d, %d, %d, %d, %d, %d, %d])",
                    position,
                    level,
                    status,
                    levelDifferences[0],
                    levelDifferences[1],
                    levelDifferences[2],
                    levelDifferences[3],
                    levelDifferences[4],
                    levelDifferences[5],
                    levelDifferences[6],
                    levelDifferences[7]);
        }
    }
}
