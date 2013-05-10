package com.izzz.iseek.corr;

import com.izzz.iseek.base.BaseMapMain;
import com.izzz.iseek.vars.StaticVar;

public class CorrCalcK {

	/**
	 * 通过数据XX和YY计算出转换系数AB
	 * 函数可以静态调用
	 * Y=A*X+B*X^2
	 * */
	public static double[] from_xy_to_ab(double[] XX, double[] YY, int size)
	{
		double A[]=new double [500];
		double B[]=new double [500];
		double AB[]=new double[2];
		
//		int len=0;
		int k=0;
		
		double pinjun_A=0;
		double pinjun_B=0;
		
		double min_A=0;
		double max_A=0;
		double min_B=0;
		double max_B=0;
		 
//		len = XX.length;
		
		//两两计算k系数
		for(int j=0;j<size-1;j++)
			for( int m=j+1;m<size;m++)
			{
			
				if(XX[j]*XX[j]*XX[m] == XX[m]*XX[m]*XX[j])
				{
					A[k]=1.0;
					B[k]=0.0;
					k++;
				}
				else
				{
					B[k]=(YY[j]*XX[m]-YY[m]*XX[j])/(XX[j]*XX[j]*XX[m] - XX[m]*XX[m]*XX[j]);
					A[k]=(YY[j]-B[k]*XX[j]*XX[j])/XX[j];
					if(A[k]<0.90||A[k]>1.10)
					{
						A[k]=1.0;
						B[k]=0.0;
						System.out.println("set zero");
					}
					k++;
				}
			}
		
		//求出最小值，最大值
		min_A=max_A=A[0];
		min_B=max_B=B[0];
		for(int n=0;n<k;n++)
		{
			if(min_A>A[n])
				min_A=A[n];
			if(max_A<A[n])
				max_A=A[n];
			if(min_B>B[n])
				min_B=B[n];
			if(max_B<B[n])
				max_B=B[n];
	      pinjun_A+=A[n];
	      pinjun_B+=B[n];
		}

		//平均
		pinjun_A=(pinjun_A-min_A-max_A)/(k-2);
		pinjun_B=(pinjun_B-min_B-max_B)/(k-2);
		
		if(StaticVar.DEBUG_ENABLE)
		{
			String str = "pinjun_A="+pinjun_A + "\n" + "pinjun_B="+pinjun_B;
			StaticVar.logPrint('D', "k=" + k);
			StaticVar.logPrint('D', str);
			
			BaseMapMain.logText.setText(str);
		}
		
		AB[0]=pinjun_A;
		AB[1]=pinjun_B;
		
    return AB;				
		
	}
}
