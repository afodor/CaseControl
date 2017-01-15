package ratioSpace;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import examples.TestClassify;
import projectDescriptors.AbstractProjectDescription;
import projectDescriptors.AllButOne;
import utils.ConfigReader;
import weka.classifiers.trees.RandomForest;
import weka.gui.visualize.ThresholdVisualizePanel;

public class RunClassifiersRatioSpace
{
	public static String[] TAXA_ARRAY
		= { "phylum", "class", "order", "family", "genus"};
	
	
	private static void writeResults(String taxa, HashMap<String, List<Double>> results) 
				throws Exception
	{
		System.out.println( "allProjects_" + taxa + ".txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
			ConfigReader.getMergedArffDir() + File.separator + "allProjectsRatioSpace_" + taxa + ".txt"	)));
		
		List<String> list = new ArrayList<String>( results.keySet());
		
		writer.write("iteration");
		
		for(String s : list )
			writer.write("\t" + s);
		
		writer.write("\n");
		
		int length = results.get(list.get(0)).size();
		
		for( int x=0; x < length; x++)
		{
			writer.write("" + (x+1));
			
			for(String s : list) 
				writer.write("\t" + results.get(s).get(x));
				
			writer.write("\n");
			
		}
		
		
		writer.flush();  writer.close();
		
	}
	
	public static void main(String[] args) throws Exception
	{
		int numPermutations = 10;
		List<AbstractProjectDescription> projects = AllButOne.getLeaveOneOutBaseProjects();
		
		for( int x=0; x < TAXA_ARRAY.length ; x++)
		{
			HashMap<String, List<Double>> resultsMap = new LinkedHashMap<String,List<Double>>();
			
			String taxa = TAXA_ARRAY[x];
			for( AbstractProjectDescription apd : projects)
			{
				File inArff= new File(apd.getNormalizedByBacteroidetesArff(taxa));

				ThresholdVisualizePanel tvp = TestClassify.getVisPanel(
					apd.getProjectName() + " " + taxa	);
				
				String ratioSpace= apd.getProjectName() + "_ratio";
				String linearSpace = apd.getProjectName() + "_linear";
				String scrambled= apd.getProjectName() + "_" +"_scrambled";
				
				if( resultsMap.containsKey(ratioSpace) || resultsMap.containsKey(scrambled))
					throw new Exception("duplicate");
				
				resultsMap.put(ratioSpace, 
				TestClassify.plotRocUsingMultithread(
					inArff, numPermutations, false, tvp, new RandomForest().getClass().getName(), 
						Color.BLACK));
				
				inArff = new File(apd.getLinearArffFileKrakenCommonScale(taxa));

				resultsMap.put(linearSpace,
				TestClassify.plotRocUsingMultithread(
						inArff, numPermutations, false, tvp, new RandomForest().getClass().getName(), 
							Color.GREEN));
				
				resultsMap.put(scrambled, 
						TestClassify.plotRocUsingMultithread(
							inArff, numPermutations, true, tvp, new RandomForest().getClass().getName(), 
								Color.RED));
			}	
			
			writeResults(taxa, resultsMap);
		}
		
	}	
}
