package sts3;

import java.util.ArrayList;

public class OptimizeAlg {
	ArrayList<TimeSeriesTranstoSet> unitD;
	ArrayList<TimeSeriesTranstoSet> unitQ;
	int numTcol, numXrow;
	double Tsize, Xsize;
	
	public OptimizeAlg(ArrayList<TimeSeriesTranstoSet> uD, ArrayList<TimeSeriesTranstoSet> uQ, Bound Bd, double unitXsize, double unitTsize) {
		this.unitD = uD;
		this.unitQ = uQ;
		this.numTcol = Bd.numCol;
		this.numXrow = Bd.numRow;
		this.Tsize = unitTsize;
		this.Xsize = unitXsize;
	}
	
	public void getCellSize() {
		int NumOfCell = 0;
		for (int i = 1; NumOfCell > 0 ; i++) {//stop 조건이 옳지 못하다....
			for (int j = 1; j <= Math.sqrt(i); j++){
				
			}
		}
	}
}