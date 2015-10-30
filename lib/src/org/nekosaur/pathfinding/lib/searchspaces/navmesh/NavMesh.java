package org.nekosaur.pathfinding.lib.searchspaces.navmesh;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.nekosaur.pathfinding.lib.common.MapData;
import org.nekosaur.pathfinding.lib.common.Option;
import org.nekosaur.pathfinding.lib.common.Vertex;
import org.nekosaur.pathfinding.lib.exceptions.MissingMapDataException;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.node.Node;
import org.nekosaur.pathfinding.lib.node.NodeState;
import org.nekosaur.pathfinding.lib.searchspaces.AbstractSearchSpace;
import org.nekosaur.pathfinding.lib.searchspaces.grid.Grid;

import javafx.scene.image.Image;

public class NavMesh extends AbstractSearchSpace {
	
	private HashMap<Node, Triangle> triangles; 

	public NavMesh(int width, int height) {
		super(width, height, EnumSet.of(Option.DIAGONAL_MOVEMENT, Option.MOVING_THROUGH_WALL_CORNERS));
	}
	
	public static SearchSpace create(MapData data) {
		if (!data.getVertices().isPresent())
			throw new MissingMapDataException("Vertices missing");

		int[][] vertices = data.getVertices().get();
		
		NavMesh navMesh = new NavMesh(vertices[0].length, vertices[1].length);
		
		// Do MarchingSquares on all obstacles to get outlines
		// Reduce vertices with VisvalingamWhyatt
		MarchingSquares ms = new MarchingSquares(vertices);
		List<List<Vertex>> obstaclePerimeters = new LinkedList<>();
		for (int y = 0; y < vertices.length; y++) {
            for (int x = 0; x < vertices[0].length; x++) {
                    if (x - 1 >= 0 && vertices[y][x-1] == 0 && vertices[y][x] > 0) {
                    	// found edge of an obstacle
                    	List<Vertex> perimeter = ms.identifyPerimeter(x, y);
                    	obstaclePerimeters.add(VisvalingamWhyatt.reduce(perimeter, (int)Math.floor(perimeter.size() * 0.8f)));
                    }
            }
        }
		
		System.out.println(obstaclePerimeters.size());
		
		// Triangulate complete mesh
		
		// Create triangles from resulting data and add to NavMesh
		
		return navMesh;
	}

	public static SearchSpace create(MapData data, EnumSet<Option> options) {
		NavMesh n = (NavMesh)NavMesh.create(data);
		n.options = options;
		return n;
	}

	@Override
	public List<Node> getNeighbours(Node n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getMovementCost(Node n1, Node n2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Node getNode(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isWalkableAt(int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Image draw(int side) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchSpace copy() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	private class Triangle {
		private Vertex[] points; 
		private Node node;

		public Node getNode() {
			return node;
		}
	}*/
	
	private class Triangle extends Node {

		public Triangle(int x, int y) {
			super(x, y);
			// TODO Auto-generated constructor stub
		}
		
	}
}
