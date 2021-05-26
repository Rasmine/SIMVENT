package SIMVENT.Controller;

import SIMVENT.INVENT.*;

import java.io.IOException;
import java.text.DecimalFormat;

import SIMVENT.Model.PredefinedPatientModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TrainingSessionCtrl {

    @FXML
    private Button startSimulationButton;

    @FXML
    private Button endTrainingSessionButton;

    @FXML
    private Text VT_Current;

    @FXML
    private Text F_Current;

    @FXML
    private Text FiO2_Current;

    @FXML
    private Text PEEP_Current;

    @FXML
    private Text IE_Current;

    @FXML
    private TextField VT_Adjust;

    @FXML
    private TextField F_Adjust;

    @FXML
    private TextField FIO2_Adjust;

    @FXML
    private TextField PEEP_Adjust;

    @FXML
    private TextField IE_Adjust;

    @FXML
    private Text SaO2_Current;

    @FXML
    private Text PaO2_Current;

    @FXML
    private Text PaCO2_Current;

    @FXML
    private Text pHa_Current;

    @FXML
    private Text baseEx_Current;

    @FXML
    private Text SaO2_Previous;

    @FXML
    private Text PaO2_Previous;

    @FXML
    private Text PaCO2_Previous;

    @FXML
    private Text pHa_Previous;

    @FXML
    private Text baseEx_Previous;

    @FXML
    private Text PIP_Current;

    @FXML
    private Text PIP_Previous;

    @FXML
    private Text COHb;

    @FXML
    private Text MetHb;

    @FXML
    private Text Hb;

    @FXML
    private Text VO2;

    @FXML
    private Text VCO2;

    @FXML
    private Text Vd;

    @FXML
    private Text shunt;

    @FXML
    private Text Ci;

    private static PredefinedPatientModel chosenPatient;

    @FXML // Denne metode kører når PatientLibraryView instantieres
    private void initialize() {
        setChosenPatient(PatientLibraryCtrl.getChosenPatient());
        displayInitialSettings(chosenPatient);
        displayCurrentVariables(chosenPatient);
        displayParameters(chosenPatient);
        
    }

    //Denne metode bruges til at opdatere attributten chosenPatient 
    private static void setChosenPatient(PredefinedPatientModel patient) {
        TrainingSessionCtrl.chosenPatient = patient;
    }

    //Denne metode bruges af EvaluationCtrl og OptimalValuesCtrl, til at få fat i chosenPatient
    public static PredefinedPatientModel getChosenPatient() {
        return chosenPatient;
    }

    // Denne metode sætter den valgte patients settings som current
    private void displayInitialSettings(PredefinedPatientModel patient) {
        VT_Current.setText(String.valueOf(patient.getVT_value()*1000));
        F_Current.setText(String.valueOf(patient.getfreq_value()));
        FiO2_Current.setText(String.valueOf(patient.getFiO2_value()*100));
        PEEP_Current.setText(String.valueOf(patient.getPEEP_value()));
        IE_Current.setText(String.valueOf(patient.getIE_value()));
    }

    // Denne metode sætter den valgte patients variabler som current
    private void displayCurrentVariables(PredefinedPatientModel patient) {
        DecimalFormat df = new DecimalFormat("###.###");
        double SaO2 = patient.getSaO2_value()*100;
        String komma = df.format(SaO2);
        String punktum = komma.replace(',','.');
        SaO2_Current.setText(punktum);

        double PaO2 = patient.getPaO2_value();
        komma = df.format(PaO2);
        punktum = komma.replace(',','.');
        PaO2_Current.setText(punktum);

        double PaCO2 = patient.getPaCO2_value();
        komma = df.format(PaCO2);
        punktum = komma.replace(',','.');
        PaCO2_Current.setText(punktum);

        double pHa = patient.getpHa_value();
        komma = df.format(pHa);
        punktum = komma.replace(',','.');
        pHa_Current.setText(punktum);

        double baseEx = patient.getbaseEx_value();
        komma = df.format(baseEx);
        punktum = komma.replace(',','.');
        baseEx_Current.setText(punktum);

        double PIP = patient.getPIP_value();
        komma = df.format(PIP);
        punktum = komma.replace(',','.');
        PIP_Current.setText(punktum);
    }

    // Denne metode sætter den valgte patients parametre som current
    private void displayParameters(PredefinedPatientModel patient) {
        Vd.setText(String.valueOf(patient.getVd_value()));
        Ci.setText(String.valueOf(patient.getCi_value()));
        Hb.setText(String.valueOf(patient.getHb_value()));
        COHb.setText(String.valueOf(patient.getCOHb_value()));
        MetHb.setText(String.valueOf(patient.getMetHb_value()));
        VO2.setText(String.valueOf(patient.getVO2_value()));
        VCO2.setText(String.valueOf(patient.getVCO2_value()));
        shunt.setText(String.valueOf(patient.getShunt_value()));
    }

    @FXML //Denne metode bruges når useren trykker på "Simulate"
    void handleStartSimulationButton(ActionEvent event) {
        displayPreviousVariables();
        Settings settings = createSettings();
        PatientState predictedPatientState = predictVariables(settings);
        updateChosenPatient(predictedPatientState, settings);
        displayCurrentVariables(chosenPatient);


    }

    //Denne metode bruges til at sætte de current variable over i previous
    private void displayPreviousVariables() {
        String SaO2Current = SaO2_Current.getText();
        SaO2_Previous.setText(SaO2Current);
        String PaO2Current = PaO2_Current.getText();
        PaO2_Previous.setText(PaO2Current);
        String PaCO2Current = PaCO2_Current.getText();
        PaCO2_Previous.setText(PaCO2Current);
        String pHaCurrent = pHa_Current.getText();
        pHa_Previous.setText(pHaCurrent);
        String baseExCurrent = baseEx_Current.getText();
        baseEx_Previous.setText(baseExCurrent);
        String PIP = PIP_Current.getText();
        PIP_Previous.setText(PIP);
    }

    //opretter en Settings, opdaterer Current Settings og "tømmer" adjust to
    private Settings createSettings() { 
        double VT, f, FIO2, PEEP, IE;
        if (!VT_Adjust.getText().isEmpty()) {

             VT = Double.parseDouble(VT_Adjust.getText())/1000;
             VT_Current.setText(VT_Adjust.getText());
             VT_Adjust.setText("");
        } else {
             VT = chosenPatient.getVT_value();
        }

        if (!F_Adjust.getText().isEmpty()) {
             f = Double.parseDouble(F_Adjust.getText());
             F_Current.setText(F_Adjust.getText());
             F_Adjust.setText("");
        } else {
             f = chosenPatient.getfreq_value();
        }

        if (!FIO2_Adjust.getText().isEmpty()) {
             FIO2 = Double.parseDouble(FIO2_Adjust.getText())/100;
             FiO2_Current.setText(FIO2_Adjust.getText());
             FIO2_Adjust.setText("");
        } else {
            FIO2 = chosenPatient.getFiO2_value();
        }

        if (!PEEP_Adjust.getText().isEmpty()) {
             PEEP = Double.parseDouble(PEEP_Adjust.getText());
             PEEP_Current.setText(PEEP_Adjust.getText());
             PEEP_Adjust.setText("");
        } else {
             PEEP = chosenPatient.getPEEP_value();
        }

        if (!IE_Adjust.getText().isEmpty()) {
             IE = Double.parseDouble(IE_Adjust.getText());
             IE_Current.setText(IE_Adjust.getText());
             IE_Adjust.setText("");

        } else {
             IE = chosenPatient.getIE_value();
        }

        Settings settings = new Settings(f, FIO2, VT, PEEP, IE);
        return settings;
    }

    //Her bruges INVENT til at predicte en ny Patient state når der er indtastet nye settings
    private PatientState predictVariables(Settings settings) {
        Prediction prediction = new Prediction();
        PatientState patientState = createPatientState(chosenPatient);

        PatientState predictedPatientState = prediction.calculatePrediction(patientState, settings);
        return predictedPatientState;

    }

    //Her bruges oprettes en PatientState ud fra en PredefinedPatientModel
    private PatientState createPatientState(PredefinedPatientModel patient) {
        double freq = patient.getfreq_value();
        double FiO2 = patient.getFiO2_value();
        double Vt = patient.getVT_value();
        double Vd = patient.getVd_value();
        double peep = patient.getPEEP_value();
        double compliance = patient.getCi_value();
        double resistance = patient.getResistance_value();
        double DPG = patient.getDPG_value();
        double Hb = patient.getHb_value();
        double COHb = patient.getCOHb_value();
        double MetHb = patient.getMetHb_value();
        double Q = patient.getQ_value();
        double VO2 = patient.getVO2_value();
        double VCO2 = patient.getVCO2_value();
        double Temperature = patient.getTemp_value();
        double shunt = patient.getShunt_value();
        double FA2 = patient.getFA2_value();
        double PIP = patient.getPIP_value();

        PatientState patientState = new PatientState(freq, FiO2, Vt, Vd, peep, compliance, resistance, DPG, Hb, COHb,
                MetHb, Q, VO2, VCO2, Temperature, shunt, FA2,PIP);
        return patientState;
    }

    //Her opdateres chosenPatient ud fra de nye settings og de predictede variable
    private void updateChosenPatient(PatientState predictedPatientState, Settings settings) {
        
        TrainingSessionCtrl.chosenPatient.setVT_value(settings.getVT());
        TrainingSessionCtrl.chosenPatient.setfreq_value(settings.getF());
        TrainingSessionCtrl.chosenPatient.setFiO2_value(settings.getFIO2());
        TrainingSessionCtrl.chosenPatient.setPEEP_value(settings.getPEEP());
        TrainingSessionCtrl.chosenPatient.setIE_value(settings.getIE());

        TrainingSessionCtrl.chosenPatient.setSaO2_value(predictedPatientState.getSaO2());
        TrainingSessionCtrl.chosenPatient.setPaO2_value(predictedPatientState.getPaO2());
        TrainingSessionCtrl.chosenPatient.setPaCO2_value(predictedPatientState.getPaCO2());
        TrainingSessionCtrl.chosenPatient.setbaseEx_value(predictedPatientState.getBaseEx());
        TrainingSessionCtrl.chosenPatient.setpHa_value(predictedPatientState.getPHa());
        TrainingSessionCtrl.chosenPatient.setPIP_value(predictedPatientState.getPip());

        TrainingSessionCtrl.chosenPatient.setYourPatientState(predictedPatientState);
    }

    @FXML // Denne metode laver pop up når der trykkes på end training session knappen
    void handleEndTrainingSessionButton(ActionEvent event) throws Exception {
        FXMLLoader Loader = new FXMLLoader();
        Loader.setLocation(getClass().getResource("/EndTrainingSessionPopUpView.fxml"));
        try {
            Loader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Parent root = Loader.getRoot();
        Stage endTrainingSession = new Stage();
        endTrainingSession.setTitle("Ending Training Session");
        endTrainingSession.setScene(new Scene(root));
        endTrainingSession.initModality(Modality.APPLICATION_MODAL);
        endTrainingSession.show();
    }
}
