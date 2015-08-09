package org.nekosaur.pathfinding.gui.business;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.concurrent.Task;

import com.google.common.eventbus.EventBus;
import org.nekosaur.pathfinding.gui.business.events.HistoryUpdatedEvent;
import org.nekosaur.pathfinding.gui.business.events.PathFoundEvent;
import org.nekosaur.pathfinding.gui.presentation.maps.searchable.ISearchableMap;
import org.nekosaur.pathfinding.lib.PathfinderFactory;
import org.nekosaur.pathfinding.lib.SearchSpaceFactory;
import org.nekosaur.pathfinding.lib.common.*;
import org.nekosaur.pathfinding.lib.interfaces.Heuristic;
import org.nekosaur.pathfinding.lib.interfaces.Pathfinder;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.node.Node;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;

/**
 * @author nekosaur
 */
@SuppressWarnings("restriction")
public class Controller {

    private String selectedSearchSpace;
    private String selectedPathfinder;
    private String selectedHeuristic;
	private SearchSpace searchSpace;
	private Pathfinder pathfinder;
	private Heuristic heuristic;
    private ISearchableMap searchableMap;
    private EnumSet<Option> options = EnumSet.noneOf(Option.class);

    private Map<String, Function<MapData, SearchSpace>> searchSpaces;
    private Map<String, Supplier<Pathfinder>> pathfinders;
    private Map<String, Heuristic> heuristics;
	
	private final ObjectProperty<Vertex> start = new SimpleObjectProperty<>();
	private final ObjectProperty<Vertex> goal = new SimpleObjectProperty<>();

    private final EventBus eventBus = new EventBus();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Task<Result> task;
    private HistoryListener history;

    private boolean isDirty = false;
    private final BooleanProperty isStepping = new SimpleBooleanProperty(false);
    private final BooleanProperty isRunning = new SimpleBooleanProperty(false);
    private final LongProperty speedDelay = new SimpleLongProperty(0);
    private final DoubleProperty weight = new SimpleDoubleProperty(1);

    @PostConstruct
    public void init() {
        searchSpaces = SearchSpaceFactory.getSearchSpaces();
        pathfinders = PathfinderFactory.getPathfinders();
        heuristics = Heuristics.getHeuristics();
    }

    public Set<String> getPathfinders() {
        return pathfinders.keySet();
    }

    public Set<String> getSearchSpaces() {
        return searchSpaces.keySet();
    }

    public Set<String> getHeuristics() {
        return heuristics.keySet();
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

    public void setSearchableMap(ISearchableMap map) {
        this.searchableMap = map;
    }

    public void selectPathfinder(String pathfinder) {
        selectedPathfinder = pathfinder;
    }

    public void selectHeuristic(String heuristic) {
        selectedHeuristic = heuristic;
    }

    public BooleanProperty isSteppingProperty() {
        return isStepping;
    }

    public BooleanProperty isRunningProperty() { return isRunning; }

    public LongProperty speedDelayProperty() {
        return speedDelay;
    }

    public DoubleProperty weightProperty() {
        return weight;
    }

    public void setOption(Option option, boolean value) {
        if (value)
            options.add(option);
        else
            options.remove(option);
    }

    public void step() {
        history.step();
    }

    public void reset() {
        searchableMap.reset();
        isDirty = false;
    }
    
    public void find() {

        // TODO: sanity checks for searchspace, pathfinder, heuristic, start and goal positions
        if (isDirty) {
            reset();
        }
        searchSpace = searchableMap.getSearchSpace();
        searchSpace.allow(options);

        pathfinder = pathfinders.get(selectedPathfinder).get();
    	
    	heuristic = heuristics.get(selectedHeuristic);
    	    	
   	    history = new HistoryListener(pathfinder.getHistory());

        // TODO: right now we're simply hoping that the map resets completely before the pathfinder starts
        //postEvent(new MapResetEvent());

        isRunning.set(true);
        isDirty = true;
    	
    	System.out.println("start="+start.get());
    	System.out.println("goal="+goal.get());

        
        task = new Task<Result>() {
            @Override
            protected Result call() throws Exception {
                try {
                    return pathfinder.findPath(searchSpace, start.get(), goal.get(), heuristic, weight.get());
                } catch (InterruptedException ex) {
                    System.out.println("Task was interrupted");
                    return new Result(new ArrayList<Vertex>(), 0, 0, 0);
                }
            }
        };

        task.setOnFailed(event -> {
            isRunning.set(false);
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
                isRunning.set(false);
            }

            System.out.println(String.format("TIME: %s", time(task.getValue().duration())));
            System.out.println("OPERATIONS: " + task.getValue().operations());
            System.out.println("LENGTH: " + task.getValue().path().size());

        });

        executor.submit(task);
        executor.submit(history);
    	
    }

    public void shutdown() {
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
            executor.shutdownNow();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private String time(long value) {
        return String.format("%d ms", TimeUnit.NANOSECONDS.toMillis(value));
    }

    private class HistoryListener extends Thread {

        Buffer<Node> history;
        boolean isPaused = false;

        public HistoryListener(Buffer<Node> history) {
            this.history = history;
        }

        @Override
        public void run() {
            synchronized (this) {
                try {
                    while(history.size() > 0) {
                        while (isStepping.get() && isPaused) {
                            wait();
                        }

                        final Node node = history.get();

                        Platform.runLater(() -> {
                            postEvent(new HistoryUpdatedEvent(node));
                        });

                        isPaused = true;

                        if (speedDelay.get() > 0)
                            Thread.sleep(speedDelay.get());

                    }

                    Platform.runLater(() -> {
                        postEvent(new PathFoundEvent(task.getValue().path()));
                        isRunning.set(false);
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }

        public synchronized void step() {
            isPaused = false;
            notifyAll();
        }
    }
}
