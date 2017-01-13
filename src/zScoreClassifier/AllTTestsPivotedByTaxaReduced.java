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
		writeTests(false);
		writeTests(true);
	}
	
	public static void writeTests(boolean useLogScale) throws Exception
	{
		List<AbstractProjectDescription> projects = new ArrayList<>(AllButOne.getLeaveOneOutBaseProjects());
		projects.addAll(AllButOne.getLeaveOneOutProjects());
		
		for( int x=0; x < RunAllClassifiers.TAXA_ARRAY.length; x++)
		{
			String taxa = RunAllClassifiers.TAXA_ARRAY[x];
			System.out.println(taxa);
			
			HashMap<String, HashMap<String,TTestResultsHolder>> 
				map = getAllTTests(projects, taxa,useLogScale);
			
			AllTTestsPivotedByTaxa.
			 writePivot(map, projects, taxa, new File(ConfigReader.getMergedArffDir() + File.separator 
						+ "allTTestsPivotedReduced_" + taxa + (useLogScale ? "_log" : "_linear" )
					 +  "scale.txt"));
		}
	}
	
	// outer string is projectID@method
	// inner string is taxa
	private static HashMap<String, HashMap<String,TTestResultsHolder>> 
		getAllTTests(List<AbstractProjectDescription> projects, String taxa,
				boolean useLogScale) throws Exception
	{
		HashMap<String, HashMap<String,TTestResultsHolder>>   map = 
				new HashMap<String, HashMap<String,TTestResultsHolder>>();
		
		for(AbstractProjectDescription apd : projects)
		{
			if( new File( apd.getZScoreFilteredLogNormalKraken(taxa)).exists())
			{
				AllTTestsPivotedByTaxa.addOne(apd, 
					useLogScale ?
							apd.getZScoreFilteredLogNormalKraken(taxa) : 
							apd.getZScoreFilteredLinearNormalKraken(taxa), 
							taxa, map, AbstractProjectDescription.KRAKEN);
			}
		}
		
		return map;
	}
	
	
}
