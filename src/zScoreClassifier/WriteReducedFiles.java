package zScoreClassifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import kraken.RunAllClassifiers;
import projectDescriptors.AbstractProjectDescription;
import zScoreClassifier.ZScoreClassifier.ReturnObject;

public class WriteReducedFiles
{
	public static void main(String[] args) throws Exception
	{
		String taxa = "genus";
		
		List<AbstractProjectDescription> projectList = new ArrayList<AbstractProjectDescription>();
		
		for(AbstractProjectDescription apd : RunAllClassifiers.getAllProjects())
		{
			ReturnObject ro = ZScoreClassifier.getFinalIteration(apd, taxa);
			
			if( ro.includedSamples.size() > 20)
			{

				BufferedWriter writer = new BufferedWriter(new FileWriter(
						new File(apd.getZScoreFilteredLogNormalKraken(taxa))));
				
				System.out.println(apd.getZScoreFilteredLogNormalKraken(taxa));
				
				BufferedReader reader = new BufferedReader(new FileReader(new File(
						apd.getLogFileKrakenCommonScale(taxa))));
				
				writer.write(reader.readLine() + "\n");
				
				for(String s = reader.readLine(); s != null; s = reader.readLine())
				{
					String[] splits =s.split("\t");
					
					if( ro.includedSamples.contains(splits[0]))
						writer.write(s + "\n");
				}
				
				writer.flush();  writer.close();
				
				WriteReducedKrakenToArff.writeArffFromLogNormalKrakenCounts(apd, taxa);
				projectList.add(apd);
			}		
		}	
		
		BringReducedIntoOneNameSpaceForKraken.writeMergedForOneLevel(projectList, taxa);
	}
}
