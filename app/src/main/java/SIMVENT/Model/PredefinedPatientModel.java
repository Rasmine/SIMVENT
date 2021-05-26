package SIMVENT.Model;

import SIMVENT.INVENT.*;


public class PredefinedPatientModel extends PatientModel {
    // General attributes
    private String patientID;
    private String patientDescription;
    private String patientLearningObjective;

    // Settings attributes
    private double VT_optimal;
    private double freq_optimal;
    private double FiO2_optimal;
    private double PEEP_optimal;
    private double IE_optimal;

    // Variables attributes
    private double SaO2_optimal;
    private double PaO2_optimal;
    private double PaCO2_optimal;
    private double pHa_optimal;
    private double baseEx_optimal;
    private double PIP_optimal;

    // Til at udregne penalty
    private PatientState yourPatientState;
    private double optimalPenalty;

    // Constructor, som også bruger constructoren brugt i superklassen
    public PredefinedPatientModel(String patientID, String patientDescription, double freq, double FiO2, double VT,
            double Vd, double PEEP, double Ci, double resistance, double DPG, double Hb, double COHb, double MetHb,
            double Q, double VO2, double VCO2, double temp, double shunt, double FA2, double IE, double SaO2,
            double PaO2, double PaCO2, double pHa, double baseEx, double VT_optimal, double freq_optimal,
            double FiO2_optimal, double PEEP_optimal, double IE_optimal, double SaO2_optimal, double PaO2_optimal,
            double PaCO2_optimal, double pHa_optimal, double baseEx_optimal, double optimalPenalty, double PIP, double PIP_optimal) {
        super(freq, FiO2, VT, Vd, PEEP, Ci, resistance, DPG, Hb, COHb, MetHb, Q, VO2, VCO2, temp, shunt, FA2, IE, SaO2,
                PaO2, PaCO2, pHa, baseEx, PIP);
        this.patientID = patientID;
        this.patientDescription = patientDescription;
        this.VT_optimal = VT_optimal;
        this.freq_optimal = freq_optimal;
        this.FiO2_optimal = FiO2_optimal;
        this.PEEP_optimal = PEEP_optimal;
        this.IE_optimal = IE_optimal;
        this.SaO2_optimal = SaO2_optimal;
        this.PaO2_optimal = PaO2_optimal;
        this.PaCO2_optimal = PaCO2_optimal;
        this.pHa_optimal = pHa_optimal;
        this.baseEx_optimal = baseEx_optimal;
        this.optimalPenalty = optimalPenalty;
        this.PIP_optimal = PIP_optimal;

        this.yourPatientState = new PatientState(freq, FiO2, VT, Vd, PEEP, Ci, resistance, DPG, Hb, COHb,
        MetHb, Q, VO2, VCO2, temp, shunt, FA2, PIP);

    }

    // Getters and setters
    public double getBaseEx_optimal() {
        return baseEx_optimal;
    }

    public void setBaseEx_optimal(double baseEx_optimal) {
        this.baseEx_optimal = baseEx_optimal;
    }

    public double getpHa_optimal() {
        return pHa_optimal;
    }

    public void setpHa_optimal(double pHa_optimal) {
        this.pHa_optimal = pHa_optimal;
    }

    public double getPaCO2_optimal() {
        return PaCO2_optimal;
    }

    public void setPaCO2_optimal(double paCO2_optimal) {
        this.PaCO2_optimal = paCO2_optimal;
    }

    public double getPaO2_optimal() {
        return PaO2_optimal;
    }

    public void setPaO2_optimal(double paO2_optimal) {
        this.PaO2_optimal = paO2_optimal;
    }

    public double getSaO2_optimal() {
        return SaO2_optimal;
    }

    public void setSaO2_optimal(double saO2_optimal) {
        this.SaO2_optimal = saO2_optimal;
    }

    public double getIE_optimal() {
        return IE_optimal;
    }

    public void setIE_optimal(double iE_optimal) {
        this.IE_optimal = iE_optimal;
    }

    public double getPEEP_optimal() {
        return PEEP_optimal;
    }

    public void setPEEP_optimal(double pEEP_optimal) {
        this.PEEP_optimal = pEEP_optimal;
    }

    public double getFiO2_optimal() {
        return FiO2_optimal;
    }

    public void setFiO2_optimal(double fiO2_optimal) {
        this.FiO2_optimal = fiO2_optimal;
    }

    public double getFreq_optimal() {
        return freq_optimal;
    }

    public void setFreq_optimal(double freq_optimal) {
        this.freq_optimal = freq_optimal;
    }

    public double getVT_optimal() {
        return VT_optimal;
    }

    public void setVT_optimal(double VT_optimal) {
        this.VT_optimal = VT_optimal;
    }

    public String getPatientID() {
        return patientID;
    }

    public String getPatientDescription() {
        return patientDescription;
    }

    public void setPatientLearningObjective(String learningObjective){
        this.patientLearningObjective = learningObjective;
    }

    public String getPatientLearningObjective (){
        return patientLearningObjective;
    }

    public void setYourPatientState(PatientState patientState) {
        this.yourPatientState = patientState;
    }

    public PatientState getYourPatientState() {
        return yourPatientState;
    }

   public double getOptimalPenalty(){
       return optimalPenalty;
   }
    
   public double getPIP_optimal(){
       return PIP_optimal;
   }

}
