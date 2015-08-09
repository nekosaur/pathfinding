package org.nekosaur.pathfinding.gui;

import com.airhacks.afterburner.injection.Injector;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.nekosaur.pathfinding.gui.presentation.main.MainPresenter;
import org.nekosaur.pathfinding.gui.presentation.main.MainView;
import org.nekosaur.pathfinding.gui.presentation.maptab.MapTabView;

/**
 *
 * @author nekosaur
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        MainView appView = new MainView();
        Scene scene = new Scene(appView.getView());
        stage.setTitle("followme.fx");
        stage.setScene(scene);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                ((MainPresenter)appView.getPresenter()).shutdown();
            }

        });

        stage.show();
    }

    @Override
    public void stop() throws Exception {
        Injector.forgetAll();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
