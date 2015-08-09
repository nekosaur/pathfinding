package org.nekosaur.pathfinding.gui.presentation.control;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.nekosaur.pathfinding.gui.business.Controller;
import org.nekosaur.pathfinding.gui.business.TriFunction;
import org.nekosaur.pathfinding.gui.business.events.ChangeEditableMapTypeEvent;
import org.nekosaur.pathfinding.gui.business.events.ChangeMapSizeEvent;
import org.nekosaur.pathfinding.gui.business.events.ChangeSearchableMapTypeEvent;
import org.nekosaur.pathfinding.gui.presentation.maps.editable.EditableGraphMap;
import org.nekosaur.pathfinding.gui.presentation.maps.editable.IEditableMap;
import org.nekosaur.pathfinding.gui.presentation.maps.editable.EditableGridMap;
import org.nekosaur.pathfinding.gui.presentation.maps.searchable.GraphMap;
import org.nekosaur.pathfinding.gui.presentation.maps.searchable.GridMap;
import org.nekosaur.pathfinding.gui.presentation.maps.searchable.ISearchableMap;
import org.nekosaur.pathfinding.gui.presentation.maps.searchable.QuadTreeMap;
import org.nekosaur.pathfinding.lib.common.MapData;
import org.nekosaur.pathfinding.gui.business.events.EditMapLoadEvent;
import org.nekosaur.pathfinding.lib.common.Option;
import org.nekosaur.pathfinding.lib.movingai.MovingAI;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * @author nekosaur
 */
@SuppressWarnings("restriction")
public class ControlPresenter implements Initializable {

    @FXML
    private ChoiceBox<Integer> choiceMapSize;

    @FXML
    private Button btnStart;

    @FXML
    private Button btnStep;

    @FXML
    private Button btnLoadMap;

    @FXML
    private Button btnSaveMap;

    @FXML
    private Button btnReset;

    @FXML
    private CheckBox cboxStep;

    @FXML
    private ChoiceBox<String> choiceMapType;

    @FXML
    private ChoiceBox<String> choiceSearchSpace;
    
    @FXML
    private CheckBox cboxIncludeScenario;

    @FXML
    private CheckBox cboxDiagonalMovement;

    @FXML
    private CheckBox cboxWallCornerMovement;

    @FXML
    private ChoiceBox<String> choicePathfinder;

    @FXML
    private ChoiceBox<String> choiceHeuristic;

    @FXML
    private Button btnLoadScenario;

    @FXML
    private Button btnRunScenario;

    @FXML
    private Slider sliderSpeed;
    
    //@FXML
    //private ListView<Experiment> listExperiments;
    
    @FXML
    private TextField txtWeight;
    
    @FXML
    private Spinner<Double> spinWeight;

    @Inject
    private Controller controller;

    private SpinnerValueFactory.DoubleSpinnerValueFactory weightValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(1, 5, 1, 0.5);

    private final Map<String, TriFunction<Double, Double, MapData, IEditableMap>> editableMaps = new LinkedHashMap<>();
    private final Map<String, TriFunction<Double, Double, MapData, ISearchableMap>> searchableMaps = new LinkedHashMap<>();
    private final Integer[] mapSizes = new Integer[] {4, 8, 16, 32, 64, 128, 256, 512};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        controller.registerEvents(this);

        // TODO: put this somewhere else?
        editableMaps.put("Grid", EditableGridMap::new);
        editableMaps.put("Graph", EditableGraphMap::new);
        searchableMaps.put("Grid", GridMap::new);
        searchableMaps.put("QuadTree", QuadTreeMap::new);
        searchableMaps.put("Graph", GraphMap::new);

        choiceMapType.getItems().addAll(editableMaps.keySet());
        choiceMapType.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            controller.postEvent(new ChangeEditableMapTypeEvent(editableMaps.get(n)));
        });

        choiceMapSize.getItems().addAll(Arrays.asList(mapSizes));
        choiceMapSize.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            controller.postEvent(new ChangeMapSizeEvent(n));
        });

        choiceSearchSpace.getItems().addAll(searchableMaps.keySet());
        choiceSearchSpace.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            controller.postEvent(new ChangeSearchableMapTypeEvent(searchableMaps.get(n)));
        });

        choicePathfinder.getItems().addAll(controller.getPathfinders());
        choicePathfinder.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            controller.selectPathfinder(n);
        });

        choiceHeuristic.getItems().addAll(controller.getHeuristics());
        choiceHeuristic.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            controller.selectHeuristic(n);
        });

        cboxDiagonalMovement.selectedProperty().addListener((obs, o, n) -> {
            controller.setOption(Option.DIAGONAL_MOVEMENT, n);
        });

        cboxWallCornerMovement.selectedProperty().addListener((obs, o, n) -> {
            controller.setOption(Option.MOVING_THROUGH_WALL_CORNERS, n);
        });

        spinWeight.valueFactoryProperty().set(weightValueFactory);
        spinWeight.valueProperty().addListener(new ChangeListener<Double>() {

            @Override
            public void changed(ObservableValue<? extends Double> obs, Double o, Double n) {
                controller.weightProperty().set(n);
            }

        });

        cboxStep.selectedProperty().bindBidirectional(controller.isSteppingProperty());
        sliderSpeed.valueProperty().bindBidirectional(controller.speedDelayProperty());
        btnStep.disableProperty().bind(Bindings.not(controller.isSteppingProperty()).or(Bindings.not(controller.isRunningProperty())));
        btnStart.disableProperty().bind(controller.isRunningProperty());

        choiceMapSize.getSelectionModel().select(0);
        choiceMapType.getSelectionModel().select(0);
        choiceSearchSpace.getSelectionModel().select(0);
        choicePathfinder.getSelectionModel().select(0);
        choiceHeuristic.getSelectionModel().select(0);
        cboxDiagonalMovement.selectedProperty().set(true);
        cboxWallCornerMovement.selectedProperty().set(false);
    }

    @FXML
    private void handleButtonStartClicked() {
        controller.find();
    }

    @FXML
    private void handleButtonStepClicked() {
        controller.step();
    }

    @FXML
    private void handleButtonResetClicked() {
        controller.reset();
    }

    @FXML
    private void handleButtonLoadMapClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Map");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Map files", "*.map")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            controller.postEvent(new EditMapLoadEvent(new MapData(MovingAI.loadMap(selectedFile), null)));
            //controller.loadMap(selectedFile, cboxIncludeScenario.selectedProperty().getValue());
        }
    }

    @FXML
    private void handleButtonSaveMapClicked() {
        /*
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Map");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Map files", "*.map")
        );

        File file = fileChooser.showSaveDialog(null);

        if (file != null)
            controller.saveMap(file);
        */
    }

    @FXML
    private void handleButtonLoadScenarioClicked() {
        /*
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Scenario");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Scenario files", "*.scen")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            controller.loadScenario(selectedFile);
        }
        */
    }

    @FXML
    private void handleButtonSaveScenarioClicked() {
        //controller.runScenario();
    }

    @FXML
    private void handleButtonAddExperimentClicked() {
        //controller.addExperiment(choiceGridSize.getSelectionModel().getSelectedItem());
    }

    @FXML void handleButtonRemoveExperimentClicked() {
        //listExperiments.getItems().remove(listExperiments.getSelectionModel().getSelectedItem());
    }


}
