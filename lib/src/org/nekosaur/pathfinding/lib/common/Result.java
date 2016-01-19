package org.nekosaur.pathfinding.lib.common;

import java.util.List;

public class Result {
    private final List<Point> path;
    private final long duration;
    private final long operations;
    private final double cost;

    public Result(List<Point> path, long duration, long operations) {
        this.path = path;
        this.duration = duration;
        this.operations = operations;
        this.cost = path.size();
    }

    public Result(List<Point> path, double cost, long duration, long operations) {
        this.path = path;
        this.duration = duration;
        this.operations = operations;
        this.cost = cost;
    }

    /**
     * @return A list consisting of the nodes used in the found path
     */
    public List<Point> path() {
        return path;
    }

    /**
     * @return The duration of the search, in milliseconds 
     */
    public long duration() {
        return duration;
    }

    /**
     * @return The number of operations performed during the search
     */
    public long operations() {
        return operations;
    }
    
    /**
     * @return The final cost of the found path
     */
    public double cost() {
        return cost;
    }
}