package sts3;

import java.util.Scanner;
import java.io.FileInputStream;
import java.io.IOException;

public class CSVreader {
	public double[][] data;
	public int[] label;
	public double tmin, tmax, xmin, xmax;
	
	public CSVreader(String path, int numofTs, int lenTs) throws IOException {
		Scanner scan;
		FileInputStream input = new FileInputStream(path);
		
		data = new double[numofTs][lenTs];
		label = new int[numofTs];
		
		int i = 0;
		try {
			scan = new Scanner(input);
			while (scan.hasNext()) {
				String[] inputArr = scan.nextLine().split(",");
				label[i] = (int)Double.parseDouble(inputArr[0]);
				for (int j = 0; j < lenTs; j++){
					this.data[i][j] = Double.parseDouble(inputArr[j+1]);
				}
				i++;
			}
			scan.close();
			
		} catch(Exception e){
			e.printStackTrace();
		}
		input.close();
	}
	
	public void getBound() {
		this.tmin = 0;
		this.tmax = this.data[0].length;
		this.xmin = this.getminvalue(data);
		this.xmax = this.getmaxvalue(data);
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
//	public static void main() throws IOException{}
}