package SIMVENT.Controller;


import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class EndTrainingSessionCtrl {

    @FXML
    private Button YesEndTrainingSessionButton;

    @FXML
    private Button ReturnToTrainingSessionButton;

    @FXML //Metoden sørger for at lukke pop up'en hvis man gerne vil tilbage til trænings sessionen
    void handleReturnToTrainingSessionButton(ActionEvent event) {
        Stage endTrainingSession = (Stage) ReturnToTrainingSessionButton.getScene().getWindow();
        endTrainingSession.close();
    }

    @FXML //Her instantieres DisclaimerView og DisclaimerCtrl
    void handleYesEndTrainingSessionButton(ActionEvent event) throws IOException {
        Parent disclaimerView = FXMLLoader.load(getClass().getResource("/DisclaimerView.fxml"));
        Stage disclaimer = (Stage) YesEndTrainingSessionButton.getScene().getWindow();
        disclaimer.setTitle("Disclaimer");
        disclaimer.setScene(new Scene(disclaimerView));
    }

}
