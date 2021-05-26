package SIMVENT.INVENT.bloodpkg;

import VisualNumerics.math.Sfun;


public class eqacidb
{
    private double[][] alpha = new double[20][20];
    private double[] beta =new double [20];
    private final static double[] empty = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

    //------------------ class constuctor
    public eqacidb(double[] x, double[] par, int n, char calnam)	
    {
//**This initialization is not necessary in java
//        for (int ai=0; ai<n; ai++)
//        { 
//            System.arraycopy(empty, 0, alpha[ai], 0, 20); //a little faster
//              for (int aj=0; aj<n; aj++)
//              {
//                  alpha[ai][aj]=0;
//              }
//        }


        switch (calnam)
        {
            // estcrb2b
        case 'e':
            alpha [18][19]=1;
            // solution of differential equations
            // i.e. construction of Jacobian matrix, alpha
            alpha[0][1] = 1/(x[1]);  
            alpha[1][3]= -1;
            alpha[1][4] = 1/(x[4]);
            alpha[1][5] =- 1/(x[5]);  
            alpha[2][6] = -1/(x[6]);   
            alpha[2][7] = 1/(x[7]); 
            alpha[3][3] = -1;
            alpha[3][8] = -1/(x[8]);
            alpha[3][9] = 1/(x[9]);
            alpha[4][3] = -1;   
            alpha[4][5] = -1/(x[5]);
            alpha[4][9] = -1/(x[9]); 
            alpha[4][10] = 1/(x[10]);  
            alpha[5][3] = -1;  
            alpha[5][11] =-1/(x[11]);
            alpha[5][12] =1/(x[12]);    
            alpha[6][3] = -1;   
            alpha[6][5] = -1/(x[5]);
            alpha[6][12] = -1/(x[12]);
            alpha[6][13] = 1/(x[13]);
            alpha[7][6] = -1;
            alpha[7][7] = -1;   
            alpha[8][8] = -1;
            alpha[8][9] = -1;
            alpha[8][10] = -1;
            alpha[8][11] = -1;
            alpha[8][12] = -1;
            alpha[8][13] = -1;
            alpha[9][5] = -1;
            alpha[10][3] = -1; 
            alpha[11][0]= -1;
            alpha[11][1] = par[2];
            alpha[11][4] = par[3]; 
            alpha[11][7] = par[2];
            alpha[11][9] = par[3];
            alpha[11][10] = 2*par[3];
            alpha[11][12] =par[3];
            alpha[11][13] =2*par[3];
            alpha[12][1] = par[2];
            alpha[12][2] = -1;
            alpha[12][4] = par[3];
            alpha[12][5] = par[3];
            alpha[12][14] =par[3];
            alpha[12][15] =par[3];
            alpha[13][13] =21/122.271087;
            alpha[13][14] =-1;
            alpha[14][10] =21/122.271087;
            alpha[14][15] =-1; 
            alpha[15][8] = 1/122.271087; 
            alpha[15][9] = 1/122.271087;   
            alpha[15][10] =1/122.271087; 
            alpha[15][16] = -1;
            alpha[16][16] =-1;
	   			
	   			// solution to ordinary equations
            beta[0]=-Sfun.log10(x[1])-6.1+Sfun.log10(par[5])+ par[4];
            beta[1]= +x[3]-Sfun.log10(x[4])+Sfun.log10(x[5])-6.1;
            beta[2]= +Sfun.log10(x[6])-Sfun.log10(x[7]) +par[4]-6.9274;
            beta[3]= +x[3]+Sfun.log10(x[8])-Sfun.log10(x[9])-7.476353;
            beta[4]= +x[3] +Sfun.log10(x[5])+Sfun.log10(x[9])-Sfun.log10(x[10])-Sfun.log10(1000) - 5.770746;
            beta[5]= +x[3] +Sfun.log10(x[11])-Sfun.log10(x[12]) - 7.70;
            beta[6]= +x[3] +Sfun.log10(x[5])+Sfun.log10(x[12])-Sfun.log10(x[13])-Sfun.log10(1000) -5.05;
            beta[7]= +x[6]+x[7]-23.7575;
            beta[8]= +x[8]+x[9]+x[10]+x[11]+x[12]+x[13]-122.271087;
            beta[9]= +x[5] -(0.191/0.225)* par[5];
            beta[10]= +x[3]-par[4]-Sfun.log10(3.094-0.335* par[4]);
            beta[11]= +x[0]-par[2]*x[1]-par[3]*x[4]-par[2]*x[7]-par[3]*x[9]-2*par[3]*x[10]-par[3]*x[12]-2*par[3]*x[13]+par[0];
            beta[12]=-par[2]*x[1]+x[2]-par[3]*x[4]- par[3]*x[5]- par[3]*x[14]- par[3]*x[15]+par[1]-par[2]*par[5];
            beta[13]=-(21/122.271087)*x[13]+x[14]; 
            beta[14]= -(21/122.271087)*x[10]+x[15];
            beta[15]=-(1/122.271087)*x[8]- (1/122.271087)*x[9]- (1/122.271087)*x[10]+x[16];
            beta[16]= +x[16]-odc( par[7], par[4],( par[5]/0.225), par[6], par[8], par[9], par[10], par[11]);
		
					
            break;
            //tio2co2c
			
        case 't':
				//	System.out.println("calnam t");
            alpha[0][0] = -1;
            alpha[2][0] = -1;  
            alpha[10][0] = 1-0.335/((3.094-0.335*x[0]));
            alpha[16][0] =deriv1(1,par[7], x[0],( par[5]/0.225), par[6], par[8], par[9], par[10], par[11]);
            alpha[0][1] = 1/(x[1]);  
   				
            alpha[1][3] = -1;
            alpha[1][4] = 1/(x[4]);
            alpha[1][5] =- 1/(x[5]);  
            alpha[2][6] = -1/(x[6]);   
            alpha[2][7] = 1/(x[7]); 
            alpha[3][3] = -1;
            alpha[3][8] = -1/(x[8]);
            alpha[3][9] = 1/(x[9]);
            alpha[4][3] = -1;   
            alpha[4][5] = -1/(x[5]);
            alpha[4][9] = -1/(x[9]); 
            alpha[4][10] = 1/(x[10]);  
            alpha[5][3] = -1;  
            alpha[5][11] =-1/(x[11]);
            alpha[5][12] =1/(x[12]);    
            alpha[6][3] = -1;   
            alpha[6][5] = -1/(x[5]);
            alpha[6][12] = -1/(x[12]);
            alpha[6][13] = 1/(x[13]);
            alpha[7][6] = -1;
            alpha[7][7] = -1;   
            alpha[8][8] = -1;
            alpha[8][9] = -1;
            alpha[8][10] = -1;
            alpha[8][11] = -1;
            alpha[8][12] = -1;
            alpha[8][13] = -1;
            alpha[9][5] = -1;
            alpha[10][3] = -1; 
            alpha[11][1] = par[2];
            alpha[11][4] = par[3]; 
            alpha[11][7] = par[2];
            alpha[11][9] = par[3];
            alpha[11][10] = 2*par[3];
            alpha[11][12] =par[3];
            alpha[11][13] =2*par[3];
            alpha[12][1] = par[2];
            alpha[12][2] = -1;
            alpha[12][4] = par[3];
            alpha[12][5] = par[3];
            alpha[12][14] =par[3];
            alpha[12][15] =par[3];
            alpha[13][13] =21/122.271087;
            alpha[13][14] =-1;
            alpha[14][10] =21/122.271087;
            alpha[14][15] =-1; 
            alpha[15][8] = 1/122.271087; 
            alpha[15][9] = 1/122.271087;   
            alpha[15][10] =1/122.271087; 
            alpha[15][16] = -1;
            alpha[16][16] =-1;	   
   
   
   				//	beta=usrfunb_tio2co2c(x,par,n);
   				
            beta[0]=-Sfun.log10(x[1])-6.1+Sfun.log10(par[5])+ x[0];	
            beta[1]= +x[3]-Sfun.log10(x[4])+Sfun.log10(x[5])-6.1;
            beta[2]= +Sfun.log10(x[6])-Sfun.log10(x[7]) +x[0]-6.9274;
            beta[3]= +x[3]+Sfun.log10(x[8])-Sfun.log10(x[9])-7.476353;
            beta[4]= +x[3] +Sfun.log10(x[5])+Sfun.log10(x[9])-Sfun.log10(x[10])-Sfun.log10(1000) - 5.770746;		
            beta[5]= +x[3] +Sfun.log10(x[11])-Sfun.log10(x[12]) - 7.70;
            beta[6]= +x[3] +Sfun.log10(x[5])+Sfun.log10(x[12])-Sfun.log10(x[13])-Sfun.log10(1000) -5.05;
            beta[7]= +x[6]+x[7]-23.7575;
            beta[8]= +x[8]+x[9]+x[10]+x[11]+x[12]+x[13]-122.271087;		
            beta[9]= +x[5] -(0.191/0.225)* par[5];
            beta[10]= +x[3]-x[0]-Sfun.log10(3.094-0.335*x[0]);
            beta[11]= +par[4]-par[2]*x[1]-par[3]*x[4]-par[2]*x[7]-par[3]*x[9]-2*par[3]*x[10]-par[3]*x[12]-2*par[3]*x[13]+par[0];
            beta[12]=-par[2]*x[1]+x[2]-par[3]*x[4]- par[3]*x[5]- par[3]*x[14]- par[3]*x[15]+par[1]-par[2]*par[5];
            beta[13]=-(21/122.271087)*x[13]+x[14]; 
            beta[14]= -(21/122.271087)*x[10]+x[15];
            beta[15]=-(1/122.271087)*x[8]- (1/122.271087)*x[9]- (1/122.271087)*x[10]+x[16];
            beta[16]= +x[16]-odc( par[7], x[0],( par[5]/0.225), par[6], par[8], par[9], par[10], par[11]);

            break;
            // steady
        case 's':
            //	alpha=usrfuna_st(x,par,n);
            //	beta=usrfunb_st(x,par,n);
            break;
            //allbcr3b
        case 'a':	
				//alpha=usrfuna_allbcr3b(x,par,n);
					
            alpha[0][0] = -1;  
            alpha[2][0] = -1;  
            alpha[10][0] = 1-0.335/((3.094-0.335*x[0]));
            alpha[16][0] =deriv1(1, x[17], x[0],( x[2]/0.225), par[6], par[8], par[9], par[10], par[11]);  
            alpha[16][17] =deriv1(18,x[17], x[0],( x[2]/0.225), par[6], par[8], par[9], par[10], par[11]);
            alpha[16][2] =deriv1(3,x[17], x[0],( x[2]/0.225), par[6], par[8], par[9], par[10], par[11]);
            alpha[0][2] = -1/(x[2]);
            alpha[9][2] =0.191/0.225;
            alpha[12][2] =par[2];
            alpha[0][1]= 1/(x[1]);  
            alpha[1][3] = -1;
            alpha[1][4] = 1/(x[4]);
            alpha[1][5] =- 1/(x[5]);  
            alpha[2][6] = -1/(x[6]);   
            alpha[2][7] = 1/(x[7]); 
            alpha[3][3] = -1;
            alpha[3][8] = -1/(x[8]);
            alpha[3][9] = 1/(x[9]);
            alpha[4][3] = -1;   
            alpha[4][5] = -1/(x[5]);
            alpha[4][9] = -1/(x[9]); 
            alpha[4][10] = 1/(x[10]);  
            alpha[5][3] = -1;  
            alpha[5][11] =-1/(x[11]);
            alpha[5][12] =1/(x[12]);    
            alpha[6][3] = -1;   
            alpha[6][5] = -1/(x[5]);
            alpha[6][12] = -1/(x[12]);
            alpha[6][13] = 1/(x[13]);
            alpha[7][6] = -1;
            alpha[7][7] = -1;   
            alpha[8][8] = -1;
            alpha[8][9] = -1;
            alpha[8][10]= -1;
            alpha[8][11] = -1;
            alpha[8][12] = -1;
            alpha[8][13] = -1;
            alpha[9][5] = -1;
            alpha[10][3] = -1; 
            alpha[11][1] = par[2];
            alpha[11][4] = par[3]; 
            alpha[11][7] = par[2];
            alpha[11][9] = par[3];
            alpha[11][10] = 2*par[3];
            alpha[11][12] =par[3];
            alpha[11][13] =2*par[3];
            alpha[12][1] = par[2];
            alpha[12][4] = par[3];
            alpha[12][5] = par[3];
            alpha[12][14] =par[3];
            alpha[12][15] =par[3];
            alpha[13][13] =21/122.271087;
            alpha[13][14] =-1;
            alpha[14][10] =21/122.271087;
            alpha[14][15] =-1; 
            alpha[15][8] = 1/122.271087; 
            alpha[15][9] = 1/122.271087;   
            alpha[15][10] =1/122.271087; 
            alpha[15][16] = -1;
            alpha[16][16] =-1;
            alpha[17][16] =par[6];
            alpha[17][17] =0.01;

	   			
				//beta=usrfunb_allbcr3b(x,par,n);
            beta[0]=-Sfun.log10(x[1])-6.1+Sfun.log10(x[2])+ x[0];
            beta[1]= +x[3]-Sfun.log10(x[4])+Sfun.log10(x[5])-6.1;
            beta[2]= +Sfun.log10(x[6])-Sfun.log10(x[7]) +x[0]-6.9274;
            beta[3]= +x[3]+Sfun.log10(x[8])-Sfun.log10(x[9])-7.476353;
            beta[4]= +x[3] +Sfun.log10(x[5])+Sfun.log10(x[9])-Sfun.log10(x[10])-Sfun.log10(1000) - 5.770746;
            beta[5]= +x[3] +Sfun.log10(x[11])-Sfun.log10(x[12]) - 7.70;
            beta[6]= +x[3] +Sfun.log10(x[5])+Sfun.log10(x[12])-Sfun.log10(x[13])-Sfun.log10(1000) -5.05;
            beta[7]= +x[6]+x[7]-23.7575;
            beta[8]= +x[8]+x[9]+x[10]+x[11]+x[12]+x[13]-122.271087;
            beta[9]= +x[5] -(0.191/0.225)* x[2];
            beta[10]= +x[3]-x[0]-Sfun.log10(3.094-0.335*x[0]);
            beta[11]= +par[4]-par[2]*x[1]-par[3]*x[4]-par[2]*x[7]-par[3]*x[9]-2*par[3]*x[10]-par[3]*x[12]-2*par[3]*x[13]+par[0];
            beta[12]=-par[2]*x[1]+par[5]-par[3]*x[4]- par[3]*x[5]- par[3]*x[14]- par[3]*x[15]+par[1]-par[2]*x[2];
            beta[13]=-(21/122.271087)*x[13]+x[14]; 
            beta[14]= -(21/122.271087)*x[10]+x[15];
            beta[15]=-(1/122.271087)*x[8]- (1/122.271087)*x[9]- (1/122.271087)*x[10]+x[16];
            beta[16]= +x[16]-odc( x[17], x[0],( x[2]/0.225), par[6], par[8], par[9], par[10], par[11]);
            beta[17]=-x[17]*0.01-x[16]*par[6]+par[7];

            break;
        }
    }
		
