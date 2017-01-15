package ratioSpace;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import examples.TestClassify;
import kraken.RunAllClassifiers;
import kraken.RunCrossClassifiers;
import projectDescriptors.AbstractProjectDescription;
import projectDescriptors.AllButOne;
import weka.classifiers.trees.RandomForest;
import weka.gui.visualize.ThresholdVisualizePanel;

public class RunCrossClassifiersRatioSpace
{
	
	public static void main(String[] args) throws Exception
	{
		String classifierName = new RandomForest().getClass().getName();
		
		for( int x=5; x <= RunClassifiersRatioSpace.TAXA_ARRAY.length; x++)
		{
			String taxa = RunAllClassifiers.TAXA_ARRAY[x];
			HashMap<String, List<Double>> resultsMap = new LinkedHashMap<String,List<Double>>();
			
			
			for( int y=0; y < AllButOne.getLeaveOneOutBaseProjects().size(); y++)
			{
				AbstractProjectDescription yProj = AllButOne.getLeaveOneOutBaseProjects().get(y);
				
				for( int z=0; z < AllButOne.getLeaveOneOutBaseProjects().size(); z++)
					if( z != y)
				{
					AbstractProjectDescription zProj = AllButOne.getLeaveOneOutBaseProjects().get(z);
					
					ThresholdVisualizePanel tvp = TestClassify.getVisPanel( taxa+ " "+
							yProj.getProjectName() + " " + zProj.getProjectName() );
					String key = yProj.getProjectName() + "_vs_" + zProj.getProjectName() ;
					System.out.println( taxa + " " +  key);
					List<Double> results = new ArrayList<Double>();
					resultsMap.put(key, results);
					
					File trainFile =new File(
								yProj.getNormalizedByBacteroidetesArffCommonNamespace(taxa));
					File testFile
							=new File(
									zProj.getNormalizedByBacteroidetesArffCommonNamespace(taxa));
							
					results.addAll( RunCrossClassifiers.getPercentCorrect(trainFile, testFile, 1, false, tvp, classifierName, Color.RED));
					results.addAll(RunCrossClassifiers.getPercentCorrect(trainFile, testFile, 100, true, tvp, classifierName, Color.BLACK));
				}
			}
		}
	}
	
}
