package com.example.wifisignal2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
//对wifi信号的预处理
public class PreProcess {

	/* 求均值*/
	public double average(double[] a){
		int length=a.length;
		double sum = 0;
		double average=0;
		double sum1=0;
		if(length==0)
			return 0;
		for(int i=0;i<length;i++)
		{
			sum+=a[i];
		}
		average=sum/length;
		return average;
	}

	/* 求方差*/
	public double variance(int[] a){
		int length=a.length;
		double sum=0;
		double average=0;
		double var=0;
		if(length==0)
			return 0;
		for(double dd:a)
		{
			sum+=dd;
		}
		average=sum/length;
		for(double dd:a)
			var+=(dd-average)*(dd-average);
		var=var/length;
		return var;
	}

	/* 格布罗斯剔除粗大误差*/
	public List Grob(List<Double> a){
		List list_out=new ArrayList();
		int length=a.size();
		double g0=G0(length);
		double sum=0;     //数列之和
		double average=0; //数列均值
		double var=0;     //数列方差
		double Mvar=0;    //数列标准差
		double gn=0;
		if(length==0)
			return null;
		for(double dd:a)
		{
			sum+=dd;
		}
		average=sum/length;
		for(double dd:a)
			var+=(dd-average)*(dd-average);
		var=var/length;
		if(length!=1){
			Mvar=Math.sqrt(var/(length-1));   //测量数据的标准差
		}else{
			Mvar=Math.sqrt(var);
		}
		Collections.sort(a);
		for(double dd:a)
		{
			gn=(dd-average)/Mvar;
			if(Math.abs(gn)<g0){
				list_out.add(dd);
			}
		}
		return list_out;


	}
	//格罗布斯准则的临界值g0(n,0.05)表
	private double G0(int n) {
		// TODO Auto-generated method stub
		switch(n)
		{
			case 3:return(1.15);
			case 4:return(1.46);
			case 5:return(1.67);
			case 6:return(1.82);
			case 7:return(1.94);
			case 8:return(2.03);
			case 9:return(2.11);
			case 10:return(2.18);
			case 11:return(2.23);
			case 12:return(2.28);
			case 13:return(2.33);
			case 14:return(2.37);
			case 15:return(2.41);
			case 16:return(2.44);
			case 17:return(2.48);
			case 18:return(2.50);
			case 19:return(2.53);
			case 20:return(2.56);

		}
		return 0;
	}

	/* 高斯滤波*/
	public double[] Gaussian(List<Double>  a){
		List list_out=new ArrayList();
		int length=a.size();
		double sum=0;     //数列之和
		double average=0; //数列均值
		double var=0;     //数列方差
		double Mvar=0;    //数列标准差
		double Range1=0;
		double Range2=0;
		if(length==0)
			return null;
		for(double dd:a)
		{
			sum+=dd;
		}
		average=sum/length;
		for(double dd:a)
			var+=(dd-average)*(dd-average);
		var=var/length;
		if(length!= 1){
			Mvar = Math.sqrt(var/(length-1));    //测量数据的标准差
		}else{
			Mvar=Math.sqrt(var);
		}
		Range1=0.15*Mvar+var;
		Range2=3.09*Mvar+var;
		for(double dd:a)
		{
			if(dd>Range1 || dd<Range2){
				list_out.add(dd);
			}
		}
		double[] b=new double[list_out.size()];
		for(int i=0;i<list_out.size();i++){
			b[i]=(Double) list_out.get(i);
		}
		return b;
	}
	public double[] Roman(List<Double>  a){
		List list=new ArrayList();
		list=a;
		boolean flag=true;
		Collections.sort(list);
		while(flag){
			double ap1=Math.abs((Double)list.get(0));
			double sum=0;
			double 	var=0;
			double Mvar=0;    //数列标准差
			for(int i=1;i<list.size();i++){
				sum=sum+Math.abs((Double) list.get(i));
			}
			double average=sum/(list.size()-1);
			for(int i=1;i<list.size();i++){
				var+=(Math.abs((Double)list.get(i))-average)*(Math.abs((Double)list.get(i))-average);
			}
			Mvar=Math.sqrt(var/(list.size()-2));
			double P=Math.abs(ap1-average);
			if(P>(K(list.size())*Mvar)){
				list.remove(0);
			}else{
				flag=false;
			}
		}

		double[] b=new double[list.size()];
		for(int i=0;i<list.size();i++){
			b[i]=(Double) list.get(i);
		}
		return b;
	}

	private double K(int size) {
		// TODO Auto-generated method stub
		switch(size)
		{
			case 4:return(4.49);
			case 5:return(3.56);
			case 6:return(3.04);
			case 7:return(2.78);
			case 8:return(2.62);
			case 9:return(2.51);
			case 10:return(2.43);
			case 11:return(2.37);
			case 12:return(2.33);
			case 13:return(2.29);
			case 14:return(2.26);
			case 15:return(2.24);
			case 16:return(2.22);
			case 17:return(2.20);
			case 18:return(2.18);
			case 19:return(2.17);
			case 20:return(2.16);
			case 21:return(2.15);
			case 22:return(2.14);
			case 23:return(2.13);
			case 24:return(2.12);
			case 25:return(2.11);
			case 26:return(2.10);
			case 27:return(2.10);
			case 28:return(2.09);
			case 29:return(2.09);
			case 30:return(2.08);

		}
		return 0;



	}
}
