package SIMVENT.Controller;

import java.io.IOException;
import java.text.DecimalFormat;

import SIMVENT.INVENT.PatientState;
import SIMVENT.INVENT.Penalties;
import SIMVENT.Model.PredefinedPatientModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EvaluationCtrl {

    @FXML
    private Button returnToMainScreenButton;

    @FXML
    private Text VT_Adjusted;

    @FXML
    private Text F_Adjusted;

    @FXML
    private Text FiO2_Adjusted;

    @FXML
    private Text PEEP_Adjusted;

    @FXML
    private Text IE_Adjusted;

    @FXML
    private Text VT_Optimal;

    @FXML
    private Text F_Optimal;

    @FXML
    private Text FiO2_Optimal;

    @FXML
    private Text PEEP_Optimal;

    @FXML
    private Text IE_Optimal;

    @FXML
    private Button viewOptimalValuesButton;

    @FXML
    private Label performanceScore;

    @FXML
    private Label learningObjective;

    @FXML // Denne metode kører når OptimalValuesView instantieres
    private void initialize() {
        PredefinedPatientModel simulationPatient = TrainingSessionCtrl.getChosenPatient();
        displayOptimalSettings(simulationPatient);
        displayYourSettings(simulationPatient);
        learningObjective.setText(simulationPatient.getPatientLearningObjective());
        String score = calculateScore(simulationPatient);
        displayScore(score);

    }

    // Denne metode indsætter den valgte patients optimale variable i viewet
    private void displayOptimalSettings(PredefinedPatientModel patient) {
        VT_Optimal.setText(String.valueOf(patient.getVT_optimal() * 1000));
        F_Optimal.setText(String.valueOf(patient.getFreq_optimal()));
        FiO2_Optimal.setText(String.valueOf(patient.getFiO2_optimal()));
        PEEP_Optimal.setText(String.valueOf(patient.getPEEP_optimal()));
        IE_Optimal.setText(String.valueOf(patient.getIE_optimal()));
    }

    // Denne metode indsætter de valgte settings i viewet
    private void displayYourSettings(PredefinedPatientModel patient) {
        VT_Adjusted.setText(String.valueOf(patient.getVT_value() * 1000));
        F_Adjusted.setText(String.valueOf(patient.getfreq_value()));
        FiO2_Adjusted.setText(String.valueOf(patient.getFiO2_value() * 100));
        PEEP_Adjusted.setText(String.valueOf(patient.getPEEP_value()));
        IE_Adjusted.setText(String.valueOf(patient.getIE_value()));

    }

    // Denne metode udregner, ved brug af penalty functionen fra INVENT, den performance score useren får
    private String calculateScore(PredefinedPatientModel patient) {
        PatientState yourPatientState = patient.getYourPatientState();
        Penalties yourPenalties = new Penalties();
        double yourPenalty = yourPenalties.calcPenalties(yourPatientState);
        double optimalPenalty = patient.getOptimalPenalty();
        System.out.println("Penalty for simulering: " + yourPenalty);
        System.out.println("Penalty for optimal: " + optimalPenalty);
        DecimalFormat df = new DecimalFormat("###.#");
        double scoren = optimalPenalty / yourPenalty * 100;
        String score = df.format(scoren);
        return score;

    }

    // Her opdateres viewet med den udregnede score
    private void displayScore(String score) {
        performanceScore.setText(score);
    }

    @FXML // Denne metode laver pop up når der trykkes på end training session knappen
    void handleViewOptimalValuesButton(ActionEvent event) throws Exception {
        FXMLLoader Loader = new FXMLLoader();
        Loader.setLocation(getClass().getResource("/OptimalValuesPopUpView.fxml"));
        try {
            Loader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Parent root = Loader.getRoot();
        Stage endTrainingSession = new Stage();
        endTrainingSession.setTitle("Optimal Values");
        endTrainingSession.setScene(new Scene(root));
        endTrainingSession.initModality(Modality.APPLICATION_MODAL);
        endTrainingSession.show();
    }

    @FXML // Denne metode instantiere et MainScreenView og MainScreenCtrl
    void handleReturnToMainScreenButton(ActionEvent event) throws Exception {
        Parent MainScreen = FXMLLoader.load(getClass().getResource("/MainScreenView.fxml"));
        Stage mainScreen = (Stage) returnToMainScreenButton.getScene().getWindow();
        mainScreen.setTitle("Main Screen");
        mainScreen.setScene(new Scene(MainScreen));
    }

}
