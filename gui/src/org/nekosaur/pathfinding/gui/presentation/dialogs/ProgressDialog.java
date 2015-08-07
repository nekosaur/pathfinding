package org.nekosaur.pathfinding.gui.presentation.dialogs;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.Dialog;
import javafx.scene.control.ProgressBar;

@SuppressWarnings("restriction")
public class ProgressDialog {
	
	public static Dialog<Void> show(ReadOnlyDoubleProperty progressProperty) {
		
		Dialog<Void> dialog = new Dialog<>();
		
		dialog.setTitle("Loading map");
		ProgressBar pb = new ProgressBar();
		pb.setPrefWidth(dialog.getWidth());
		pb.progressProperty().bind(progressProperty);
		
		dialog.getDialogPane().setContent(pb);
		
		dialog.show();
		
		
		return null;
	}

}
