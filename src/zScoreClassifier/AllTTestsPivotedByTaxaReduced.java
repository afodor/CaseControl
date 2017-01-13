package zScoreClassifier;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kraken.RunAllClassifiers;
import kraken.inference.AllTTestsPivotedByTaxa;
import kraken.inference.RunAllTTests.TTestResultsHolder;
import projectDescriptors.AbstractProjectDescription;
import projectDescriptors.AllButOne;
import utils.ConfigReader;

public class AllTTestsPivotedByTaxaReduced
{
	public static void main(String[] args) throws Exception
	{
		List<AbstractProjectDescription> projects = new ArrayList<>(AllButOne.getLeaveOneOutBaseProjects());
		projects.addAll(AllButOne.getLeaveOneOutProjects());
		
		for( int x=0; x < RunAllClassifiers.TAXA_ARRAY.length; x++)
		{
			String taxa = RunAllClassifiers.TAXA_ARRAY[x];
			System.out.println(taxa);
			
			HashMap<String, HashMap<String,TTestResultsHolder>> 
				map = getAllTTests(projects, taxa);
			
			AllTTestsPivotedByTaxa.
			 writePivot(map, projects, taxa, new File(ConfigReader.getMergedArffDir() + File.separator 
						+ "allTTestsPivotedReduced_" + taxa + ".txt"));
		}
	}
	
	// outer string is projectID@method
	// inner string is taxa
	private static HashMap<String, HashMap<String,TTestResultsHolder>> 
		getAllTTests(List<AbstractProjectDescription> projects, String taxa) throws Exception
	{
		HashMap<String, HashMap<String,TTestResultsHolder>>   map = 
				new HashMap<String, HashMap<String,TTestResultsHolder>>();
		
		for(AbstractProjectDescription apd : projects)
		{
			if( new File( apd.getZScoreFilteredLogNormalKraken(taxa)).exists())
			{
				AllTTestsPivotedByTaxa.addOne(apd, apd.getZScoreFilteredLogNormalKraken(taxa), taxa, map, AbstractProjectDescription.KRAKEN);
			}
		}
		
		return map;
	}
	
	
}
