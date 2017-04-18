package sts3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class OptimizeAlg {
	ArrayList<TimeSeriesTranstoSet> unitD, newD;
	ArrayList<TimeSeriesTranstoSet> unitQ;
	double unitRowSize, unitColSize; //Column = Time, Row = Value(X)
	int unitNumOfCol, unitNumOfRow;
//	int numTcol, numXrow;
	double Tsize, Xsize;
	
	public OptimizeAlg(ArrayList<TimeSeriesTranstoSet> uD, ArrayList<TimeSeriesTranstoSet> uQ, Bound Bd, double unitXsize, double unitTsize) {
		this.unitD = uD;
		this.unitQ = uQ;
		
		this.newD = new ArrayList<TimeSeriesTranstoSet>();
		
		this.Tsize = unitTsize;
		this.Xsize = unitXsize;
		
		this.unitColSize = unitTsize;
		this.unitRowSize = unitXsize;
		this.unitNumOfCol = Bd.numCol;
		this.unitNumOfRow = Bd.numRow;
		
//		getCellSize();
	}
	
	public void getCellSize() throws FileNotFoundException {
//		int numOfCell = 0;
//		int bestColSize, bestRowSize;
		
	    
		weightedSTS3 unitInfoGain = new weightedSTS3(this.unitD);
		HashMap<Integer, Double> unitInfoGainDic = unitInfoGain.getInfoGainPerCell();
		Double unitAvgInfoGain = getAvgInfoGain(unitInfoGainDic);
		
		for (int i = 1; i <= this.unitNumOfCol*this.unitNumOfRow; i++) {//몇개의 cell을 합칠 것인지
			String filepath = "infoGainPerCell" + i + ".csv";
			PrintWriter pw = new PrintWriter(new File(filepath));//("SetbasedTs.csv")); //data 확인을 위한 용도
		    StringBuilder sb = new StringBuilder();
		    
			for (int j = 1; j <= Math.sqrt(i); j++){
//				System.out.println("# of cell combined\t" + i + "column size\t" + j );
				if ((double)i%j==0) {//i개의 cell로 직사각형 superCell 생성이 가능하다면
//					System.out.println(i+"\t"+ i/j);
					buildCellId(j, i/j);
					weightedSTS3 infoGainSts3 = new weightedSTS3(this.newD);
					HashMap<Integer, Double> infoGainDic = infoGainSts3.getInfoGainPerCell();
//					System.out.println(infoGainDic);
					
					sb.append("num cell to be combined,");
					sb.append(i);
					sb.append(", colsize, ");
					sb.append(j);
					sb.append("\ncell_id, infoGain\n");
					sb.append(infoGainDic);
					sb.append("\n");
//							new HashMap<Integer, Double>(infoGainSts3.getInfoGainPerCell());
					Double avgInfoGain = getAvgInfoGain(infoGainDic);
					
//					System.out.println(unitAvgInfoGain + "\t" + avgInfoGain);
					if (unitAvgInfoGain < avgInfoGain) {
						this.Tsize = j;
						this.Xsize = i/j;
					}
					//compare history of avgInfoGain
					
					buildCellId(i/j, j);
					infoGainSts3 = new weightedSTS3(this.newD);
					infoGainDic = new HashMap<Integer, Double>(infoGainSts3.getInfoGainPerCell());
					sb.append("num cell to be combined,");
					sb.append(i);
					sb.append(",colsize, ");
					sb.append(i/j);
					sb.append("\ncell_id, infoGain\n");
					sb.append(infoGainDic);
					sb.append("\n");
					pw.write(sb.toString().replace("{", "").replace("}", "").replace(",", "\n").replace("=", ","));
					sb.delete(0, sb.length()-1);
					avgInfoGain = getAvgInfoGain(infoGainDic);
					if (unitAvgInfoGain < avgInfoGain) {
						this.Tsize = i/j;
						this.Xsize = j;
					}
				}
			}
			pw.close();
		}
//		pw.write(sb.toString().replace("{", "").replace("}", "").replace("=", ","));
//		pw.close();
	}
	
	public void buildCellId(int newColSize, int newRowSize) {
//		System.out.print("newColSize\t" + newColSize + "\t newRowSize\t" + newRowSize);
		for (int i = 0; i < this.unitD.size(); i++){
//			System.out.println(i+"-th TS");
			TimeSeriesTranstoSet TS = this.unitD.get(i);
			TimeSeriesTranstoSet newTS = new TimeSeriesTranstoSet();
			newTS.label = TS.label;
			for (Iterator<Integer> it = TS.set.iterator(); it.hasNext();) {
				Integer old_cell_id = it.next();
				 int oldRowId = (int) Math.floor((old_cell_id - 1) / this.unitNumOfCol) + 1;
				 int oldColId = (int) Math.floor((old_cell_id - 1) % this.unitNumOfCol) + 1;
				 
				 int newRowId = (int) Math.floor((oldRowId - 1) / newRowSize) + 1;
				 int newColId = (int) Math.floor((oldColId - 1) / newColSize) + 1;
				 
				 int new_numOfCol = (int) this.unitNumOfCol / newColSize + 1;
				 Integer new_cell_id = (newRowId - 1) * new_numOfCol + newColId;
//				 System.out.println(old_cell_id +" -> " + new_cell_id);
				 newTS.set.add(new_cell_id);
			}
			this.newD.add(newTS);
		}
		
	}
	
	public Double getAvgInfoGain(HashMap<Integer, Double> dic) {
		Double sumOfInfoGain = 0.0;
		Integer numOfCell = dic.keySet().size();
		for (Iterator<Integer> it = dic.keySet().iterator(); it.hasNext(); ){
			Integer cell_id = it.next();
			sumOfInfoGain += dic.get(cell_id);
		}
		return sumOfInfoGain/numOfCell;
	}
}