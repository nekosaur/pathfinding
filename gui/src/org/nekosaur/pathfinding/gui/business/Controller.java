package org.nekosaur.pathfinding.gui.business;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;

import com.google.common.eventbus.EventBus;
import org.nekosaur.pathfinding.gui.business.events.EditMapLoadEvent;
import org.nekosaur.pathfinding.lib.common.Heuristics;
import org.nekosaur.pathfinding.lib.common.Result;
import org.nekosaur.pathfinding.lib.common.Vertex;
import org.nekosaur.pathfinding.lib.interfaces.Heuristic;
import org.nekosaur.pathfinding.lib.interfaces.Pathfinder;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.pathfinders.astar.AStarFinder;
import org.nekosaur.pathfinding.lib.searchspaces.Grid;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

/**
 * @author nekosaur
 */
@SuppressWarnings("restriction")
public class Controller {
	
	private SearchSpace searchSpace;
	private Pathfinder pathfinder;
	private Heuristic heuristic;
	
	private final ObjectProperty<Vertex> start = new SimpleObjectProperty<>();
	private final ObjectProperty<Vertex> goal = new SimpleObjectProperty<>();

    private final EventBus eventBus = new EventBus();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @PostConstruct
    public void init() {

        start.addListener((obs, o, n) -> {
        	System.out.println("setting start to "+n);
        });

    }
    
    public ObjectProperty<Vertex> getStartProperty() {
		return start;
	}

	public ObjectProperty<Vertex> getGoalProperty() {
		return goal;
	}

	public void registerEvents(Object o) {
        eventBus.register(o);
    }

    public void postEvent(Object e) {
        eventBus.post(e);
    }
    
    public void setSearchSpace(MapData data) {
    	searchSpace = Grid.create(data.getVertices().get(), null);
    }
    
    public void setPathfinder(Class<Pathfinder> clazz) {
    	
    }
    
    public void find() {
    	
    	pathfinder = new AStarFinder();
    	heuristic = Heuristics.euclidean;
    	    	
    	//history = new HistoryListener(pathfinder.getHistory());

        // TODO: right now we're simply hoping that the map resets completely before the pathfinder starts
        //postEvent(new MapResetEvent());

        //isRunning.set(true);
    	
    	System.out.println("start="+start.get());
    	System.out.println("goal="+goal.get());

        
        Task<Result> task = new Task<Result>() {
            @Override
            protected Result call() throws Exception {
                try {
                    return pathfinder.findPath(searchSpace, start.get(), goal.get(), heuristic, 1);
                } catch (InterruptedException ex) {
                    System.out.println("Task was interrupted");
                    return new Result(new ArrayList<Vertex>(), 0, 0, 0);
                }
            }
        };

        task.setOnFailed(event -> {
            //isRunning.set(false);
            try {
                throw event.getSource().getException();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            System.out.println("Task failed");
        });

        task.setOnSucceeded(event -> {

            // No path found, we need to abort
            if (task.getValue().path().size() == 0) {
                //isRunning.set(false);
            }

            System.out.println(String.format("TIME: %s", time(task.getValue().duration())));
            System.out.println("OPERATIONS: " + task.getValue().operations());
            System.out.println("LENGTH: " + task.getValue().path().size());


        });

        executor.submit(task);
        //executor.submit(history);
    	
    }
    
    private String time(long value) {
        return String.format("%d,%d s", TimeUnit.MILLISECONDS.toSeconds(value), TimeUnit.MILLISECONDS.toMillis(value));
    }
}
