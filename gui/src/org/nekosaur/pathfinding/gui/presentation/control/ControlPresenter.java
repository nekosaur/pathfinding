package org.nekosaur.pathfinding.gui.presentation.control;

import com.google.common.eventbus.Subscribe;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.nekosaur.pathfinding.gui.business.Controller;
import org.nekosaur.pathfinding.gui.business.MapData;
import org.nekosaur.pathfinding.gui.business.events.EditMapLoadEvent;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author nekosaur
 */
@SuppressWarnings("restriction")
public class ControlPresenter implements Initializable {

    @FXML
    private ChoiceBox<Integer> choiceGridSize;

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
    private CheckBox checkboxStep;

    @FXML
    private ChoiceBox<String> cboxMapType;
    
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        controller.registerEvents(this);


        
    }

    @FXML
    public void handleButtonLoadMapClicked() {
        System.out.println("Load");

        int[][] vertices = {
                {0,0,0,0,0,0,0,0},
                {0,1,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0}
        };

        controller.postEvent(new EditMapLoadEvent(new MapData(vertices, null)));
    }

    @FXML
    private void handleButtonStartClicked() {
        controller.find();
    }

    @FXML
    private void handleButtonStepClicked() {
        //controller.step();
    }

    @FXML
    private void handleButtonResetClicked() {
        //controller.reset();
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
