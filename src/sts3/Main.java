package sts3;

import java.io.IOException;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class Main {
	public static final int numData = 30;
	public static final int numQuery = 900;
	public static final int lenTs = 128;
	public static final double sigma = 0.18; // row cell size
	public static final double epsilon = 21; // column cell size
	public static final int k = 5; // set the # of top K
	public static int maxNumber;
	
	public static CSVreader Data, Query;
	public static Bound BD;
	public static ArrayList<TimeSeriesTranstoSet> d, q;
//	public static Ans[] ans;
	public static Ans ans;
	public static int error = 0;
	public static void main(String[] args) throws IOException {
		Data = new CSVreader("C:/Users/monster/Documents/2016.WV/STS3/CBF/CBF_TRAIN.csv", numData, lenTs);
		Query = new CSVreader("C:/Users/monster/Documents/2016.WV/STS3/CBF/CBF_TEST.csv", numQuery, lenTs);
		BD = new Bound(Data);
		BD.setRowsAndCols(sigma, epsilon);
		maxNumber = BD.rows * BD.cols;
		
		d = new ArrayList<TimeSeriesTranstoSet>();
		q = new ArrayList<TimeSeriesTranstoSet>();
		
		for (int i = 0; i < numData; i++) {
			TimeSeriesTranstoSet item = new TimeSeriesTranstoSet(Data, i, BD, epsilon, sigma);
			d.add(item);
		}
		
		ans = new Ans();
		for (int i = 0; i < numQuery; i++) {
			TimeSeriesTranstoSet query = Trans_outQuery_to_Set(i);
			q.add(query);
			ans.init();
			for (int j = 0; j < numData; j++) {
				//compute jaccard similarity
				Set<Integer> U = new HashSet<Integer>(d.get(j).set);
				Set<Integer> I = new HashSet<Integer>(d.get(j).set);
				
				U.addAll(query.set);
				I.retainAll(query.set);
				
				double jac = (double)I.size()/U.size();
				if (ans.isempty() || ans.jac < jac) {
					ans.jac = jac;
					ans.index = j;
				}
//				if (ans.top1 < jac) {
//					// sudo code
//					//minheap 정의 필요
//  					1. remove ans.top1
//  					2. add jac
//				}
			}
			if (d.get(ans.index).label != query.label) error++;
		}
		System.out.println((double)error/numQuery); // error rate = (#wrongly classified ts) / (#test)
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
//		if (!qout.set.isEmpty()){
//			System.out.println(qout.set);
//			System.out.println(Qout);
//		}
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
