package zScoreClassifier;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import kraken.RunAllClassifiers;
import kraken.RunCrossClassifiers;
import projectDescriptors.AbstractProjectDescription;
import utils.ConfigReader;
import weka.classifiers.trees.RandomForest;
import weka.gui.visualize.ThresholdVisualizePanel;

public class RunCrossClassifiersReduced
{
	public static void main(String[] args) throws Exception
	{
		List<AbstractProjectDescription> projectList = RunAllClassifiers.getAllProjects();
		
		//for( int t =0; t < RunAllClassifiers.TAXA_ARRAY.length; t++)
		{ 
		
			//String taxa = RunAllClassifiers.TAXA_ARRAY[t];
			String taxa = "genus";
			
			HashMap<String, List<Double>> resultsMap = new LinkedHashMap<String,List<Double>>();
				
			for(int x=0; x < projectList.size(); x++)
				for( int y=0; y < projectList.size(); y++)
					if( x != y )
					{
						File xFile = new File(
								projectList.get(x).getZScoreFilteredLogNormalKrakenToCommonNamespaceArff(taxa));
						
						File yFile = new File(
								//projectList.get(y).getZScoreFilteredLogNormalKrakenToCommonNamespaceArff(taxa));
								projectList.get(y).getLogArffFileKrakenCommonScaleCommonNamespace(taxa));
								
						if( xFile.exists() && yFile.exists())
						{
							AbstractProjectDescription xProject = projectList.get(x);
							AbstractProjectDescription yProject = projectList.get(y);
							System.out.println(taxa + " " + xProject + " "+yProject );
							//ThresholdVisualizePanel tvp = TestClassify.getVisPanel( taxa+ " "+
								//xProject.getProjectName() + " " + yProject.getProjectName() );
							ThresholdVisualizePanel tvp = null;
							
							String key = xProject.getProjectName() + "_vs_" + yProject.getProjectName();
							System.out.println( taxa + " " +  key);
							List<Double> results = new ArrayList<Double>();
							resultsMap.put(key, results);
							
							File trainFile =xFile;
							File testFile = yFile;
							String classifierName = new RandomForest().getClass().getName();
							
							results.addAll(  RunCrossClassifiers.getPercentCorrect(trainFile, testFile, 1, false, tvp, classifierName, Color.RED));
							results.addAll(RunCrossClassifiers.getPercentCorrect(trainFile, testFile, 100, true, tvp, classifierName, Color.BLACK));
							writeResults(resultsMap, taxa, classifierName);
						}
					}
			}
	}
	
	private static void writeResults( HashMap<String, List<Double>> resultsMap , String level,
			String classifierName)
		throws Exception
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
			ConfigReader.getMergedArffDir() 
				+ File.separator + "cross_" + level + "kraken_" +classifierName+ "_reducedToFull.txt"	)));
		
		writer.write( "count\tisScrambled"  );
		
		List<String> keys = new ArrayList<>(resultsMap.keySet());
		
		for(String s : keys)
			writer.write("\t" + s);
		
		writer.write("\n");
		
		int size = resultsMap.get(keys.get(0)).size();
		
		for( int x=0; x < size; x++)
		{
			writer.write((x+1) + "\t");
			writer.write( (x != 0) + "");
			
			for(String key : keys)
				writer.write("\t" + resultsMap.get(key).get(x));
			
			writer.write("\n");
		}
		
		writer.flush();  writer.close();
	}
	
}
