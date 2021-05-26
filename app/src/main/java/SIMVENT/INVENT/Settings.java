package SIMVENT.INVENT;

/**
 * @(#)Settings.java
 *
 */
//package invent;

/**
 * This class is a simple container of values, that all together span a set of
 * settings. Note that this set is different in different ventilator modes
 * ie. volume controlled or pressure controled. This setting collection however
 * contains all possible settings.
 * 
 * First Author: 02gr1029
 * @author %$Author: dska02 $
 * @version %$Revision: 1.1 $ */

public class Settings
{
    private double VT, PIP, f, FIO2, PEEP, IE;

    public Settings()
    {
    }

    public Settings(Settings settingDNA)
    {
        this.VT=settingDNA.VT;
        this.PIP=settingDNA.PIP;
        this.f=settingDNA.f;
        this.FIO2=settingDNA.FIO2;
        this.PEEP=settingDNA.PEEP;
        this.IE=settingDNA.IE;        
    }

    public Settings(
        double f,
        double FIO2,
        double VT,
        double PEEP,
        double IE
        )
    {
        this.VT=VT;
        this.f=f;
        this.FIO2=FIO2;
        this.PEEP=PEEP;
        this.IE=IE;
    }


/**
 * Gets the value of vT
 *
 * @return the value of vT
 */
    public double getVT() 
    {
        return this.VT;
    }

/**
 * Sets the value of vT
 *
 * @param argVT Value to assign to this.vT
 */
    public void setVT(double argVT)
    {
        this.VT = argVT;
    }

/**
 * Gets the value of pIP
 *
 * @return the value of pIP
 */
    public double getPIP() 
    {
        return this.PIP;
    }

/**
 * Sets the value of pIP
 *
 * @param argPIP Value to assign to this.pIP
 */
    public void setPIP(double argPIP)
    {
        this.PIP = argPIP;
    }

/**
 * Gets the value of f
 *
 * @return the value of f
 */
    public double getF() 
    {
        return this.f;
    }

/**
 * Sets the value of f
 *
 * @param argF Value to assign to this.f
 */
    public void setF(double argF)
    {
        this.f = argF;
    }

/**
 * Gets the value of fIO2
 *
 * @return the value of fIO2
 */
    public double getFIO2() 
    {
        return this.FIO2;
    }

/**
 * Sets the value of fIO2
 *
 * @param argFIO2 Value to assign to this.fIO2
 */
    public void setFIO2(double argFIO2)
    {
        this.FIO2 = argFIO2;
    }

/**
 * Gets the value of pEEP
 *
 * @return the value of pEEP
 */
    public double getPEEP() 
    {
        return this.PEEP;
    }

/**
 * Sets the value of pEEP
 *
 * @param argPEEP Value to assign to this.pEEP
 */
    public void setPEEP(double argPEEP)
    {
        this.PEEP = argPEEP;
    }

/**
 * Gets the value of iE
 *
 * @return the value of iE
 */
    public double getIE() 
    {
        return this.IE;
    }

/**
 * Sets the value of iE
 *
 * @param argIE Value to assign to this.iE
 */
    public void setIE(double argIE)
    {
        this.IE = argIE;
    }

}
