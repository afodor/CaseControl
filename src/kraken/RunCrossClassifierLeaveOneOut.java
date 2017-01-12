package kraken;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import examples.TestClassify;
import projectDescriptors.AbstractProjectDescription;
import projectDescriptors.AllButOne;
import utils.ConfigReader;
import weka.classifiers.trees.RandomForest;
import weka.gui.visualize.ThresholdVisualizePanel;

public class RunCrossClassifierLeaveOneOut
{
	public static void main(String[] args) throws Exception
	{
		List<AbstractProjectDescription> projects = new ArrayList<>(AllButOne.getLeaveOneOutBaseProjects());
		String classifierName = new RandomForest().getClass().getName();
		
		//for( int t =0; t < RunAllClassifiers.TAXA_ARRAY.length; t++)
		{ 
			//String taxa = RunAllClassifiers.TAXA_ARRAY[t];
			String taxa = "genus";
			
			HashMap<String, List<Double>> resultsMap = new LinkedHashMap<String,List<Double>>();
			
			for( AbstractProjectDescription apd : projects)
			{
				AbstractProjectDescription allButOne = new AllButOne(projects, apd);
				File trainFile =new File(allButOne.getLogArffFileKrakenCommonScaleCommonNamespace(taxa));
				File testFile = new File(apd.getLogArffFileKrakenCommonScaleCommonNamespace(taxa));
				
				String key = allButOne.getProjectName() + "_vs_" + apd.getProjectName();
				
				ThresholdVisualizePanel tvp = TestClassify.getVisPanel( taxa+ " "+apd.getProjectName());
				List<Double> results = new ArrayList<Double>();
				resultsMap.put(key, results);
				
				
				results.addAll( RunCrossClassifiers.getPercentCorrect(trainFile, testFile, 1,false, tvp, classifierName, Color.RED));
				results.addAll(RunCrossClassifiers.getPercentCorrect(trainFile, testFile, 100, true, tvp, classifierName, Color.BLACK));
				
				String outFilePath = 
					ConfigReader.getMergedArffDir() 
					+ File.separator + "cross_" + taxa+ "LeaveOneOut.txt";
			
				RunCrossClassifiers.writeResults(resultsMap, taxa, classifierName, outFilePath);
			}
		}
	}
}
