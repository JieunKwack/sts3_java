package sts3;

//import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TimeSeriesTranstoSet {
	Set<Integer> set;
	int label;
	
	public TimeSeriesTranstoSet(CSVreader d, int index, Bound BD, double sig, double eps) { // 1 TS to Set
		int row, col, number;
		int column_num = (int) Math.floor((BD.tmax - BD.tmin)/eps) + 1;
		
		this.set = new HashSet<Integer>();	// empty set
		this.label = d.label[index];
		
		for (int t = 1; t <= d.data[index].length; t++) {// t time
			row = (int) Math.floor((d.data[index][t-1] - BD.xmin)/sig) + 1;
			col = (int) Math.floor((t - BD.tmin)/eps) + 1;
			number = (row - 1) * column_num + col;
			set.add(number);
		}
	}
	
//	public TimeSeriesTranstoSet(DevidedQ q, Bound B, double sig, double eps) {
//		int row, col, number;
//		int column_num = (int) Math.floor((B.tmax - B.tmin)/eps) + 1;
//		
//		this.set = new HashSet<Integer>();
//		
//		for (int i = 0; i < q.data.size(); i++) {
//			row = (int) Math.floor((q.data.get(i) - B.xmin)/sig) + 1;
//			col = (int) Math.floor((q.time.get(i) - B.tmin)/ eps) + 1;
//			number = (row - 1) * column_num + col;
//			set.add(number);
////			if(number <0){System.out.println("("+row+","+col+", "+column_num+")");}
//		}
//	}
}