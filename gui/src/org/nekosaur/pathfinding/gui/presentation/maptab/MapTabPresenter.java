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
import org.nekosaur.pathfinding.gui.business.events.EditMapLoadEvent;
import org.nekosaur.pathfinding.gui.business.events.SearchMapLoadEvent;
import org.nekosaur.pathfinding.gui.presentation.dialogs.ProgressDialog;
import org.nekosaur.pathfinding.gui.presentation.maps.IEditableMap;
import org.nekosaur.pathfinding.gui.presentation.maps.ISearchableMap;
import org.nekosaur.pathfinding.gui.presentation.maps.editable.EditableGridMap;
import org.nekosaur.pathfinding.gui.presentation.maps.grid.GridMap;

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
    public void handleEditMapLoadEvent(EditMapLoadEvent e) {
        if (editMap != null) {
            editPane.getChildren().remove(editMap);
            editMap = null;
        }

        System.out.println("Loading edit map");

        editMap = new EditableGridMap(rootPane.getPrefWidth(), rootPane.getPrefHeight(), e.getData());
        editPane.getChildren().add((Pane)editMap);
        
    }
    
    @Subscribe
    public void handleSearchMapLoadEvent(SearchMapLoadEvent e) {
    	if (searchMap != null) {
    		if (!editMap.isDirty())
    			return;
    		searchPane.getChildren().remove(searchMap);
    		searchMap = null;
    	}
    	
    	System.out.println("Loading search map");
    	Task<ISearchableMap> task = GridMap.create(rootPane.getPrefWidth(), rootPane.getPrefHeight(), e.getData());
    	
    	ProgressBar pbar = new ProgressBar();
    	pbar.progressProperty().bind(task.progressProperty());
    	searchPane.getChildren().add(pbar);
    	
    	task.setOnSucceeded((event) -> {
    		System.out.println("Succeeded!");
    		Platform.runLater(() -> {
    			searchPane.getChildren().remove(pbar);
    			searchMap = task.getValue();
    			searchPane.getChildren().add((Pane)searchMap);
    			
    			controller.getStartProperty().bind(searchMap.getStartProperty());
    			controller.getGoalProperty().bind(searchMap.getGoalProperty());
    			controller.setSearchSpace(searchMap.getData());
    		});
    	});
    	
    	Thread t = new Thread(task);
    	t.start();
    	
    }

}
