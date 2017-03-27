package sts3;

import java.util.Scanner;
import java.io.FileInputStream;
import java.io.IOException;

public class CSVreader {
	public double[][] data;
	public int[] label;
	
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
}