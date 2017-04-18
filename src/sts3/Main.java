package sts3;

import java.io.IOException;
import java.util.ArrayList;

// file I/O library 
import java.io.FileNotFoundException;
import java.io.File;
import java.io.PrintWriter;

public class Main {
	// parameter
	public static final int numData = 30;
	public static final int numQuery = 900;
	public static final int lenTs = 128;
	
//	public static final double sigma = 0.88;// 0.18; 	// row cell size
//	public static final double epsilon = 1;// 21; 		// column cell size
	
	public static final double unitTsize = 1; //column
	public static final double unitXsize = 0.05; //row
	
	public static final int k = 1; 						// set the # of top K
	
	public static final String dataname = "CBF"; // put directory name
	//-----------------------------------------------------------------------------------
	
	public static final String trainPath = "src/"+dataname+"/"+dataname+"_TRAIN.csv";
	public static final String testPath = "src/"+dataname+"/"+dataname+"_TEST.csv";
	
	public static CSVreader Data, Query;
	public static Bound BD, BQ;
	public static ArrayList<TimeSeriesTranstoSet> unitD, unitQ, d, q;
//	public static ArrayList<QueryTranstoSet> q;
//	public static int errorCounts = 0;
	
	public static void main(String[] args) throws IOException, FileNotFoundException{
		// File output
		PrintWriter pw = new PrintWriter(new File("errorLists.csv"));//("SetbasedTs.csv")); //data 확인을 위한 용도
	    StringBuilder sb = new StringBuilder();
	    
		Data = new CSVreader(trainPath, numData, lenTs);
		Query = new CSVreader(testPath, numQuery, lenTs);
		
		BD = new Bound(Data, unitXsize, unitTsize);
		BQ = new Bound(Query, unitXsize, unitTsize);
		BD.getBound(BQ); //get new Bound
		
		unitD = new ArrayList<TimeSeriesTranstoSet>();
		for (int i = 0; i < numData; i++) {
			TimeSeriesTranstoSet item = new TimeSeriesTranstoSet(Data, i, BD, unitXsize, unitTsize);
			unitD.add(item);																		//			sb.append(item.set);//			sb.append('\n');
		}
																								//        pw.write(sb.toString().replace("[", "").replace("]", "")); //        pw.close();
		unitQ = new ArrayList<TimeSeriesTranstoSet>();
		for (int i = 0; i < numQuery; i++) {
			TimeSeriesTranstoSet item = new TimeSeriesTranstoSet(Query, i, BD, unitXsize, unitTsize);
			unitQ.add(item);
		}

		OptimizeAlg OA = new OptimizeAlg(unitD, unitQ, BD, unitXsize, unitTsize);
		OA.getCellSize();
		System.out.println("done");
//		sts3Naive sts3 = new sts3Naive(d, q);
//		sts3.run(k);
//		
//		System.out.println("errorCount\terrorRate");
//		System.out.println(sts3.getErrorCounts() + "\t" + sts3.getErrorRate());
//		
//		weightedSTS3 w_sts3 = new weightedSTS3(d, q);
//		w_sts3.run(k);
//		
//		System.out.println(w_sts3.getErrorCounts() + "\t" + w_sts3.getErrorRate());
//		
//		TfIdfSTS3 tfidf_sts3 = new TfIdfSTS3(d, q);
//		tfidf_sts3.run(k);
//		
//		System.out.println(tfidf_sts3.getErrorCounts() + "\t" + tfidf_sts3.getErrorRate());		
//		
		pw.write(sb.toString()); // file output
		pw.close(); // print writer for file close
	}
}