package zScoreClassifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import kraken.BringIntoOneNameSpaceForKraken;
import projectDescriptors.AbstractProjectDescription;

public class BringReducedIntoOneNameSpaceForKraken
{
	public static void writeMergedForOneLevel( List<AbstractProjectDescription> projects, String taxa)
		throws Exception
	{
		List<String> allNumeric = getAllNumericAttributes(projects, taxa);
		
		HashMap<String, Integer> positionMap = new HashMap<String,Integer>();
		for(int x=0;x  < allNumeric.size(); x++)
		{
			if(positionMap.containsKey(allNumeric.get(x)))
				throw new Exception("Logic error");
			
			positionMap.put(allNumeric.get(x), x);
		}
		
		for(AbstractProjectDescription apd : projects)
		{
			List<String> numericAttributes = BringIntoOneNameSpaceForKraken.getNumericAttributes(
				new File(apd.getZScoreFilteredLogNormalKrakenToArff(taxa)));
			
			HashMap<String, Integer> thisPositionMap = new HashMap<String,Integer>();
			
			for(int x=0;x  < numericAttributes.size(); x++)
			{
				if(thisPositionMap.containsKey(numericAttributes.get(x)))
					throw new Exception("Parsing error duplicate attibute " +numericAttributes.get(x) );
				
				thisPositionMap.put(numericAttributes.get(x), x);
			}
			
			HashMap<Integer, String> flipMap = new HashMap<Integer,String>();
			
			for(String s : thisPositionMap.keySet())
				flipMap.put(thisPositionMap.get(s), s);
			
			BufferedReader reader = new BufferedReader(new FileReader(new File(
				apd.getZScoreFilteredLogNormalKrakenToArff(taxa)	)));
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					apd.getZScoreFilteredLogNormalKrakenToCommonNamespaceArff(taxa))));
			
			writer.write(reader.readLine() + "\n");  // header comment line
			writer.write(reader.readLine() + "\n");  // @relation
			
			for( int x=0; x < allNumeric.size(); x++)
				writer.write("@attribute " + allNumeric.get(x) + " numeric\n");
			
			writer.write("@attribute isCase { true, false }\n");
			
			writer.write("\n\n");
			
			String nextLine = reader.readLine();
			
			while(! nextLine.startsWith("@data"))
				nextLine = reader.readLine();
			
			writer.write(nextLine + "\n");
			writer.write(reader.readLine() + "\n");  // %
			writer.write(reader.readLine() + "\n");  // number of instances
			writer.write(reader.readLine() + "\n");  //
			
			for(String s = reader.readLine(); s != null; s= reader.readLine())
			{
				writer.write(BringIntoOneNameSpaceForKraken.getNewLine(s, flipMap, positionMap));
			}
			
			writer.flush(); writer.close();
			reader.close();
		}
	}
	

	
	
	private static List<String> getAllNumericAttributes( List<AbstractProjectDescription> projects,
				String level) throws Exception
	{
		HashSet<String> set = new HashSet<String>();
		
		for(AbstractProjectDescription abd : projects)
		{
			set.addAll( BringIntoOneNameSpaceForKraken.getNumericAttributes( new File( abd.getZScoreFilteredLogNormalKrakenToArff(level))));
		}
		
		List<String> list = new ArrayList<String>(set);
		Collections.sort(list);
		return list;
	}
	
	
}
