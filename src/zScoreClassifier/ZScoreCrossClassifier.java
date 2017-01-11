package zScoreClassifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

import kraken.RunAllClassifiers;
import projectDescriptors.AbstractProjectDescription;
import utils.ConfigReader;
import zScoreClassifier.ZScoreClassifier.ReturnObject;
import zScoreClassifier.ZScoreClassifier.ZHolder;

public class ZScoreCrossClassifier
{
	public static void main(String[] args) throws Exception
	{
		List<AbstractProjectDescription> list = 
				RunAllClassifiers.getAllProjects();
		
		String taxa = "genus";
		
		for( int x=0; x < list.size(); x++)
		{
			ReturnObject rox = 
					ZScoreClassifier.getFinalIteration(list.get(x), taxa);
			
			if( rox.includedSamples.size() != 0)
			{
				for(int y=0; y < list.size(); y++)
				{
					if( x != y)
					{
						ReturnObject roy = 
								ZScoreClassifier.getFinalIteration(list.get(y), taxa);
						
						System.out.println(taxa + "_"+ list.get(x).getProjectName() + " " + 
								list.get(y).getProjectName());	
						
						if(roy.includedSamples.size() != 0)
						{
							writeCross(list.get(x), list.get(y), rox, roy, taxa);
							writeCross(list.get(y), list.get(x), roy, rox, taxa);
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
