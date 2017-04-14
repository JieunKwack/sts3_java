package sts3;

public class Bound {
	public double tmin, tmax, xmin, xmax;
	public int maxNumber, numCol, numRow;
	
	public Bound(CSVreader data, double sig, double eps) {
		this.tmin = 1;
		this.tmax = data.data[0].length;
		this.xmin = getminvalue(data.data);
		this.xmax = getmaxvalue(data.data);
		this.maxNumber = getmaxNumber(sig, eps);
	}
	
	public void getBound(Bound A) {
		if (A.tmin < this.tmin) this.tmin = A.tmin;
		if (A.tmax > this.tmax) this.tmax = A.tmax;
		if (A.xmin < this.xmin) this.xmin = A.xmin;
		if (A.xmax > this.xmax) this.xmax = A.xmax;
	}
	
	public int getmaxNumber(double sig, double eps) {
		int theNumOfCell;
		this.numRow = (int) Math.floor((this.xmax - this.xmin)/sig) + 1;
		this.numCol = (int) Math.floor((this.tmax - this.tmin)/eps) + 1;
		theNumOfCell = this.numRow * this.numCol;
		return theNumOfCell;
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