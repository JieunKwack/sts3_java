package sts3;

//import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TimeSeriesTranstoSet {
	Set<Integer> set;
	int label;
	
	public TimeSeriesTranstoSet(CSVreader d, int index, Bound BD, double eps, double sig) {
		set = new HashSet<Integer>();
		this.label = d.label[index];		
		int row, col, number;
		int column_num = (int) Math.round((BD.tmax - BD.tmin)/eps);
		
		for (int t = 0; t < d.data[index].length; t++) {// t time
			row = (int)((d.data[index][t] - BD.xmin)/sig + 1);
			col = (int)((t - BD.tmin)/eps + 1);
			number = (row - 1) * column_num + col;
			set.add(number);
		}
	}
}