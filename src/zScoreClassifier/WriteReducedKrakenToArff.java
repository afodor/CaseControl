package zScoreClassifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import kraken.WriteKrakenToArff;
import projectDescriptors.AbstractProjectDescription;

public class WriteReducedKrakenToArff
{
	// todo: The duplicate code in this class could be eliminated by
	// having this method take an in file and an out file...
	public static void writeArffFromLogNormalKrakenCounts(AbstractProjectDescription apb, String taxa)
		throws Exception
	{
		File inFile = new File(apb.getZScoreFilteredLogNormalKraken(taxa));
		System.out.println(inFile.getAbsolutePath());
		File outFile = new File(apb.getZScoreFilteredLogNormalKrakenToArff(taxa));
		int numSamples = WriteKrakenToArff.getNumSamples(inFile, apb);
		System.out.println("Got" + numSamples);
		BufferedReader reader = new BufferedReader(new FileReader(inFile));
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
		
		writer.write("% " + taxa + "\n");
		
		writer.write("@relation " + apb.getProjectName() +  "\n");
		
		String[] topSplits = reader.readLine().replaceAll("\"","").split("\t");
		
		for( int y=0; y < topSplits.length; y++)
		{
			if( y >= 2)
			{
				String attribute = topSplits[y].replaceAll(" ", "_").
						replaceAll("shannonEntropy", "shannonDiversity").replaceAll(",", "_");

				
				writer.write("@attribute " + attribute + " numeric\n");

			}
		}
		
		writer.write("@attribute isCase { true, false }\n");
		
		writer.write("\n\n@data\n");
		
		writer.write("%\n% " + numSamples + " instances\n%\n");
		
		for( String s= reader.readLine(); s != null; s = reader.readLine())
		{
			s = s.replaceAll("\"", "");
			String[] splits = s.split("\t");
			
			if( apb.getNegativeClassifications().contains(splits[1]) ||  
					 apb.getPositiveClassifications().contains(splits[1]))
			{

				if( splits.length != topSplits.length)
					throw new Exception("Parsing error!");
				
				
				for( int y=0; y < splits.length; y++)
				{ 
					if( y>=2 )
						writer.write( splits[y] + ",");
				}
			
				if( apb.getNegativeClassifications().contains(splits[1]))
					writer.write("false\n");
				else if( apb.getPositiveClassifications().contains(splits[1]))
					writer.write("true\n");
				else throw new Exception("Logic error");
			}
			else
			{
				System.out.println("Skipping " + splits[0]);
			}
			
		}
		
		writer.flush();  writer.close();
		
		reader.close();
		
	}
}
