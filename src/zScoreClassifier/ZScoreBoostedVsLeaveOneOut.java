package zScoreClassifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import projectDescriptors.AbstractProjectDescription;
import projectDescriptors.AllButOne;
import utils.ConfigReader;
import zScoreClassifier.ZScoreClassifier.ReturnObject;

public class ZScoreBoostedVsLeaveOneOut
{
	public static void main(String[] args) throws Exception
	{
		List<AbstractProjectDescription> projects = new ArrayList<>(AllButOne.getLeaveOneOutBaseProjects());
		
		//	for( int t=0;t < RunAllClassifiers.TAXA_ARRAY.length ; t++)
		{
			String taxa = "genus"; //RunAllClassifiers.TAXA_ARRAY[t];
			
			for( int x=0; x < projects.size(); x++)
			{
				AbstractProjectDescription apd = projects.get(x);
				System.out.println(apd.getProjectName());
				AbstractProjectDescription allButOne = new AllButOne(projects, apd);
				
				ReturnObject rox = ZScoreClassifier.getFinalIteration(allButOne, taxa,null);
				
				BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
						ConfigReader.getMergedArffDir() + File.separator + 
						"zScores" + File.separator + "cross" + File.separator + 
						allButOne.getProjectName() + "_vs" + 
								apd.getProjectName() + "_" +  taxa + 
							"_zScores_" + taxa + ".txt")));

				writer.write("sampleId\tassignment\tcaseControl\tcaseScore\tcontrolScore\tcall\tdiff\tcorrect\n");
				
				BufferedReader reader = new BufferedReader(new FileReader(new 
						File(apd.getLogFileKrakenCommonScale(taxa))));
				
				String[] topSplits = reader.readLine().split("\t");
				for(String s = reader.readLine(); s != null; s = reader.readLine())
				{
					String[] splits = s.split("\t");
					
					String caseControl = splits[1];
					
					if(apd.getPositiveClassifications().contains(caseControl) || 
							apd.getNegativeClassifications().contains(caseControl)) 
					{
						writer.write(splits[0] + "\t" + splits[1]);
						
						String classification = null;
						
						if( apd.getPositiveClassifications().contains(caseControl))
									classification = "case";
						else if(apd.getNegativeClassifications().contains(caseControl))
									classification = "control";
						else throw new Exception("Logic error");
							
						writer.write("\t" + classification);
						
						double caseScore =  ZScoreClassifier.getScore(rox.taxaMap, splits, topSplits, true);
						double controlScore = ZScoreClassifier.getScore(rox.taxaMap, splits, topSplits, false);
						writer.write("\t" + caseScore + "\t" + controlScore );
							
						String call = null;
							
						if( caseScore < controlScore)
							call = "case";
						else
							call = "control";
							
						writer.write("\t" +  call +  "\t" + (caseScore - controlScore) + "\t" +
								call.equals(classification) + "\n");	
					}
						
				}
				
				reader.close();
				writer.flush();  writer.close();
			}
		}
	}
}
