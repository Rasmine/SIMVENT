package SIMVENT.INVENT;

/*
 * @(#)Penalties.java
 *
 */
//package invent;
import java.io.*;


/**
 * This class contains the table to consult when calculating penalties for
 * ventilator settings. It also hold variables to express one set of penalties. It
 * can be invoked by a patientState and thereby calculating penalties to start with.
 *
 * The tables are directly adopted from Stephen Rees Matlab INVENT code.
 *
 * First Author: 02gr1029
 * @author %$Author: dska02 $
 * @version %$Revision: 1.2 $ */

public class Penalties
{
    private double baro, atel, acid, oxy, o2toxy;
    private double total;
    
    public Penalties()
    {
    }
    
    /**
     * Creates a new <code>Penalties</code> instance. The calculation of penalties
     * is done at once, if the PatientState object is not null.
     *
     * @param pState a <code>PatientState</code> value */
    public Penalties(PatientState pState)
    {
        double tmp;
        if (pState != null)
            tmp=calcPenalties(pState);
        else
            tmp=20;
    }
    
   /* public Penalties(double baro, double acid, double oxy, double o2toxy)
    {
        this.baro=baro;
        //this.atel=atel;
        this.acid=acid;
        this.oxy=oxy;
        this.o2toxy=o2toxy;
        this.total=baro+acid+oxy+o2toxy;
    }*/
    
    public double calcBaro(double PEIP, double Fresp)
    {
        double baro;
        if (PEIP <= 20)
        {
            if (Fresp >15)
            {	baro = 0.0004*Math.pow(Fresp, 2.0) - 0.0053 * Fresp - 0.0105;
                
            }
            else
            {
                baro=0;
            }
            
        }
        else
        {
            if (Fresp > 15)
            {
                baro =  ( (0.000574*Math.pow(PEIP, 2.0) - 0.009828 * PEIP - 0.03304) * Fresp / 15) + (0.0004*Math.pow(Fresp, 2.0) - 0.0053 * Fresp - 0.0105);
                
            }
            else
            {
                baro =  ( (0.000574*Math.pow(PEIP, 2.0) - 0.009828 * PEIP - 0.03304) * Fresp / 15);
            }
        }
        
        return baro;
    }
    
    
    public double calcAtel(double Vt)
    {
        double atel;
        double LDa;
        
        //linear interpolation:
        if (5.0<=Vt)
        {LDa=0.0;}
        else if ((5.0>=Vt)&(Vt>0.5))
        {LDa=(-(0.00/4.5)*(Vt));}
        else if ((0.5>=Vt)&(Vt>0.46))
        {LDa=(-(0.02/0.04)*(Vt-0.5));}
        else if ((0.46>=Vt)&(Vt>0.4))
        {LDa=(-(0.03/0.06)*(Vt-0.46))+0.02;}
        else if ((0.4>=Vt)&(Vt>0.35))
        {LDa=(-(0.10/0.05)*(Vt-0.40))+0.05;}
        else if ((0.35>=Vt)&(Vt>0.3))
        {LDa=(-(0.22/0.05)*(Vt-0.35))+0.15;}
        else if ((0.3>=Vt)&(Vt>0.28))
        {LDa=(-(0.30/0.02)*(Vt-0.30))+0.37;}
        else if ((0.28>=Vt)&(Vt>0.24))
        {LDa=(-(0.95/0.04)*(Vt-0.28))+0.67;}
        else if ((0.24>=Vt)&(Vt>0.2))
        {LDa=(-(2.08/0.04)*(Vt-0.24))+1.62;}
        else if ((0.2>=Vt)&(Vt>0.15))
        {LDa=(-(2.00/0.05)*(Vt-0.20))+3.7;}
        else if ((0.15>=Vt)&(Vt>0.1))
        {LDa=(-(4.30/0.05)*(Vt-0.15))+5.7;}
        else if (0.1>=Vt)
        {LDa=10.0;}
        else
        {LDa=10.0;}
        
        atel= LDa;
        return atel;
    }
    
    
    public double calcAcid(double phmv)
    {
        double acid;
        acid =  20.5*Math.pow((phmv - 7.36),2.0);
        return acid;
    }
    
    
    public double calcOxy(double sO2vIn,double sO2aIn)
    {
        double oxyv;
        double oxya;
        
        System.out.println(" input oxygen sat "+sO2aIn);
        
        if (sO2vIn <= 0.6)
        {
            oxyv =  7*Math.pow(sO2vIn,2.0)  - 8.5*sO2vIn  + 2.6;
        }
        else if ((sO2vIn>0.6)&(sO2vIn<0.7))
        {
            oxyv =  ((0.7-sO2vIn)/0.1) * (7*Math.pow(0.6,2.0) - 8.5 * 0.6  + 2.6);
        }
        else
        {
            oxyv = 0;
        }
        
        
        // arterial contribution,
        if (sO2aIn <= 0.98)
        {
            oxya =  26.84*Math.pow((sO2aIn-0.90),2.0)  - 4.22*(sO2aIn-0.90)  + 0.1658;
            System.out.println("oxya 1 "+oxya);
            
        }
        else
        {
            oxya = 0;
            System.out.println("oxya 3 "+oxya);
        }
        
        oxy=oxya+oxyv;
        System.out.println("oxy  "+oxy);
        
        return oxy;
    }
    
    
    public double calcO2toxy(double FIO2)
    {
        double toxy;
        if (FIO2<=0.21)
        {
            toxy=0;
        }
        else
        {
            toxy = 0.7243* Math.pow(FIO2,2.0) - 0.248 * FIO2 + 0.02013837;
        }
        return toxy;
    }
    
    
    /**
     * Calculates the total penalty by invoking all penalty methods. The sum of all
     * the penalties are returned, unless the PatientState object is null in which
     * case INFINITY is returned.
     *
     * @param pState a <code>PatientState</code> value
     * @return a <code>double</code> value */
    public double calcPenalties(PatientState pState)
    {
        double retur;
        if (pState != null)
        {
            this.baro=calcBaro(pState.getPip(),pState.getFreq());
            this.atel= 0.0; 
            this.acid=calcAcid(pState.getPHmv());
            this.oxy=calcOxy(pState.getSmvO2(),pState.getSaO2());
            this.o2toxy=calcO2toxy(pState.getFiO2());
            retur = calcTotal();
        }
        else
            retur = 20;
        
        return retur;
    }
    
    public double calcTotal()
    {
        this.total=this.baro+this.acid+this.oxy+this.o2toxy;
        return this.total;
    }
    
    //Set and Getz: (Stan)
    public void setBaro(double obj)
    {this.baro=obj;}
    public double getBaro()
    {return this.baro;}
    
    public void setAtel(double obj)
    {this.atel=obj;}
    public double getAtel()
    {return this.atel;}
    
    public void setAcid(double obj)
    {this.acid=obj;}
    public double getAcid()
    {return this.acid;}
    
    public void setOxy(double obj)
    {this.oxy=obj;}
    public double getOxy()
    {return this.oxy;}
    
    public void setO2toxy(double obj)
    {this.o2toxy=obj;}
    public double getO2toxy()
    {return this.o2toxy;}
    
    public void setTotal(double obj)
    {this.total=obj;}
    
    /**
     * Gets the total penalty stored in the Penalty object.
     *
     * @return a <code>double</code> value
     */
    public double getTotal()
    {return this.total;}
}