    public final double odc(double PO2,double pHp,double PCO2,double Hb,double FMetHb,double FCOHb,double T,double cDPG)
    {
        // odc - Siggaar Andersen oxygena dissociation curve, takes values of PO2,
        // pH and PCO2 and returns the oxygen saturation. Assumes nomal concnetration
        // of DPG, and zero methly and carboxy Hb.

        //-----------------------------------------------------------------------------------
        // Siggaard-Andersen oxygen dissociation curve-------------------------------------
        //-------------------------------------------------------------------------------
		
        double a1=-0.88*(pHp-7.4);				
        double a2=0.048*Math.log(PCO2/5.3);
        double a3=-0.7*FMetHb;				// mean FMetHb
        double a4=(0.3-(0.1*0.005))*((cDPG/5)-1);	// x=cDPG parameter
        double a5=-0.25*0.005;				// mean FHbF
        double a=a1+a2+a3+a4+a5;
        double ko=0.5343;
        double b=0.055*(T-37);				// mean temp
        double yo=Math.log(0.867/(1-0.867));
        double h=3.5+a;
        double xo=a+b;

        double cHb=(1-(FMetHb+FCOHb))*Hb;			

        //calculate SO2 from pO2 at constant pH i.e. Haldane
        double p=PO2+(218*0.00023);			
        double x1=Math.log(p/7);
        double y=yo+(x1-xo)+(h*Sfun.tanh(ko*(x1-xo)));
        double tmp = Math.exp(y);
        double s= tmp/(1+tmp);
        double sO2=(Hb*(s*(1-FMetHb-FCOHb)))/cHb;	// for eryth
	
        return sO2;
    }
	
