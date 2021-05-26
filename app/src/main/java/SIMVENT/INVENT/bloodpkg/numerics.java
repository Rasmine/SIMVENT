package SIMVENT.INVENT.bloodpkg;

public class numerics
{

    private double[][] alpha;
    private double[] beta;
    private int[] indx;

    //------------------ class constuctor
    public numerics()  //tidligere hed den - mnewt()
    {	
    }
	
    /**
     * Gets the value of q as the result of the numerics.
     * @return the comments.
     */
    public final double [] getq(double ntrial, double[] var, double[] par,int n,double tolx,double tolf,char calnam)
    {
        //	double [] q= solvemnewt();
        //	this.q=q;
        double errf=0;	
        double errx = 0.0;

        indx=new int [n];
        alpha = new double[n][n];
        beta =new double[n];

        eqacidb rests= new eqacidb(var,par,n,calnam);
        alpha= rests.getalpha();
        beta=rests.getbeta();
 
        for (int i = 0;i<n;i++)
        { 
            errf = errf+Math.abs(beta[i]);
        }
        if (errf <= tolf)
        {
            System.out.println(" mnewt: error limit reached");
            // find a way to stop the code
        }
   	
        ludcmp(n);
  
        lubksb(n);
	 	
        for (int i = 0; i<n;i++)
        {
            errx = errx+Math.abs(beta[i]);
            var[i] = var[i]+beta[i];      
        }
        if(errx <= tolx)
        {
            // find some way to exit
        }
        return var;
    }



//ludcmp
        //constants
    private static final double TINY=1.0e-20;
	
    //------------------ class constuctor
    private final void ludcmp(int n)	
    {	
        double d = 1.0;			// no initialisation done on matlab code
        double big;
        double [] vv=new double[50];
        double sum= 10000;
        double dum;
        int imax;

        int i;
        int j;
        int k;
		
        // initialise
        sum=alpha[1][1];
        imax=1;
		
		
        for (i = 0;i<n;i++)
        {
            big = 0.0;
            for (j = 0; j<n; j++)
            {
                if (Math.abs(alpha[i][j]) > big)
                {
                    big = Math.abs(alpha[i][j]);
                }
            }
			
            if (big == 0.0)
            {
                //println('pause in LUDCMP - singular matrix'); 
            }
            vv[i] = 1.0/big;
        }
		
        for (j = 0;j<n;j++)
        {
            for (i=0; i<=(j-1);i++)
            {
                sum = alpha[i][j];
                for (k=0;k<=(i-1);k++)
                {
                    sum = sum-alpha[i][k]*alpha[k][j];
                }	
                alpha[i][j] = sum;
            }
  			
            big = 0.0;
            for(i = j; i<n;i++)
            {
                sum = alpha[i][j];
                for (k=0;k<=(j-1);k++)
                {
                    sum = sum-alpha[i][k]*alpha[k][j];
                }
                alpha[i][j] = sum;
                dum = vv[i]*Math.abs(sum);
      		if (dum > big)
      		{
                    big = dum;
                    imax = i;
      		}
            }
  			
            if (j != imax)
            {
                for (k=0; k<n; k++)
                {
                    dum = alpha[imax][k];
                    alpha[imax][k] = alpha[j][k];
                    alpha[j][k] = dum;
                }
                d = -d;
                vv[imax] = vv[j];
            }
            indx[j] = imax;
            if (alpha[j][j] == 0.0)
            {
                alpha[j][j] = TINY;
            }
            if (j != (n-1))
            {
                dum = 1.0/alpha[j][j];
                for (i = (j+1); i<n;i++)
                {
                    alpha[i][j] = alpha[i][j]*dum;
                }
            }
        }

    }


//lubksb

    //------------------ class constuctor
    private final void lubksb(int n)
    {
        int ip;
        double sum;
        int i;
        int j;
		
        int ii = -1;
        for (i=0;i<n;i++)
        {
            ip = indx[i];
            sum = beta[ip];
            beta[ip] = beta[i];
            if(ii != -1)
            {
                for(j=ii; j<=(i-1);j++)
                {
                    sum = sum-alpha[i][j]*beta[j];
                }
            }
            else if(sum != 0.0)
            {
                ii = 0;
            }
            beta[i] = sum;
        }
        for (i = (n-1);i>=0;i--)
        {
            sum = beta[i];
            if (i<(n-1))
            {
                for (j=(i+1); j<n;j++)
                {
                    sum = sum-alpha[i][j]*beta[j];
                }
            }
            beta[i] = sum/alpha[i][i];
	
        }

    }

}
