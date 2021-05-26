package SIMVENT.Controller;


import java.io.IOException;

import SIMVENT.Model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;

public class PatientInfoCtrl {

    @FXML
    private Button startTrainingSessionButton;

    @FXML
    private Text patientID;

    @FXML
    private Text VT_Value;

    @FXML
    private Text F_Value;

    @FXML
    private Text FiO2_Value;

    @FXML
    private Text PEEP_Value;

    @FXML
    private Text IE_Value;

    @FXML
    private Text SaO2_Value;

    @FXML
    private Text PaO2_Value;

    @FXML
    private Text pHa_Value;

    @FXML
    private Text baseEx_Value;

    @FXML
    private Text PaCO2_Value;

    @FXML
    private Text PIP_Value;

    @FXML
    private Label ParameterLabel1;

    @FXML
    private Text Vd_Value;

    @FXML
    private Text Ci_Value;

    @FXML
    private Text MetHb_Value;

    @FXML
    private Text COHb_Value;

    @FXML
    private Text Hb_Value;

    @FXML
    private Text VO2_Value;

    @FXML
    private Text VCO2_Value;

    @FXML
    private Text Shunt_Value;

    @FXML //Denne metode bruges når PatientInfoView instantieres
    public void initialize() {
        PredefinedPatientModel chosenPatient = PatientLibraryCtrl.getChosenPatient(); 
     displaySettings(chosenPatient);
     displayVariables(chosenPatient);
     displayParameters(chosenPatient);

    }
   
    //Denne metode bruges til at opdatere Settings i info-viewet
    private void displaySettings(PredefinedPatientModel chosenPatient){
        patientID.setText(chosenPatient.getPatientID());
        VT_Value.setText(String.valueOf(chosenPatient.getVT_value()*1000));
        F_Value.setText(String.valueOf(chosenPatient.getfreq_value()));
        FiO2_Value.setText(String.valueOf(chosenPatient.getFiO2_value()*100));
        PEEP_Value.setText(String.valueOf(chosenPatient.getPEEP_value()));
        IE_Value.setText(String.valueOf(chosenPatient.getIE_value()));
    }
    
    //Denne metode bruges til at opdatere variables i info-viewet
    private void displayVariables(PredefinedPatientModel chosenPatient){ 
        SaO2_Value.setText(String.valueOf(chosenPatient.getSaO2_value()*100));
        PaO2_Value.setText(String.valueOf(chosenPatient.getPaO2_value()));
        PaCO2_Value.setText(String.valueOf(chosenPatient.getPaCO2_value()));
        pHa_Value.setText(String.valueOf(chosenPatient.getpHa_value()));
        baseEx_Value.setText(String.valueOf(chosenPatient.getbaseEx_value()));
        IE_Value.setText(String.valueOf(chosenPatient.getIE_value()));
        PIP_Value.setText(String.valueOf(chosenPatient.getPIP_value()));
    }

    //Denne metode bruges til at opdatere parametrer i info-viewet
    private void displayParameters(PredefinedPatientModel chosenPatient){
        Vd_Value.setText(String.valueOf(chosenPatient.getVd_value()));
        Ci_Value.setText(String.valueOf(chosenPatient.getCi_value()));
        Hb_Value.setText(String.valueOf(chosenPatient.getHb_value()));
        COHb_Value.setText(String.valueOf(chosenPatient.getCOHb_value()));
        MetHb_Value.setText(String.valueOf(chosenPatient.getMetHb_value()));
        VO2_Value.setText(String.valueOf(chosenPatient.getVO2_value()));
        VCO2_Value.setText(String.valueOf(chosenPatient.getVCO2_value()));
        Shunt_Value.setText(String.valueOf(chosenPatient.getShunt_value()));
    }
    

    @FXML // Denne metode instantierer SimulationScreen, som styres af SimulationCtrl
    void handleStartTrainingSessionButton(ActionEvent event) throws IOException {
        Parent trainingSession = FXMLLoader.load(getClass().getResource("/TrainingSessionView.fxml"));
        Stage training = (Stage) startTrainingSessionButton.getScene().getWindow();
        training.setTitle("Training Session");
        training.setScene(new Scene(trainingSession));
    }
}