package org.nekosaur.pathfinding.gui.presentation.maps.grid;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.concurrent.Task;

import org.nekosaur.pathfinding.gui.business.MapData;
import org.nekosaur.pathfinding.gui.business.exceptions.MapDataIncompleteException;
import org.nekosaur.pathfinding.gui.presentation.maps.AbstractMap;
import org.nekosaur.pathfinding.gui.presentation.maps.IEditableMap;
import org.nekosaur.pathfinding.gui.presentation.maps.ISearchableMap;
import org.nekosaur.pathfinding.gui.presentation.maps.MapCanvas;

import org.nekosaur.pathfinding.lib.common.Vertex;
import org.nekosaur.pathfinding.lib.node.Node;
import org.nekosaur.pathfinding.lib.node.NodeState;
import org.nekosaur.pathfinding.lib.node.NodeStatus;

/**
 * @author nekosaur
 */
@SuppressWarnings("restriction")
public class GridMap extends AbstractMap implements ISearchableMap {

    private MapCanvas canvas;

    private int columns;
    private int rows;
    private double cellWidth;
    private double cellHeight;
    private double cellPadding = 1;
        
    private MapProperty<MapCell, MapCell> cells;
    
    private final ObjectProperty<? super Vertex> start = new SimpleObjectProperty<>();
    private final ObjectProperty<? super Vertex> goal = new SimpleObjectProperty<>();
    
	private GridMap(double width, double height/*, MapData mapData*/) {
        super();

        System.out.println(width + " " + height);

        this.width = width;
        this.height = height;

        this.canvas = new MapCanvas(width, height);
        this.cells = new SimpleMapProperty<MapCell, MapCell>(FXCollections.observableHashMap());

        //load(mapData.vertices);

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            System.out.println("Clicked!");
            
            int x = (int)(event.getX() / cellWidth);
            int y = (int)(event.getY() / cellHeight);
            
            System.out.println(x + " " + y);
            
            if (event.isPopupTrigger()) {
            	MapCell g = cells.get(new Vertex(x, y));
            	if (g.isStart)
            		return;
            	
            	if (goal.isNotNull().get()) {
            		MapCell pg = (MapCell)goal.get();
            		pg.isGoal = false;
                	draw(pg);
            	}
            	
            	g.isGoal = true;
            	goal.set(g);
            	draw(g);
            } else {
            	MapCell s = cells.get(new Vertex(x, y));
            	System.out.println(s);
            	if (s.isGoal)
            		return;
            	
            	if (start.isNotNull().get()) {
            		MapCell ps = (MapCell)start.get();
	            	ps.isStart = false;
	            	draw(ps);
            	}

            	s.isStart = true;
            	start.set((Vertex)s);
            	start.set(s);
            	draw(s);
            }

        });

        this.getChildren().add(canvas);
        
        System.out.println("ZXCXZC");

    }
			
	@SuppressWarnings("unchecked")
	public ObjectProperty<Vertex> getStartProperty() {
		return (ObjectProperty<Vertex>)start;
	}

	@SuppressWarnings("unchecked")
	public ObjectProperty<Vertex> getGoalProperty() {
		return (ObjectProperty<Vertex>)goal;
	}

	public void update(Node node) {
		MapCell cell = cells.get(node);
		
		cell.state = node.state;
		cell.status = node.status;
		
		draw(cell);
	}
	
	private void draw(MapCell cell) {
		Color cstate = NodeState.color(cell.state);
		Color cstatus = NodeStatus.color(cell.status);
		
		Color c = cstate.interpolate(cstatus, 0.5);
		
		if (cell.isGoal)
			c = Color.ORANGERED;
		else if (cell.isStart)
			c = Color.LIMEGREEN;
		
		canvas.drawRect(cell.x * cellWidth,  cell.y * cellHeight, cellWidth - cellPadding, cellHeight - cellPadding, c);
	}
	
	private void load(int[][] data) {
		columns = data[0].length;
		rows = data.length;
		double cellSize = Math.min((width - (columns * cellPadding)) / columns, (height - (rows * cellPadding)) / rows);
        cellWidth = cellSize;
        cellHeight = cellSize;
        
        System.out.println("width="+width);
        System.out.println("height="+height);
        System.out.println("cellsize="+cellSize);
        
		
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
            	MapCell cell = new MapCell(x, y, NodeState.parse(data[y][x]));
                cells.put(cell, cell);
                draw(cell);
            }
        }
	}
	
	public static Task<ISearchableMap> create(double width, double height, MapData mapData) {
		if (!mapData.getVertices().isPresent())
			throw new MapDataIncompleteException("Missing vertices");
		
		return new Task<ISearchableMap>() {
			
			final int[][] data = mapData.getVertices().get();
			final double max = width * height;

			@Override
			protected ISearchableMap call() throws Exception {
				
				System.out.println("Creating GridMap");
				
				GridMap map = new GridMap(width, height);
								
				map.columns = data[0].length;
				map.rows = data.length;
				double cellSize = Math.min((width - (map.columns * map.cellPadding)) / map.columns, (height - (map.rows * map.cellPadding)) / map.rows);
				map.cellWidth = cellSize;
				map.cellHeight = cellSize;
		        
		        System.out.println("width="+width);
		        System.out.println("height="+height);
		        System.out.println("cellsize="+cellSize);
		        
				double progress = 0;
		        for (int y = 0; y < map.rows; y++) {
		            for (int x = 0; x < map.columns; x++) {
		            	MapCell cell = map.new MapCell(x, y, NodeState.parse(data[y][x]));
		            	map.cells.put(cell, cell);
		            	map.draw(cell);
		            	
		            	this.updateProgress(progress++, max);
		            }
		        }
		        
		        return map;
				
			}
			
		};
	}

	@Override
	public MapData getData() {
		
		int[][] vertices = new int[rows][columns];
		
		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < columns; x++) {
				vertices[y][x] = cells.get(new Vertex(x, y)).state.value;
			}
		}

		return new MapData(vertices, null, (Vertex)start.get(), (Vertex)goal.get());
	}
	
	class MapCell extends Vertex {
		private NodeState state = NodeState.EMPTY;
		private NodeStatus status = NodeStatus.INACTIVE;
		private boolean isStart = false;
		private boolean isGoal = false;
		
		public MapCell(int x, int y, NodeState state) {
			super(x, y);
			this.state = state;
		}
		
	}

}




