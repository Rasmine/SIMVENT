package SIMVENT.INVENT;

/*
 * ===================================================================
 *  FILENAME: IO2Constants.java
 * ===================================================================
 */

//package invent;

/**
 *  This interface defines the constants used in the oxygen icumatic.client.tools.models.
 *  (Note 1: to invoke any changes made to these constants the clases
 *  implementing this interface need to be recompiled aswell.
 *  Note 2:  The values defined in such an interface are 'public static final' by 
 *  default. )
 *
 *  @author   David Murley.
 *  @version  1.1, 02/10/01.
 */
public interface IO2Constants
{
    /**  The integer identifier for the diffusion oxygen model. */
    public static final int DIFF_ID = 1;
    /**  The integer identifier for the volume-perfusion oxygen model. */
    public static final int VENT_PERF_ID = 2;
    /**  Conversion factor to convert ltr/min to mmol/min. */
    public static final float JUST = 41.56f;
    /** 
     *  The first estimate for the lower limit of the partial pressure (KPa) of oxygen
     *  in the arteries.  The lower limit for accurate SO2a calculations using the 
     *  Siggaard-Andersen ODC model.
     */
    public static final int PO2AL = 1;// sO2al approx. = 5% for normal.      
    /** 
     *  The first estimate for the upper limit of the partial pressure (KPa) of oxygen
     *  in the arteries.  The upper limit for accurate SO2a calculations using the 
     *  Siggaard-Andersen ODC model.
     */
    public static final float PO2AU = 150f;//  sO2au approx = 99.97% for normal.
//	public static final float PO2AU = 14.5f;//  sO2au approx = 97% for normal.

    /**  The default value of SO2a used when there is no solution to an oxygen model.  */
    public static final int DEFAULT_SO2 = 0;
    /**  The lower limit for the value of shunt. */
    public static final double SHUNT_LOWER_BOUND = 0.0000000; 
    /**  The upper limit for the value of shunt. */
    public static final double SHUNT_UPPER_BOUND = 1.00000000;
    
    /**  The lower limit for the value of oxygen consumption. */
    public static final double VO2_LOWER_BOUND = 0.00000001; 
    
    /**  The lower limit for the value of oxygen consumption. */
    public static final double VA_LOWER_BOUND = 0.00000001; 
    
    /**  The lower limit for the value of cardaic output. */
    public static final double Q_LOWER_BOUND = 0.00000001; 
    
     /**  The identifier that corresponds to a simple solution of the oxygen model.  */
    public static final int FOR_SO2 = 0; 
    /**  The identifier that corresponds to a solution of the oxygen model for use in the CO2 model.   */
    public static final int FOR_CO2 = 1;
}

