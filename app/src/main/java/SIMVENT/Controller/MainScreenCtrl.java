package SIMVENT.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;


public class MainScreenCtrl {

    @FXML
    private Button predefinedPatientButton;


    @FXML // Denne metode instantierer PatientLibraryView, som styres af PatientLibraryCtrl
    void handlePredefinedPatientButton(ActionEvent event) throws Exception {
        Parent pLibrary = FXMLLoader.load(getClass().getResource("/PatientLibraryView.fxml"));
        Stage patientLibrary = (Stage) predefinedPatientButton.getScene().getWindow();
        patientLibrary.setTitle("Patient Library");
        patientLibrary.setScene(new Scene(pLibrary));

    }

}


   




