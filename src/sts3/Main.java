package sts3;

import java.io.IOException;
import java.util.ArrayList;

// file I/O library 
import java.io.FileNotFoundException;
import java.io.File;
import java.io.PrintWriter;

public class Main {
	public static final int numData = 30;
	public static final int numQuery = 900;
	public static final int lenTs = 128;
	public static final String dataname = "CBF"; // put directory name
	
	public static final String trainPath = "src/"+dataname+"/"+dataname+"_TRAIN.csv";
	public static final String testPath = "src/"+dataname+"/"+dataname+"_TEST.csv";
	
	//parameter
	public static final double sigma = 0.18;// 0.18; 	// row cell size
	public static final double epsilon = 21;// 21; 		// column cell size
	public static final int k = 1; 						// set the # of top K
	
	public static CSVreader Data, Query;
	public static Bound BD;
	public static ArrayList<TimeSeriesTranstoSet> d;
	public static ArrayList<QueryTranstoSet> q;
	public static int error = 0;
	
	public static void main(String[] args) throws IOException, FileNotFoundException{
		PrintWriter pw = new PrintWriter(new File("errorLists.csv"));//("SetbasedTs.csv")); //data 확인을 위한 용도
	    StringBuilder sb = new StringBuilder();
	    
	    
		Data = new CSVreader(trainPath, numData, lenTs);
		Query = new CSVreader(testPath, numQuery, lenTs);
		
		BD = new Bound(Data, sigma, epsilon);

		d = new ArrayList<TimeSeriesTranstoSet>();
		for (int i = 0; i < numData; i++) {
			TimeSeriesTranstoSet item = new TimeSeriesTranstoSet(Data, i, BD, sigma, epsilon);
			d.add(item);
//			sb.append(item.set);
//			sb.append('\n');
		}
//        pw.write(sb.toString().replace("[", "").replace("]", ""));
//        pw.close();
		
		q = new ArrayList<QueryTranstoSet>();
		for (int i = 0; i < numQuery; i++) {//query index
			QueryTranstoSet query =  new QueryTranstoSet(Query, i, BD, sigma, epsilon);
			q.add(query);
			for (int j = 0; j < numData; j++) {
				double jac = query.getJacSIM(d.get(j).set);
				
				if (query.AnsisEmpty()) {
					query.addAns(jac, j, d.get(j).label);
				} else if (query.getRootOfAns().jac < jac) {
					query.addAns(jac, j, d.get(j).label);
					if (query.ans.size() > k) query.removeRootOfAns();
				}
			}
			
			if (query.getBestAns().label != query.label) {
				// query ans 1NN output in file
//				sb.append("query\n"+query.set.toString().replace("[", "").replace("]", "")+"\n");
//				sb.append("1NN\n"+d.get(query.getBestAns().index).set.toString().replace("[","").replace("[", ""));
				
				sb.append(query.getBestAns().label+","+query.label+","+(error+1)+"\n"); // make string for file output				
//				System.out.println(query.getBestAns().label+", "+query.label+", "+(error+1)); // console output
				error++;
			}
		}
		pw.write(sb.toString()); // file output
		pw.close(); // print writer for file close
		System.out.println(error + "\t" +(double)error/numQuery); // error rate = (#wrongly classified ts) / (#test)
	}
}


