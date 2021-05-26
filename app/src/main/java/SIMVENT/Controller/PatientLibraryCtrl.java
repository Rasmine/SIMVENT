package SIMVENT.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.util.Arrays;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.cell.PropertyValueFactory;
import SIMVENT.Model.*;
import java.io.IOException;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class PatientLibraryCtrl {

    @FXML
    private TableView<PredefinedPatientModel> patientLibraryTable;

    @FXML
    private TableColumn<PredefinedPatientModel, String> patientIDColumn;
    
    @FXML
    private TableColumn<PredefinedPatientModel, String> patientDescriptionColumn;

    @FXML
    private Button returnToMainScreenButton;

    // Denne attribut bruges til at opbevare den valgte patient, så den kan gives videre til PatientInfoCtrl
    private static PredefinedPatientModel chosenPatient; 

    @FXML // Laver en MainScreenView når man trykker på knappen "Return to Main Screen"
    void handleReturnToMainScreenButton(ActionEvent event) throws Exception {
        Parent MainScreen = FXMLLoader.load(getClass().getResource("/MainScreenView.fxml"));
        Stage mainScreen = (Stage) returnToMainScreenButton.getScene().getWindow();
        mainScreen.setTitle("Main Screen");
        mainScreen.setScene(new Scene(MainScreen));
    }

    @FXML // Denne metode kører når PatientLibraryView instantieres
    private void initialize() {
        
        populateLibraryTable(); //Denne metode udfylder tabellen
        
        handleClickToChoosePatient(); // Denne metode bruges når der trykkes på en patient inde i tabellen
    }

    // Følgende metode udfylder TableView med Patienter
    private void populateLibraryTable() {
        patientIDColumn.setCellValueFactory(new PropertyValueFactory<PredefinedPatientModel, String>("patientID"));
        patientDescriptionColumn.setCellValueFactory(new PropertyValueFactory<PredefinedPatientModel, String>("patientDescription"));
        patientLibraryTable.getItems().setAll(createPatientList());
    }

    // Metode der håndterer klik på patient i tabellen
    private void handleClickToChoosePatient() {
        patientLibraryTable.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                // Her opdateres chosenPatient så den passer til den patient man har trykket på
                PredefinedPatientModel chosenPatient = patientLibraryTable.getSelectionModel().getSelectedItem();
                PatientLibraryCtrl.setChosenPatient(chosenPatient);

                // Her instantieres PatientInfoView
                FXMLLoader Loader = new FXMLLoader();
                Loader.setLocation(getClass().getResource("/PatientInfoView.fxml"));
                try {
                    Loader.load();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                Parent root = Loader.getRoot();
                Stage patientInfo = new Stage();
                patientInfo.setTitle("Patient Information");
                patientInfo.setScene(new Scene(root));
                patientInfo.initModality(Modality.APPLICATION_MODAL);
                patientInfo.show();

                // Følgende links er brugt til at lave popup ud fra et tryk på table (slet inden
                // aflevering)
                // https://github.com/javatech46/FxTableView/blob/master/FxmlTableView/src/fxmltableview/FXMLDocumentController.java
                // https://www.youtube.com/watch?v=ancUwZnPmLw&t=79s
            }

        });

    }

    // Denne metode bruges af PatientInfoCtrl for at få data fra den patient man har valgt
    public static PredefinedPatientModel getChosenPatient() {
        return chosenPatient;
    }

    // Denne metode bruges i initialize() til at opdatere chosenPatient til den patient man har trykket på
    private static void setChosenPatient(PredefinedPatientModel chosenPatient) {
        PatientLibraryCtrl.chosenPatient = chosenPatient;
    }

    // Denne metode bruges oppe i "initialize()" når tabellen skal udfyldes - her skal data fra predefined patients indsættes
    private List<PredefinedPatientModel> createPatientList() {
        PredefinedPatientModel patient1 = new PredefinedPatientModel("Patient 1", "Patient with low pH in arterial blood", 26,0.59,0.52,0.144,9,0.020,10,5,6.40,0.017,0.009,10.145,0.323,0.274,37,0.367,0.133,0.33,0.919,9.281,7.067,7.298,-1.137,0.553,25.0,54.2,9.0,0.33,0.914,8.923,6.799,7.312,-1.010, 1.0666708839380565,35.59,37.187);
      patient1.setPatientLearningObjective("The user must be able to identify the low compliance and the high shunt and compensate for this without exposing the patient of barotrauma or oxygen toxicity.");
        PredefinedPatientModel patient2 = new PredefinedPatientModel("Patient 2", "Patient with low arterial oxygen saturation",
        16,0.48,0.65,0.15,12,0.04,0.5,5,7.6,0.018,0.004,8.5,0.56,0.442,37,0.31,0.4,0.33,0.86333,8.05,8.323,7.242,-1.833,0.714,20,57,12,0.33,0.883,7.616,6.013,7.358,-0.683,0.7369092420014737,28.25, 29.85);
        patient2.setPatientLearningObjective("The user must be able to identify the low arterial oxygen saturation and pH in the arterial blood and compensate for this without exposing the patient to barotrauma or oxygen toxicity.");
        List<PredefinedPatientModel> patientList = Arrays.asList(patient1, patient2);
        return patientList;

    }

    

}