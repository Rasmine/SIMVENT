package SIMVENT.INVENT;

/**
 * @(#)PatientState.java
 *
 */
//package invent;

/**
 * This class contains a ... patientState! Thus it is quite similar to the
 * repository DataObj, but this one only hold double values instead of rank and all
 * that stuff. It is intended for quick access by calculating algorithms.
 *
 * First Author: 02gr1029
 * @author %$Author: dska02 $
 * @version %$Revision: 1.2 $ */

public class PatientState
{
    private double freq, FiO2, Vt, Vd, fetco2, feto2, FO2mix, feco2, pip, peep, compliance, resistance;
    private double SaO2, PaO2, PaCO2, pHa;
    private double SmvO2, PmvO2, PmvCO2, pHmv;
    private double ScvO2, PcvO2, PcvCO2, pHcv;
    private double DPG, Hb, COHb, MetHb;
    private double Q, VO2, VCO2, Temperature, baseEx;
    private double Rval, shunt, FA2;

    private double h = 24; //The hypothetically number of hours, the inspired oxygen fraction is going to be held in.

    public PatientState()                                                                     
    {                                                                                         
    }                                                                                         

    public PatientState(
        double freq, 
        double FiO2, 
        double Vt, 
        double Vd, 
        double peep,
        double compliance, 
        double resistance, 
        double DPG, 
        double Hb, 
        double COHb, 
        double MetHb,
        double Q, 
        double VO2, 
        double VCO2, 
        double Temperature,
        double shunt, 
        double FA2,
        double PIP
        )
    {
        this.freq=freq; 
        this.FiO2=FiO2; 
        this.Vt=Vt; 
        this.Vd=Vd; 
        this.peep=peep;
        this.compliance=compliance; 
        this.resistance=resistance;
        this.DPG=DPG; 
        this.Hb=Hb; 
        this.COHb=COHb; 
        this.MetHb=MetHb;
        this.Q=Q; 
        this.VO2=VO2; 
        this.VCO2=VCO2; 
        this.Temperature=Temperature;
        this.shunt=shunt; 
        this.FA2=FA2;
        this.pip=PIP;
    }

/**
 * Gets the value of h
 *
 * @return the value of h
 */
        public double getH() 
    {
        return this.h;
    }

/**
 * Sets the value of h
 *
 * @param argH Value to assign to this.h
 */
        public void setH(double argH)
    {
        this.h = argH;
    }

/**
 * Gets the value of freq
 *
 * @return the value of freq
 */
    public double getFreq() 
    {
        return this.freq;
    }

/**
 * Sets the value of freq
 *
 * @param argFreq Value to assign to this.freq
 */
    public void setFreq(double argFreq)
    {
        this.freq = argFreq;
    }

/**
 * Gets the value of fiO2
 *
 * @return the value of fiO2
 */
    public double getFiO2() 
    {
        return this.FiO2;
    }

/**
 * Sets the value of fiO2
 *
 * @param argFiO2 Value to assign to this.fiO2
 */
    public void setFiO2(double argFiO2)
    {
        this.FiO2 = argFiO2;
    }

/**
 * Gets the value of vte
 *
 * @return the value of vte
 */
    public double getVt() 
    {
        return this.Vt;
    }

/**
 * Sets the value of vte
 *
 * @param argVte Value to assign to this.vte
 */
    public void setVt(double argVt)
    {
        this.Vt = argVt;
    }

/**
 * Gets the value of vd
 *
 * @return the value of vd
 */
    public double getVd() 
    {
        return this.Vd;
    }

/**
 * Sets the value of vd
 *
 * @param argVd Value to assign to this.vd
 */
    public void setVd(double argVd)
    {
        this.Vd = argVd;
    }

/**
 * Gets the value of fetco2
 *
 * @return the value of fetco2
 */
    public double getFCO2et() 
    {
        return this.fetco2;
    }

/**
 * Sets the value of fetco2
 *
 * @param argFCO2et Value to assign to this.fetco2
 */
    public void setFCO2et(double argFCO2et)
    {
        this.fetco2 = argFCO2et;
    }

/**
 * Gets the value of feto2
 *
 * @return the value of feto2
 */
    public double getFO2et() 
    {
        return this.feto2;
    }

/**
 * Sets the value of feto2
 *
 * @param argFO2et Value to assign to this.feto2
 */
    public void setFO2et(double argFO2et)
    {
        this.feto2 = argFO2et;
    }

/**
 * Gets the value of fO2mix
 *
 * @return the value of fO2mix
 */
    public double getFO2mix() 
    {
        return this.FO2mix;
    }

/**
 * Sets the value of fO2mix
 *
 * @param argFO2mix Value to assign to this.fO2mix
 */
    public void setFO2mix(double argFO2mix)
    {
        this.FO2mix = argFO2mix;
    }

/**
 * Gets the value of feco2
 *
 * @return the value of feco2
 */
    public double getFCO2mix() 
    {
        return this.feco2;
    }

/**
 * Sets the value of feco2
 *
 * @param argFCO2mix Value to assign to this.feco2
 */
    public void setFCO2mix(double argFCO2mix)
    {
        this.feco2 = argFCO2mix;
    }

/**
 * Gets the value of pip
 *
 * @return the value of pip
 */
    public double getPip() 
    {
        return this.pip;
    }

/**
 * Sets the value of pip
 *
 * @param argPip Value to assign to this.pip
 */
    public void setPip(double argPip)
    {
        this.pip = argPip;
    }

/**
 * Gets the value of peep
 *
 * @return the value of peep
 */
    public double getPeep() 
    {
        return this.peep;
    }

/**
 * Sets the value of peep
 *
 * @param argPeep Value to assign to this.peep
 */
    public void setPeep(double argPeep)
    {
        this.peep = argPeep;
    }

/**
 * Gets the value of compliance
 *
 * @return the value of compliance
 */
    public double getCompliance() 
    {
        return this.compliance;
    }

/**
 * Sets the value of compliance
 *
 * @param argCompliance Value to assign to this.compliance
 */
    public void setCompliance(double argCompliance)
    {
        this.compliance = argCompliance;
    }

/**
 * Gets the value of resistance
 *
 * @return the value of resistance
 */
    public double getResistance() 
    {
        return this.resistance;
    }

/**
 * Sets the value of resistance
 *
 * @param argResistance Value to assign to this.resistance
 */
    public void setResistance(double argResistance)
    {
        this.resistance = argResistance;
    }

/**
 * Gets the value of saO2
 *
 * @return the value of saO2
 */
    public double getSaO2() 
    {
        return this.SaO2;
    }

/**
 * Sets the value of saO2
 *
 * @param argSaO2 Value to assign to this.saO2
 */
    public void setSaO2(double argSaO2)
    {
        this.SaO2 = argSaO2;
    }

/**
 * Gets the value of paO2
 *
 * @return the value of paO2
 */
    public double getPaO2() 
    {
        return this.PaO2;
    }

/**
 * Sets the value of paO2
 *
 * @param argPaO2 Value to assign to this.paO2
 */
    public void setPaO2(double argPaO2)
    {
        this.PaO2 = argPaO2;
    }

/**
 * Gets the value of paCO2
 *
 * @return the value of paCO2
 */
    public double getPaCO2() 
    {
        return this.PaCO2;
    }

/**
 * Sets the value of paCO2
 *
 * @param argPaCO2 Value to assign to this.paCO2
 */
    public void setPaCO2(double argPaCO2)
    {
        this.PaCO2 = argPaCO2;
    }

/**
 * Gets the value of pHa
 *
 * @return the value of pHa
 */
    public double getPHa() 
    {
        return this.pHa;
    }

/**
 * Sets the value of pHa
 *
 * @param argPHa Value to assign to this.pHa
 */
    public void setPHa(double argPHa)
    {
        this.pHa = argPHa;
    }

/**
 * Gets the value of smvO2
 *
 * @return the value of smvO2
 */
    public double getSmvO2() 
    {
        return this.SmvO2;
    }

/**
 * Sets the value of smvO2
 *
 * @param argSmvO2 Value to assign to this.smvO2
 */
    public void setSmvO2(double argSmvO2)
    {
        this.SmvO2 = argSmvO2;
    }

/**
 * Gets the value of pmvO2
 *
 * @return the value of pmvO2
 */
    public double getPmvO2() 
    {
        return this.PmvO2;
    }

/**
 * Sets the value of pmvO2
 *
 * @param argPmvO2 Value to assign to this.pmvO2
 */
    public void setPmvO2(double argPmvO2)
    {
        this.PmvO2 = argPmvO2;
    }

/**
 * Gets the value of pmvCO2
 *
 * @return the value of pmvCO2
 */
    public double getPmvCO2() 
    {
        return this.PmvCO2;
    }

/**
 * Sets the value of pmvCO2
 *
 * @param argPmvCO2 Value to assign to this.pmvCO2
 */
    public void setPmvCO2(double argPmvCO2)
    {
        this.PmvCO2 = argPmvCO2;
    }

/**
 * Gets the value of pHmv
 *
 * @return the value of pHmv
 */
    public double getPHmv() 
    {
        return this.pHmv;
    }

/**
 * Sets the value of pHmv
 *
 * @param argPHmv Value to assign to this.pHmv
 */
    public void setPHmv(double argPHmv)
    {
        this.pHmv = argPHmv;
    }

/**
 * Gets the value of scvO2
 *
 * @return the value of scvO2
 */
    public double getScvO2() 
    {
        return this.ScvO2;
    }

/**
 * Sets the value of scvO2
 *
 * @param argScvO2 Value to assign to this.scvO2
 */
    public void setScvO2(double argScvO2)
    {
        this.ScvO2 = argScvO2;
    }

/**
 * Gets the value of pcvO2
 *
 * @return the value of pcvO2
 */
    public double getPcvO2() 
    {
        return this.PcvO2;
    }

/**
 * Sets the value of pcvO2
 *
 * @param argPcvO2 Value to assign to this.pcvO2
 */
    public void setPcvO2(double argPcvO2)
    {
        this.PcvO2 = argPcvO2;
    }

/**
 * Gets the value of pcvCO2
 *
 * @return the value of pcvCO2
 */
    public double getPcvCO2() 
    {
        return this.PcvCO2;
    }

/**
 * Sets the value of pcvCO2
 *
 * @param argPcvCO2 Value to assign to this.pcvCO2
 */
    public void setPcvCO2(double argPcvCO2)
    {
        this.PcvCO2 = argPcvCO2;
    }

/**
 * Gets the value of pHcv
 *
 * @return the value of pHcv
 */
    public double getPHcv() 
    {
        return this.pHcv;
    }

/**
 * Sets the value of pHcv
 *
 * @param argPHcv Value to assign to this.pHcv
 */
    public void setPHcv(double argPHcv)
    {
        this.pHcv = argPHcv;
    }

/**
 * Gets the value of dPG
 *
 * @return the value of dPG
 */
    public double getDPG() 
    {
        return this.DPG;
    }

/**
 * Sets the value of dPG
 *
 * @param argDPG Value to assign to this.dPG
 */
    public void setDPG(double argDPG)
    {
        this.DPG = argDPG;
    }

/**
 * Gets the value of hb
 *
 * @return the value of hb
 */
    public double getHb() 
    {
        return this.Hb;
    }

/**
 * Sets the value of hb
 *
 * @param argHb Value to assign to this.hb
 */
    public void setHb(double argHb)
    {
        this.Hb = argHb;
    }

/**
 * Gets the value of cOHb
 *
 * @return the value of cOHb
 */
    public double getCOHb() 
    {
        return this.COHb;
    }

/**
 * Sets the value of cOHb
 *
 * @param argCOHb Value to assign to this.cOHb
 */
    public void setCOHb(double argCOHb)
    {
        this.COHb = argCOHb;
    }

/**
 * Gets the value of metHb
 *
 * @return the value of metHb
 */
    public double getMetHb() 
    {
        return this.MetHb;
    }

/**
 * Sets the value of metHb
 *
 * @param argMetHb Value to assign to this.metHb
 */
    public void setMetHb(double argMetHb)
    {
        this.MetHb = argMetHb;
    }

/**
 * Gets the value of q
 *
 * @return the value of q
 */
    public double getQ() 
    {
        return this.Q;
    }

/**
 * Sets the value of q
 *
 * @param argQ Value to assign to this.q
 */
    public void setQ(double argQ)
    {
        this.Q = argQ;
    }

/**
 * Gets the value of vO2
 *
 * @return the value of vO2
 */
    public double getVO2() 
    {
        return this.VO2;
    }

/**
 * Sets the value of vO2
 *
 * @param argVO2 Value to assign to this.vO2
 */
    public void setVO2(double argVO2)
    {
        this.VO2 = argVO2;
    }

/**
 * Gets the value of vCO2
 *
 * @return the value of vCO2
 */
    public double getVCO2() 
    {
        return this.VCO2;
    }

/**
 * Sets the value of vCO2
 *
 * @param argVCO2 Value to assign to this.vCO2
 */
    public void setVCO2(double argVCO2)
    {
        this.VCO2 = argVCO2;
    }

/**
 * Gets the value of temperature
 *
 * @return the value of temperature
 */
    public double getTemperature() 
    {
        return this.Temperature;
    }

/**
 * Sets the value of temperature
 *
 * @param argTemperature Value to assign to this.temperature
 */
    public void setTemperature(double argTemperature)
    {
        this.Temperature = argTemperature;
    }

/**
 * Gets the value of baseEx
 *
 * @return the value of baseEx
 */
    public double getBaseEx() 
    {
        return this.baseEx;
    }

/**
 * Sets the value of baseEx
 *
 * @param argBaseEx Value to assign to this.baseEx
 */
    public void setBaseEx(double argBaseEx)
    {
        this.baseEx = argBaseEx;
    }

/**
 * Gets the value of rval
 *
 * @return the value of rval
 */
    public double getRval() 
    {
        return this.Rval;
    }

/**
 * Sets the value of rval
 *
 * @param argRval Value to assign to this.rval
 */
    public void setRval(double argRval)
    {
        this.Rval = argRval;
    }

/**
 * Gets the value of shunt
 *
 * @return the value of shunt
 */
    public double getShunt() 
    {
        return this.shunt;
    }

/**
 * Sets the value of shunt
 *
 * @param argShunt Value to assign to this.shunt
 */
    public void setShunt(double argShunt)
    {
        this.shunt = argShunt;
    }

/**
 * Gets the value of fA2
 *
 * @return the value of fA2
 */
    public double getFA2() 
    {
        return this.FA2;
    }

/**
 * Sets the value of fA2
 *
 * @param argFA2 Value to assign to this.fA2
 */
    public void setFA2(double argFA2)
    {
        this.FA2 = argFA2;
    }

}
