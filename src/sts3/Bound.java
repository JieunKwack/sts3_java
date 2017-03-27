package sts3;

public class Bound {
	public double tmin, tmax, xmin, xmax;
	
	public Bound(CSVreader data) {
		this.tmin = 0;
		this.tmax = data.data[0].length;
		this.xmin = getminvalue(data.data);
		this.xmax = getmaxvalue(data.data);
	}
	
	private double getminvalue(double[][] d) {
		double min = d[0][0];
		for (int i = 0; i < d.length; i++) {
			for (int j = 0; j < d[0].length; j++) {
				if (d[i][j] < min) min = d[i][j];
			}
		}
		return min;
	}
	
	private double getmaxvalue(double[][] d) {
		double max = d[0][0];
		for (int i = 0; i < d.length; i++) {
			for (int j = 0; j < d[0].length; j++){
				if (max < d[i][j]) max = d[i][j];
			}
		}
		return max;
	}
}