    public final double deriv1(double ii,double PO2,double pHp,double PCO2,double Hb,double FMetHb,double FCOHb,double T,double cDPG)
    {
        // derivative of odc
        double a1=-0.88*(pHp-7.4);				
        double aa=PCO2*0.225;
        double a2=0.048*Math.log(PCO2/5.3);
        double a3=-0.7*FMetHb;					
        double a4=(0.3-(0.1*0.005))*((cDPG/5)-1);		
        double a5=-0.25*0.005;					
        double a=a1+a2+a3+a4+a5;
        //a=a1+0.048*log(aa/(0.225*5.3))+a3+a4+a5;
        double ko=0.5343;
        double b=0.055*(T-37);				
        double yo=Math.log(0.867/(1-0.867));
        double xo=a+b;
        double h=3.5+xo-b;
        double cHb=(1-(FMetHb+FCOHb))*Hb;			
        double p=PO2+(218*0.00023);			
        double x1=Math.log(p/7);

        double y=yo+x1-xo+(3.5+xo-b)*Sfun.tanh(ko*x1-ko*xo);
        double tmp = Math.exp(y);
        double s= tmp/(1+tmp);
        double w=ko*x1-ko*xo;
        tmp = Math.exp(w);
        double patrat=(tmp)*(tmp)+1;
        double d_tan=4*(tmp)/ (patrat*patrat);

        double d_y= 0;
        if (ii==1)
        {
            double d_xo=-0.88;
            d_y=(-1+Sfun.tanh(w)+(3.5+xo-b)*d_tan*(-ko))*d_xo;
        }

        if(ii==18)
        {
            double d_x1=7/(PO2+218*0.00023);
            d_y=(1+(3.5+xo-b)*d_tan*(ko))*d_x1;
        }

        if (ii==3)
        {
            double d_xo=0.048*(0.225*5.3)/PCO2;
            //d_xo=0.048*5.3/PCO2;
            d_y=(-1+Sfun.tanh(w)+(3.5+xo-b)*d_tan*(-ko))*d_xo;
        }

        double vodc=s*(1-FMetHb)*Hb/cHb-Hb*FCOHb/cHb;
        tmp = Math.exp(y);
        double d_s=d_y*(tmp)/(1+(tmp*tmp));
        double d_odc=(1-FMetHb)*Hb/cHb;

        double derivat1=d_odc*d_s;

        return derivat1;
    }

    /**
     * Gets the value of alpha as the result of the numerics.
     * @return the comments.
     */
    public final double [][] getalpha()
    {
        return alpha;
    }
		
    /**
     * Gets the value of beta as the result of the numerics.
     * @return the comments.
     */
    public final double [] getbeta()
    {
        return beta;
    }
}
