package sts3;

//import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TimeSeriesTranstoSet {
	Set<Integer> set;
	int label;
	
	public TimeSeriesTranstoSet(CSVreader d, int index, Bound BD, double eps, double sig) { // 1 TS to Set
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
	
	public TimeSeriesTranstoSet(double[][] q, int label, Bound B, double eps, double sig) {
		final int time = 0, data = 1;
		set = new HashSet<Integer>();
		this.label = label;
		int row, col, number;
		int column_num = (int) Math.round((B.tmax - B.tmin)/eps);
		
		for (int i = 0; i < q[data].length; i++) {// t time
			if (q[time][i] == 0 && q[data][i] == 0) break;
			row = (int)((q[data][i] - B.xmin)/sig + 1);
			col = (int)((q[time][i] - B.tmin)/eps + 1);
			number = (row - 1) * column_num + col;
			if(number <0){System.out.println("("+row+","+col+", "+column_num+")");}
			set.add(number);
		}
	}

	public TimeSeriesTranstoSet(Set<Integer> q, int label) {
		this.set = q;
		this.label = label;
	}
}