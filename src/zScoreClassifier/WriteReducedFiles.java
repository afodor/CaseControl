package zScoreClassifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

import kraken.RunAllClassifiers;
import projectDescriptors.AbstractProjectDescription;
import projectDescriptors.AllButOne;
import zScoreClassifier.ZScoreClassifier.ReturnObject;

public class WriteReducedFiles
{
	/*
	 * Something like:
	 * 	kraken.LogAllOnCommonScale
	 *  kraken.WriteKraenToArff
	 *  projectDescriptors.AllButOne
	 *  zScoreClassifier.WriteReducedFiles
	 *  kraken.BringIntoOneNameSpaceForKraken
	 *  kraken.RunCrossClassifiersLeaveOneOut
	 */
	public static void main(String[] args) throws Exception
	{
		writeReducedFiles(AllButOne.getLeaveOneOutProjects(),false);
		writeReducedFiles(RunAllClassifiers.getAllProjects(),false);
		
		writeReducedFiles(AllButOne.getLeaveOneOutProjects(),true);
		writeReducedFiles(RunAllClassifiers.getAllProjects(),true);
	}
	
	public static void writeReducedFiles(List<AbstractProjectDescription> projectList,
			boolean useLogScale) throws Exception
	{
		for( int t=0;t < RunAllClassifiers.TAXA_ARRAY.length ; t++)
		{
				String taxa = RunAllClassifiers.TAXA_ARRAY[t];
			
				System.out.println(taxa);
				
				for(AbstractProjectDescription apd :projectList)
				{
					ReturnObject ro = ZScoreClassifier.getFinalIteration(apd, taxa, useLogScale);
					
					if( ro.includedSamples.size() > 20)
					{
		
						BufferedWriter writer = new BufferedWriter(new FileWriter(
							new File( useLogScale ? 
									apd.getZScoreFilteredLogNormalKraken(taxa) :
										apd.getZScoreFilteredLinearNormalKraken(taxa))));
						
						System.out.println(apd.getZScoreFilteredLogNormalKraken(taxa));
						
						BufferedReader reader = new BufferedReader(new FileReader(new File(
							useLogScale	? apd.getLogFileKrakenCommonScale(taxa) :
										apd.getNonLogFileKrakenCommonScale(taxa))));
						
						writer.write(reader.readLine() + "\n");
						
						for(String s = reader.readLine(); s != null; s = reader.readLine())
						{
							String[] splits =s.split("\t");
							
							if( ro.includedSamples.contains(splits[0]))
								writer.write(s + "\n");
						}
						
						writer.flush();  writer.close();
						
						WriteReducedKrakenToArff.writeArffFromLogNormalKrakenCounts(apd, taxa,useLogScale);
					}		
				}	
		}
	}
}
