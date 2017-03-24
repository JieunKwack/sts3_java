package sts3;

import java.util.HashSet;
import java.util.Set;

public class TimeSeriesTranstoSet {
	Set<Integer>[] s;
	int[] label;
	
	public TimeSeriesTranstoSet(CSVreader d, double eps, double sig) {
		this.label = d.label.clone();
		
		int row, col, number;
		int column_num = (int) Math.round((d.tmax - d.tmin)/eps);
		for (int i = 0; i < d.data.length; i++) {
			s[i] = new HashSet<Integer>();
			for (int t = 0; t < d.data[0].length; t++) {// t time
				row = (int)((d.data[i][t] - d.xmin)/sig + 1);
				col = (int)((t - d.tmin)/eps + 1);
				number = (row - 1) * column_num + col;
				s[i].add(number);
			}
		}
	}
}