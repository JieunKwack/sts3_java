package sts3;

public class Bound {
	public double tmin, tmax, xmin, xmax;
	public int rows, cols;
	
	public Bound(CSVreader data) {
		this.tmin = 1;
		this.tmax = data.data[0].length;
		this.xmin = getminvalue(data.data);
		this.xmax = getmaxvalue(data.data);
	}
	
	public Bound(double[][] data) {
		final int t = 0, d = 1;
		
		this.tmin = data[t][0];
		this.tmax = data[t][0];
		for (int i = 0; i < data[d].length; i++){
			if ( data[t][i] < this.tmin ) this.tmin = data[t][i];
			if ( data[t][i] > this.tmax ) this.tmax = data[t][i];
		}
		
		this.xmin = data[d][0];
		this.xmax = data[d][0];
		for (int i = 0; i < data[d].length; i++){
			if ( data[d][i] < this.xmin ) this.xmin = data[d][i];
			if ( data[d][i] > this.xmax ) this.xmax = data[d][i];
		}
	}
	
	public void setRowsAndCols(double sig, double eps) {
		this.rows = (int)((this.xmax - this.xmin)/sig + 1);
		this.cols = (int)((this.tmax - this.tmin)/eps + 1);
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