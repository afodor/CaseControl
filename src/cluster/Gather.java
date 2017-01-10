package cluster;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import kraken.RunAllClassifiers;

public class Gather
{
	private static File topDir = new File("/nobackup/afodor_research/clusterArff/ArffMerged/clusterCross/");
	private static File spreadsheetDir = new File("/nobackup/afodor_research/clusterArff/spreadsheets");

	private static void writeResults(String taxa, HashMap<String , List<Double>> map )
		throws Exception
	{
		System.out.println("Writing ... " + taxa);
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
				spreadsheetDir.getAbsolutePath() + File.separator + "cross_" + taxa + ".txt")));
		
		List<String> keys = new ArrayList<String>(map.keySet());
		Collections.sort(keys);
		
		writer.write("iteration\tisCross");
		
		for(String s : keys)
			writer.write("\t" + s);
		
		writer.write("\n");
		
		int length = RunCrossClassifiersCluster.NUM_PERMUTATIONS+1;
		
		for( int x=1; x < keys.size(); x++)
			if( map.get(keys.get(x)).size() != length)
				throw new Exception("Parsing error " + length + " " +
						map.get(keys.get(x)).size()  + " " + keys.get(x)) ;
		
		for( int x=0; x< length; x++)
		{
			writer.write((x+1) + "\t" + (x>0));
			
			for( int y=0; y < keys.size(); y++)
				writer.write("\t" + map.get(keys.get(y)).get(x) );
			
			writer.write("\n");
		}
		
		writer.flush();  writer.close();
	}
	
	public static void main(String[] args) throws Exception
	{
		String[] list = topDir.list();
		for( int t =0; t < RunAllClassifiers.TAXA_ARRAY.length; t++)
		{
			String taxa = RunAllClassifiers.TAXA_ARRAY[t];
			HashMap<String , List<Double>> map = new HashMap<String , List<Double>>();
			
			for(String s : list)
			{
				if( s.indexOf(taxa) != -1)
				{
					String key = s.replace("_" + taxa, "").replace(".txt", "");
					
					if( map.containsKey(key))
						throw new Exception("Duplicate " + key);
					
					List<Double> innerList = new ArrayList<Double>();
					
					
					BufferedReader reader = new BufferedReader(new FileReader(
							new File(topDir.getAbsolutePath() + File.separator + 
									s)));
					
					reader.readLine();
					
					for(String s2 = reader.readLine(); s2 != null; s2 =reader.readLine())
					{
						String[] splits =s2.split("\t");
						
						if( splits.length != 2)
							throw new Exception("Parsing error");
						
						innerList.add(Double.parseDouble(splits[0]));
					}
					
					if( innerList.size() == RunCrossClassifiersCluster.NUM_PERMUTATIONS + 1)
					{
						map.put(key, innerList);
					}
					else
					{
						System.out.println("Skipping " + key+ " " +  taxa + " " + 
											innerList.size() + " " + RunCrossClassifiersCluster.NUM_PERMUTATIONS + 1);
					}
					
					reader.close();
				}
			}
			
			writeResults(taxa, map);
		}
		
	}
}
