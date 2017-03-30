package sts3;

public class Bound {
	public double tmin, tmax, xmin, xmax;
	public int maxNumber;
	
	public Bound(CSVreader data, double sig, double eps) {
		this.tmin = 1;
		this.tmax = data.data[0].length;
		this.xmin = getminvalue(data.data);
		this.xmax = getmaxvalue(data.data);
		this.maxNumber = getmaxNumber(sig, eps);
	}
	
	public Bound(DevidedQ dq, double sig, double eps) {
		final int index = dq.time.size();
		
		this.tmin = dq.time.get(0);
		this.tmax = dq.time.get(index-1);
		
		this.xmin = dq.data.get(0);
		this.xmax = dq.data.get(0);
		for (int i = 0; i < index; i++){
			double val = dq.data.get(i);
			if ( val < this.xmin ) this.xmin = val;
			else if ( val > this.xmax ) this.xmax = val;
		}
		
		this.maxNumber = getmaxNumber(sig, eps);
	}
	
	public int getmaxNumber(double sig, double eps) {
		int cell_num, row_num, col_num;
		row_num = (int) Math.floor((this.xmax - this.xmin)/sig) + 1;
		col_num = (int) Math.floor((this.tmax - this.tmin)/eps) + 1;
		cell_num = row_num * col_num;
		return cell_num;
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