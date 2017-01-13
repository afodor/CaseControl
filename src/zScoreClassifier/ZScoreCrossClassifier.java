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

public class ZScoreCrossClassifier
{
	public static void main(String[] args) throws Exception
	{
		List<AbstractProjectDescription> projects = new ArrayList<>(AllButOne.getLeaveOneOutBaseProjects());
		projects.addAll(AllButOne.getLeaveOneOutProjects());
		
		
		//	for( int t=0;t < RunAllClassifiers.TAXA_ARRAY.length ; t++)
			{
				String taxa = "genus"; //RunAllClassifiers.TAXA_ARRAY[t];
			
			for( int x=0; x < projects.size(); x++)
			{
				ReturnObject rox = 
						ZScoreClassifier.getFinalIteration(projects.get(x), taxa,true);
				
				if( rox.includedSamples.size() != 0)
				{
					for(int y=0; y < projects.size(); y++)
					{
						ReturnObject roy = 
									ZScoreClassifier.getFinalIteration(projects.get(y), taxa,true);
							
						System.out.println(taxa + "_"+ projects.get(x).getProjectName() + " " + 
									projects.get(y).getProjectName());	
							
						if(roy.includedSamples.size() != 0)
						{
								writeCross(projects.get(x), projects.get(y), rox, roy, taxa);
								writeCross(projects.get(y), projects.get(x), roy, rox, taxa);
						}
					}
				}
			}
		}
	}
	
	// uses x as the training set and y as the test
	private static void writeCross( AbstractProjectDescription xAPD, 
									AbstractProjectDescription yAPD,
									ReturnObject xObj,
									ReturnObject yObj,
									String taxa) throws Exception
	{

		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
				ConfigReader.getMergedArffDir() + File.separator + 
				"zScores" + File.separator + "cross" + File.separator + 
				xAPD.getProjectName() + "_vs" + 
						yAPD.getProjectName() + "_" +  taxa + 
					"_zScores_" + taxa + ".txt")));
		 
		writer.write("sampleId\tassignment\tcaseControl\tcaseScore\tcontrolScore\tcall\tdiff\tcorrect\n");
		
		BufferedReader reader = new BufferedReader(new FileReader(new 
				File(yAPD.getLogFileKrakenCommonScale(taxa))));
		
		String[] topSplits = reader.readLine().split("\t");
		
		for(String s = reader.readLine(); s != null; s = reader.readLine())
		{
			String[] splits = s.split("\t");
			
			if(yObj.includedSamples.contains(splits[0]))
			{
				String caseControl = splits[1];
				
				writer.write(splits[0] + "\t" + splits[1]);
					
				String classification = null;
					
				if( yAPD.getPositiveClassifications().contains(caseControl))
						classification = "case";
				else if(yAPD.getNegativeClassifications().contains(caseControl))
						classification = "control";
				else throw new Exception("Logic error");
					
				writer.write("\t" + classification);
					
				double caseScore =  ZScoreClassifier.getScore(xObj.taxaMap, splits, topSplits, true);
				double controlScore = ZScoreClassifier.getScore(xObj.taxaMap, splits, topSplits, false);
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
