package diverticulosis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import parsers.NewRDPParserFileLine;
import utils.ConfigReader;

/*
 * Eventual target of this :
 * java -classpath weka.jar weka.classifiers.trees.RandomForest  -t C:\adenomasRelease\spreadsheets\pivoted_familyLogNormalWithMetadata.arff -T C:\tope_Sep_2015\spreadsheets\familyasColumnsLogNormalPlusMetadataFilteredCaseControl.arff
 */

public class WriteToArff
{
	private static int getNumSamples(File file ) throws Exception
	{
		int count = 0;
		
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		reader.readLine();
		
		for( String s = reader.readLine(); s != null; s= reader.readLine())
		{
			s = s.replaceAll("\"", "");
			String[] splits = s.split("\t");
			
			if( include(splits))
				count++;
		}
		
		reader.close();
		
		return count;
	}
	
	private static boolean include(String[] splits)
	{
		if( splits[3].equals("false") &&  Integer.parseInt(splits[2]) == 1 &&
					! splits[6].equals("NA"))
			return true;
		
		return false;
			
	}
	
	public static void main(String[] args) throws Exception
	{
		for( int x=1; x < NewRDPParserFileLine.TAXA_ARRAY.length; x++)
		{
			System.out.println(NewRDPParserFileLine.TAXA_ARRAY[x]);
			
			File inFile = new File(ConfigReader.getTopeOneAtATimeDir() + File.separator + 
					"merged" + File.separator + "pivoted_" +  
					NewRDPParserFileLine.TAXA_ARRAY[x] + "asColumnsLogNormalPlusMetadata.txt");
			
			int numSamples = getNumSamples(inFile);
			
			BufferedReader reader = new BufferedReader(new FileReader(inFile));
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					ConfigReader.getTopeOneAtATimeDir() + File.separator + 
					"merged" + File.separator + "pivoted_" +  
					NewRDPParserFileLine.TAXA_ARRAY[x] + "asColumnsLogNormalPlusMetadata.arff")
					));
			
			writer.write("% " + NewRDPParserFileLine.TAXA_ARRAY[x] + "\n");
			
			writer.write("@relation " + NewRDPParserFileLine.TAXA_ARRAY[x]  + "_diverticulosis\n");
			
			String[] topSplits = reader.readLine().replaceAll("\"","").split("\t");
			
			
			for( int y=0; y < topSplits.length; y++)
			{
				if( y == 5 || y >= 9)
				writer.write("@attribute " + 
						topSplits[y].replaceAll(" ", "_").replaceAll("shannonEntropy", "shannonDiversity") 
								+ " numeric\n");
			}
			
			writer.write("@attribute isCase { true, false }\n");
			
			writer.write("\n\n@data\n");
			
			writer.write("%\n% " + numSamples + " instances\n%\n");
			
			for( String s= reader.readLine(); s != null; s = reader.readLine())
			{
				s = s.replaceAll("\"", "");
				String[] splits = s.split("\t");
				
				if( splits.length != topSplits.length)
					throw new Exception("Parsing error!");
				
				if(include(splits))
				{

					for( int y=0; y < splits.length; y++)
					{
						if( y==5 || y >=9)
							writer.write( splits[y] + ",");
					}
					
					if( splits[6].equals("0") )
						writer.write("false\n");
					else if( splits[6].equals("1"))
						writer.write("true\n");
					else throw new Exception("Parsing error " + splits[6]);
				}
				
			}
			
			writer.flush();  writer.close();
			
			reader.close();
		}
	}
}
