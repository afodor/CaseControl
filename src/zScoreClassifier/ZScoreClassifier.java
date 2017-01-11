package zScoreClassifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

import kraken.RunAllClassifiers;
import kraken.inference.RunAllTTests;
import kraken.inference.RunAllTTests.CaseControlHolder;
import projectDescriptors.AbstractProjectDescription;
import utils.Avevar;
import utils.ConfigReader;
import utils.TTest;

public class ZScoreClassifier
{
	private static class ZHolder
	{
		double caseAvg;
		double controlAvg;
		double pooledSD;
		CaseControlHolder ccHolder;
	}
	
	public static void main(String[] args) throws Exception
	{
		String taxa = "genus";
		
		for(AbstractProjectDescription apd : RunAllClassifiers.getAllProjects())
		{
			System.out.println(apd.getProjectName());
			HashMap<String, ZHolder> map = getZHolderMap(apd, taxa);
			System.out.println(map.size());
			writeZScoreVsCategory(apd, taxa, map);
		}
		
	}
	
	private static void writeZScoreVsCategory(AbstractProjectDescription apd,
				String taxa,HashMap<String, ZHolder> zMap ) throws Exception
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
				ConfigReader.getMergedArffDir() + File.separator + 
				"zScores" + File.separator + apd.getProjectName() + "_" + taxa + 
					"_zScoresVsClass.txt")));
		
		writer.write("sampleId\tassignment\tcaseControl\tcaseScore\tcontrolScore\tcall\tdiff\tcorrect\n");
		
		BufferedReader reader = new BufferedReader(new FileReader(new 
				File(apd.getLogFileKrakenCommonScale(taxa))));
		
		String[] topSplits = reader.readLine().split("\t");
		
		for(String s = reader.readLine(); s != null; s = reader.readLine())
		{
			String[] splits = s.split("\t");
			
			String caseControl = splits[1];
			
			if( apd.getPositiveClassifications().contains(caseControl) || 
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
				
				double caseScore = getScore(zMap, splits, topSplits, true);
				double controlScore = getScore(zMap, splits, topSplits, false);
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
	
	private static double getScore( HashMap<String, ZHolder> zMap, String[] splits, 
									String[] topSplits,
									boolean isCase)
	{
		double sum =0;
		
		for( int x=2; x < splits.length; x++)
		{
			Double val = Double.parseDouble(splits[x]);
			String key = topSplits[x];
			
			ZHolder zh = zMap.get(key);
			
			if( zh != null)
			{
				double top = Math.abs( (val - (isCase ? zh.caseAvg : zh.controlAvg)));
				sum += top / zh.pooledSD;
			}
			
		}
		
		return sum;
	}
	
	private static HashMap<String, ZHolder> getZHolderMap( AbstractProjectDescription apd,
					String taxa) throws Exception
	{
		HashMap<String, CaseControlHolder> caseControlmap = 
				RunAllTTests.getCaseControlMap(apd, taxa, 
							apd.getLogFileKrakenCommonScale(taxa));
		
		HashMap<String, ZHolder> returnMap = new HashMap<String, ZHolder>();
		
		for(String s : caseControlmap.keySet())
		{
			boolean tTestOk = false;
			
			CaseControlHolder cch = caseControlmap.get(s);
			
			if( returnMap.containsKey(s))
				throw new Exception("No");
			
			try
			{
				TTest.ttestFromNumberUnequalVariance(cch.caseVals, cch.controlVals);
				tTestOk = true;
			}
			catch(Exception ex)
			{
				
			}
			
			if(tTestOk)
			{

				ZHolder zh = new ZHolder();
				returnMap.put(s,zh);
				
				Avevar caseA = new Avevar(cch.caseVals);
				Avevar controlA = new Avevar(cch.controlVals);
				
				zh.caseAvg = caseA.getAve();
				zh.controlAvg = controlA.getAve();
				
				zh.pooledSD = Math.sqrt(caseA.getVar()/cch.caseVals.size() 
											+ controlA.getVar()/cch.controlVals.size());
			}
		}
		
		return returnMap;
	}
}
