package sts3;

import java.io.IOException;
import java.util.Set;
import java.util.HashSet;

public class Main {
	public static final int numData = 30;
	public static final int numQuery = 900;
	public static final int lenTs = 128;
	public static final double sigma = 0.18; // row cell size
	public static final double epsilon = 21; // column cell size
	public static final int k = 5; // set the # of top K
	
	public static CSVreader Data, Query;
	public static TimeSeriesTranstoSet d, q;
//	public static Ans[] ans;
	public static Ans ans;
	public static int error = 0;
	public static void main(String[] args) throws IOException {
		Data = new CSVreader("C:/Users/monster/Documents/2016.WV/STS3/CBF/CBF_TRAIN.csv", numData, lenTs);
		Query = new CSVreader("C:/Users/monster/Documents/2016.WV/STS3/CBF/CBF_TEST.csv", numQuery, lenTs);
		Data.getBound();
		
		d = new TimeSeriesTranstoSet(Data, epsilon, sigma);
		q = new TimeSeriesTranstoSet(Query, epsilon, sigma);
		
		ans = new Ans();
		
		for (int i = 0; i < numQuery; i++) {
			for (int j = 0; j < numData; j++) {
				//jaccard similarity
				Set<Integer> U = new HashSet<Integer>(d.s[j]);
				Set<Integer> I = new HashSet<Integer>(d.s[j]);
				
				U.addAll(q.s[i]);
				I.retainAll(q.s[i]);
				
				double jac = (double)I.size()/U.size();
				if (ans.isempty()) {
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
			if (d.label[ans.index] != q.label[i]) error++;
		}
		System.out.println((double)error/numQuery); // error rate = (#wrongly classified ts) / (#test)
	}
	
}
