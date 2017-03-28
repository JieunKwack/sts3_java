package sts3;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Main {
	public static final int numData = 30;
	public static final int numQuery = 900;
	public static final int lenTs = 128;
	
	//parameter
	public static final double sigma = 0.18;// 0.18; 	// row cell size
	public static final double epsilon = 21;// 21; 		// column cell size
	public static final int k = 1; 						// set the # of top K
	
	public static CSVreader Data, Query;
	
	public static Bound BD;
	public static int maxNumber;
	
	public static ArrayList<TimeSeriesTranstoSet> d, q;
	public static ArrayList<Ans> ans;
	public static int error = 0;
	
	public static void main(String[] args) throws IOException, FileNotFoundException{
		PrintWriter pw = new PrintWriter(new File("SetbasedTs.csv")); //data 확인을 위한 용도
	    StringBuilder sb = new StringBuilder();
	    
		Data = new CSVreader("src/CBF/CBF_TRAIN.csv", numData, lenTs);
		Query = new CSVreader("src/CBF/CBF_TEST.csv", numQuery, lenTs);
		
		BD = new Bound(Data);
		
		BD.setRowsAndCols(sigma, epsilon);//need to modify
		maxNumber = (BD.rows - 1) * (int) Math.round((BD.tmax - BD.tmin)/epsilon) + BD.cols;//need to modify
		
		d = new ArrayList<TimeSeriesTranstoSet>();
		for (int i = 0; i < numData; i++) {
			TimeSeriesTranstoSet item = new TimeSeriesTranstoSet(Data, i, BD, epsilon, sigma);
			d.add(item);//python 결과랑 다름..틀린 부분 있는 지 확인해볼것
			sb.append(item);//과 똑같은 결과
//			Object[] array = item.set.toArray();
//			for (int a=0;a<array.length;a++){
//				sb.append(array[a]);
//			}
			sb.append('\n');
		}
        pw.write(sb.toString());
        pw.close();
        
		q = new ArrayList<TimeSeriesTranstoSet>();
		AscendingObj ascending = new AscendingObj();
		
		for (int i = 0; i < numQuery; i++) {//query index
			ans = new ArrayList<Ans>();
			TimeSeriesTranstoSet query = Trans_outQuery_to_Set(i);
			q.add(query);
			for (int j = 0; j < numData; j++) {
				//compute jaccard similarity
				Set<Integer> U = new HashSet<Integer>(d.get(j).set);
				Set<Integer> I = new HashSet<Integer>(d.get(j).set);
				
				U.addAll(query.set);
				I.retainAll(query.set);
				
				double jac = (double)I.size()/U.size();
				if (ans.size() < k) {
					Ans e = new Ans(jac, j);
					ans.add(e);
					Collections.sort(ans, ascending);
				}
				else if (ans.get(0).jac < jac) {
					Ans e = new Ans(jac, j);
					ans.remove(0);
					ans.add(e);
					Collections.sort(ans, ascending);
				}
			}
			
//			for(int j=0;j<k;j++){
//				System.out.println(ans.get(j).index);
//				System.out.println(ans.get(j).jac);
//			}
			
			if (d.get(ans.get(ans.size()-1).index).label != query.label) {
				error++;
//				System.out.println(ans.get(ans.size()-1).index + "\t" + d.get(ans.get(ans.size()-1).index).label);
//				System.out.println(i + "\t" + query.label);
			}
		}
//		System.out.println(maxNumber);
//		System.out.println(BD.rows + " " + BD.cols);
		System.out.println(error + "\t" +(double)error/numQuery); // error rate = (#wrongly classified ts) / (#test)
	}

	public static TimeSeriesTranstoSet Trans_outQuery_to_Set(int index) {
		//divide q into qin, qout
		ArrayList<double[][]> qinout = divideQintoQinAndQout(index, BD);
		TimeSeriesTranstoSet Qin = new TimeSeriesTranstoSet(qinout.get(0), Query.label[index], BD, epsilon, sigma);		
		Bound BQ = new Bound(qinout.get(1));		
		TimeSeriesTranstoSet qout = new TimeSeriesTranstoSet(qinout.get(1), Query.label[index], BQ, epsilon, sigma);
		Set<Integer> Qout = new HashSet<Integer>();
		for (Iterator<Integer> i = qout.set.iterator(); i.hasNext(); ) {
			Qout.add(i.next() + maxNumber);
		}
		Qin.set.addAll(Qout);
		
		return Qin;
	}
	
	public static ArrayList<double[][]> divideQintoQinAndQout(int index, Bound BD) {
		final int time = 0;
		final int data = 1;
		
		ArrayList<double[][]> qinout = new ArrayList<double[][]>(2);
		double[][] qin = new double[2][lenTs];
		double[][] qout = new double[2][lenTs];
		
		int jin = 0, jout = 0;
		for (int i = 0; i < lenTs; i++){
			if (Query.data[index][i] > BD.xmin && Query.data[index][i] < BD.xmax) {
				qin[time][jin] = i;
				qin[data][jin] = Query.data[index][i];
				jin++;
			} else {
				qout[time][jout] = i;
				qout[data][jout] = Query.data[index][i];
				jout++;
			}
		}
		
		qinout.add(qin);
		qinout.add(qout);
		
		return qinout;
	}
}

class AscendingObj implements Comparator<Ans> {	 
    @Override
    public int compare(Ans lhs, Ans rhs) {
        return Double.compare(lhs.getJac(), rhs.getJac());
    }
}
