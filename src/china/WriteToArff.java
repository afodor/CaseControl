package china;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import parsers.NewRDPParserFileLine;
import utils.ConfigReader;

public class WriteToArff
{
	private static int getNumSamples(File file , String timepoint) throws Exception
	{
		int count = 0;
		
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		reader.readLine();
		
		for( String s = reader.readLine(); s != null; s= reader.readLine())
		{
			s = s.replaceAll("\"", "");
			String[] splits = s.split("\t");
			
			if( include(splits, timepoint))
				count++;
		}
		
		reader.close();
		
		return count;
	}
	
	private static boolean include(String[] splits, String timepoint)
	{
		if( splits[1].equals("1") &&  splits[8].equals(timepoint))
			return true;
		
		return false;
			
	}
	
	private static void writeForATimepoint(File inFile, String timepoint, String taxa)
		throws Exception
	{
		System.out.println(inFile.getAbsolutePath());
		int numSamples = getNumSamples(inFile, timepoint);
		BufferedReader reader = new BufferedReader(new FileReader(inFile));
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
				ConfigReader.getMergedArffDir() + File.separator + 
				"China2015" + File.separator + "pivoted_" +  
				taxa + "asColumnsLogNormalPlusMetadata_" + timepoint + ".arff")));
		
		writer.write("% " + taxa + "\n");
		
		writer.write("@relation " +  "china_" + timepoint +  "\n");
		
		String[] topSplits = reader.readLine().replaceAll("\"","").split("\t");
		
		for( int y=0; y < topSplits.length; y++)
		{
			if( y == 3 || y >= 9)
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
			
			if(include(splits, timepoint))
			{

				for( int y=0; y < splits.length; y++)
				{
					if( y==5 || y >=9)
						writer.write( splits[y] + ",");
				}
				
				if( splits[7].equals("rural") )
					writer.write("false\n");
				else if( splits[7].equals("urban"))
					writer.write("true\n");
				else throw new Exception("Parsing error " + splits[6]);
			}
			
		}
		
		writer.flush();  writer.close();
		
		reader.close();
		
	}
	
	public static void main(String[] args) throws Exception
	{
		for( int x=1; x < NewRDPParserFileLine.TAXA_ARRAY.length; x++)
		{
			String taxa = NewRDPParserFileLine.TAXA_ARRAY[x];
			System.out.println(taxa);
			
			File inFile = new File(ConfigReader.getChinaDir() + File.separator + 
				taxa + 	"_taxaAsColumnsLogNorm_WithMetadata.txt");
			
			writeForATimepoint(inFile, "first_A", taxa);
			writeForATimepoint(inFile, "second_B", taxa);			
		}
	}
}
