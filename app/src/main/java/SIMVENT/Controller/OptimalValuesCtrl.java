package SIMVENT.Controller;

import java.text.DecimalFormat;

import SIMVENT.Model.PredefinedPatientModel;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class OptimalValuesCtrl {

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
    private Text PaO2_Adjusted;

    @FXML
    private Text SaO2_Adjusted;

    @FXML
    private Text PaCO2_Adjusted;

    @FXML
    private Text baseEx_Adjusted;

    @FXML
    private Text pHa_Adjusted;

    @FXML
    private Text SaO2_Optimal;

    @FXML
    private Text PaO2_Optimal;

    @FXML
    private Text PaCO2_Optimal;

    @FXML
    private Text baseEx_Optimal;

    @FXML
    private Text pHa_Optimal;

    @FXML
    private Text COHb_Current;
    @FXML
    private Text PIP_Optimal;

    @FXML
    private Text PIP_Adjusted;

    @FXML
    private Text MetHb_Current;

    @FXML
    private Text Hb_Current;

    @FXML
    private Text VO2_Current;

    @FXML
    private Text VCO2_Current;

    @FXML
    private Text Vd_Current;

    @FXML
    private Text shunt_Current;

    @FXML
    private Text Ci_Current;

    @FXML // Denne metode kører når OptimalValuesView instantieres
    private void initialize() {
        PredefinedPatientModel patient = TrainingSessionCtrl.getChosenPatient();
        displayPatientParameters(patient);
        displayOptimalSettings(patient);
        displayOptimalVariables(patient);
        displayYourSettings(patient);
        displayYourVariable(patient);
    }

 // Denne metode indsætter den valgte patients parametre i viewet
private void displayPatientParameters(PredefinedPatientModel patient){
    Vd_Current.setText(String.valueOf(patient.getVd_value()));
    Ci_Current.setText(String.valueOf(patient.getCi_value()));
    Hb_Current.setText(String.valueOf(patient.getHb_value()));
    COHb_Current.setText(String.valueOf(patient.getCOHb_value()));
    MetHb_Current.setText(String.valueOf(patient.getMetHb_value()));
    VO2_Current.setText(String.valueOf(patient.getVO2_value()));
    VCO2_Current.setText(String.valueOf(patient.getVCO2_value()));
    shunt_Current.setText(String.valueOf(patient.getShunt_value()));
    }

 // Denne metode indsætter den valgte patients optimale settings i viewet
private void displayOptimalSettings(PredefinedPatientModel patient){ 
    VT_Optimal.setText(String.valueOf(patient.getVT_optimal()*1000));
    F_Optimal.setText(String.valueOf(patient.getFreq_optimal()));
    FiO2_Optimal.setText(String.valueOf(patient.getFiO2_optimal()));
    PEEP_Optimal.setText(String.valueOf(patient.getPEEP_optimal()));
    IE_Optimal.setText(String.valueOf(patient.getIE_optimal()));
    }

// Denne metode indsætter den valgte patients optimale varible i viewet
private void displayOptimalVariables(PredefinedPatientModel patient){
    SaO2_Optimal.setText(String.valueOf(patient.getSaO2_optimal()*100));
    PaO2_Optimal.setText(String.valueOf(patient.getPaO2_optimal()));
    PaCO2_Optimal.setText(String.valueOf(patient.getPaCO2_optimal()));
    baseEx_Optimal.setText(String.valueOf(patient.getBaseEx_optimal()));
    pHa_Optimal.setText(String.valueOf(patient.getpHa_optimal()));
    PIP_Optimal.setText(String.valueOf(patient.getPIP_optimal()));
    }

//Denne metode indsætter de adjustede settings i viewet
private void displayYourSettings(PredefinedPatientModel patient){
    VT_Adjusted.setText(String.valueOf(patient.getVT_value()*1000));
    F_Adjusted.setText(String.valueOf(patient.getfreq_value()));
    FiO2_Adjusted.setText(String.valueOf(patient.getFiO2_value()*100));
    PEEP_Adjusted.setText(String.valueOf(patient.getPEEP_value()));
    IE_Adjusted.setText(String.valueOf(patient.getIE_value()));
}

//Denne metode indsætter de variable, som passer til de adjusted settings, i viewet
private void displayYourVariable(PredefinedPatientModel patient){
    DecimalFormat df = new DecimalFormat("###.###");
        double SaO2 = patient.getSaO2_value()*100;
        String komma = df.format(SaO2);
        String punktum = komma.replace(',','.');
        SaO2_Adjusted.setText(punktum);

        double PaO2 = patient.getPaO2_value();
        String komma1 = df.format(PaO2);
        String punktum1 = komma1.replace(',','.');
        PaO2_Adjusted.setText(punktum1);

        double PaCO2 = patient.getPaCO2_value();
        String komma2 = df.format(PaCO2);
        String punktum2 = komma2.replace(',','.');
        PaCO2_Adjusted.setText(punktum2);

        double pHa = patient.getpHa_value();
        String komma3 = df.format(pHa);
        String punktum3 = komma3.replace(',','.');
        pHa_Adjusted.setText(punktum3);

        double baseEx = patient.getbaseEx_value();
        String komma4 = df.format(baseEx);
        String punktum4 = komma4.replace(',','.');
        baseEx_Adjusted.setText(punktum4);

        double PIP = patient.getPIP_value();
        String komma5 = df.format(PIP);
        String punktum5 = komma5.replace(',','.');
        PIP_Adjusted.setText(punktum5);
}
}