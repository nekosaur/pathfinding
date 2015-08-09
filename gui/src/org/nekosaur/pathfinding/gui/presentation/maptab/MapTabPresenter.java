package org.nekosaur.pathfinding.gui.presentation.maptab;

import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javafx.application.Platform;
import org.nekosaur.pathfinding.gui.business.Controller;
import org.nekosaur.pathfinding.gui.business.TriFunction;
import org.nekosaur.pathfinding.gui.business.events.*;
import org.nekosaur.pathfinding.gui.presentation.maps.editable.IEditableMap;
import org.nekosaur.pathfinding.gui.presentation.maps.searchable.ISearchableMap;
import org.nekosaur.pathfinding.lib.common.MapData;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author nekosaur
 */
@SuppressWarnings("restriction")
public class MapTabPresenter implements Initializable {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private HBox editPane;

    @FXML
    private HBox searchPane;
    
    @FXML
    private TabPane tabPane;
    
    @FXML
    private Tab searchTab;
    
    @FXML
    private Tab editTab;

    @Inject
    private Controller controller;

    IEditableMap editMap;
    ISearchableMap searchMap;
    TriFunction<Double, Double, MapData, IEditableMap> selectedMapType;
    TriFunction<Double, Double, MapData, ISearchableMap> selectedSearchableMap;
    int mapSize;

    private static final double MAP_WIDTH = 1024;
    private static final double MAP_HEIGHT = 1024;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        controller.registerEvents(this);
        
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
        	System.out.println("Switching tabs!");
        	if (n.equals(searchTab))
        		controller.postEvent(new SearchMapLoadEvent(editMap.getData()));
        });
    }

    @Subscribe
    public void handlePathFoundEvent(PathFoundEvent e) {
        if (searchMap != null)
            searchMap.drawPath(e.getPath());
    }

    @Subscribe
    public void handleHistoryUpdatedEvent(HistoryUpdatedEvent e) {
        searchMap.update(e.getNode());
    }

    @Subscribe
    public void handleChangeEditableMapTypeEvent(ChangeEditableMapTypeEvent e) {
        selectedMapType = e.getFunction();

        controller.postEvent(new EditMapLoadEvent(null));
    }

    @Subscribe
    public void handleChangeSearchableMapTypeEvent(ChangeSearchableMapTypeEvent e) {
        selectedSearchableMap = e.getFunction();

        if (tabPane.getSelectionModel().selectedItemProperty().get().equals(searchTab))
            controller.postEvent(new SearchMapLoadEvent(null));
    }

    @Subscribe
    public void handleChangeMapSizeEvent(ChangeMapSizeEvent e) {
        mapSize = e.getMapSize();

        if (editMap != null)
            controller.postEvent(new EditMapLoadEvent(new MapData(new int[mapSize][mapSize], null)));
    }

    @Subscribe
    public void handleEditMapLoadEvent(EditMapLoadEvent e) {
        MapData data = e.getData() == null ? new MapData(new int[mapSize][mapSize], null) : e.getData();
        if (editMap != null) {
            editPane.getChildren().remove(editMap);
            editMap = null;
        }

        System.out.println("Loading edit map");

        editMap = selectedMapType.apply(MAP_WIDTH, MAP_HEIGHT, data);
        editPane.getChildren().add((Pane) editMap);

        tabPane.getSelectionModel().select(editTab);
        
    }
    
    @Subscribe
    public void handleSearchMapLoadEvent(SearchMapLoadEvent e) {
    	if (searchMap != null) {
    		searchPane.getChildren().remove(searchMap);
    		searchMap = null;
    	}

    	System.out.println("Loading search map");
        //GridMap.create(MAP_WIDTH, MAP_HEIGHT, e.getData());
    	Task<ISearchableMap> task = new Task<ISearchableMap>() {

            @Override
            protected ISearchableMap call() throws Exception {
                return selectedSearchableMap.apply(MAP_WIDTH, MAP_HEIGHT, editMap.getData());
            }
        };
    	
    	ProgressBar pbar = new ProgressBar();
    	pbar.progressProperty().bind(task.progressProperty());
    	searchPane.getChildren().add(pbar);
    	
    	task.setOnSucceeded((event) -> {
    		System.out.println("Succeeded!");
    		Platform.runLater(() -> {
    			searchPane.getChildren().remove(pbar);
    			searchMap = task.getValue();
    			searchPane.getChildren().add((Pane)searchMap);

                System.out.println(searchMap.getStartProperty());
    			controller.getStartProperty().bind(searchMap.getStartProperty());
    			controller.getGoalProperty().bind(searchMap.getGoalProperty());
    			controller.setSearchableMap(searchMap);
    		});
    	});

        task.setOnFailed(event -> {
            try {
                throw event.getSource().getException();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    	
    	Thread t = new Thread(task);
    	t.start();
    	
    }

}
