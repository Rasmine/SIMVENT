package SIMVENT.Model;

public abstract class PatientModel {

    // Settings
    private double VT;
    private double freq;
    private double FiO2;
    private double PEEP;
    private double IE;

    // Variable
    private double SaO2;
    private double PaO2;
    private double PaCO2;
    private double pHa;
    private double baseEx;
    private double PIP;

    // Parametre
    private double Vd;
    private double Ci;
    private double resistance;
    private double DPG;
    private double Hb;
    private double COHb;
    private double MetHb;
    private double Q;
    private double VO2;
    private double VCO2;
    private double shunt;
    private double temp;
    private double FA2;

    // Constructor
    public PatientModel(double freq, double FiO2, double VT, double Vd, double PEEP, double Ci, double resistance,
            double DPG, double Hb, double COHb, double MetHb, double Q, double VO2, double VCO2, double temp,
            double shunt, double FA2, double IE, double SaO2, double PaO2, double PaCO2, double pHa, double baseEx, double PIP) {

        this.freq = freq;
        this.FiO2 = FiO2;
        this.VT = VT;
        this.Vd = Vd;
        this.PEEP = PEEP;
        this.Ci = Ci;
        this.resistance = resistance;
        this.DPG = DPG;
        this.Hb = Hb;
        this.COHb = COHb;
        this.MetHb = MetHb;
        this.Q = Q;
        this.VO2 = VO2;
        this.VCO2 = VCO2;
        this.temp = temp;
        this.shunt = shunt;
        this.FA2 = FA2;
        this.IE = IE;
        this.SaO2 = SaO2;
        this.PaO2 = PaO2;
        this.PaCO2 = PaCO2;
        this.pHa = pHa;
        this.baseEx = baseEx;
        this.PIP = PIP;
    }

    // Getters og setters - Settings
    public double getfreq_value() {
        return freq;
    }

    public void setfreq_value(double value) {
        this.freq = value;
    }

    public double getFiO2_value() {
        return FiO2;
    }
    public void setFiO2_value(double value) {
        this.FiO2 = value;
    }

    public double getVT_value() {
        return VT;
    }
    public void setVT_value(double value) {
        this.VT = value;
    }

    public double getPEEP_value() {
        return PEEP;
    }
    public void setPEEP_value(double value) {
        this.PEEP = value;
    }

    public double getIE_value() {
        return IE;
    }
    public void setIE_value(double value) {
        this.IE = value;
    }

    // Getters og setters - Variable
    public double getSaO2_value() {
        return SaO2;
    }
    public void setSaO2_value(double value) {
        this.SaO2 = value;
    }

    public double getPaO2_value() {
        return PaO2;
    }
    public void setPaO2_value(double value) {
        this.PaO2 = value;
    }

    public double getPaCO2_value() {
        return PaCO2;
    }
    public void setPaCO2_value(double value) {
        this.PaCO2 = value;
    }

    public double getpHa_value() {
        return pHa;
    }
    public void setpHa_value(double value) {
        this.pHa = value;
    }

    public double getbaseEx_value() {
        return baseEx;
    }
    public void setbaseEx_value(double value) {
        this.baseEx = value;
    }
    public double getPIP_value() {
        return PIP;
    }
    public void setPIP_value(double value) {
        this.PIP = value;
    }

    // Getters og setters - Parametre
    public double getVd_value() {
        return Vd;
    }
    public void setVd_value(double value) {
        this.Vd = value;
    }

    public double getCi_value() {
        return Ci;
    }
    public void setCi_value(double value) {
        this.Ci = value;
    }

    public double getResistance_value() {
        return resistance;
    }
    public void setResistance_value(double value) {
        this.resistance = value;
    }

    public double getDPG_value() {
        return DPG;
    }
    public void setDPG_value(double value) {
        this.DPG = value;
    }

    public double getHb_value() {
        return Hb;
    }
    public void setHb_value(double value) {
        this.Hb = value;
    }

    public double getCOHb_value() {
        return COHb;
    }
    public void setCOHb_value(double value) {
        this.COHb = value;
    }

    public double getMetHb_value() {
        return MetHb;
    }
    public void setMetHb_value(double value) {
        this.MetHb = value;
    }

    public double getQ_value() {
        return Q;
    }
    public void setQ_value(double value) {
        this.Q = value;
    }

    public double getVO2_value() {
        return VO2;
    }
    public void setVO2_value(double value) {
        this.VO2 = value;
    }

    public double getVCO2_value() {
        return VCO2;
    }
    public void setVCO2_value(double value) {
        this.VCO2 = value;
    }

    public double getTemp_value() {
        return temp;
    }
    public void setTemp_value(double value) {
        this.temp = value;
    }

    public double getShunt_value() {
        return shunt;
    }
    public void setShunt_value(double value) {
        this.shunt = value;
    }

    public double getFA2_value() {
        return FA2;
    }
    public void setFA2_value(double value) {
        this.FA2 = value;
    }
}
