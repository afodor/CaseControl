package zScoreClassifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import kraken.RunAllClassifiers;
import kraken.inference.RunAllTTests.CaseControlHolder;
import projectDescriptors.AbstractProjectDescription;
import projectDescriptors.AllButOne;
import utils.Avevar;
import utils.ConfigReader;
import utils.StatisticReturnObject;
import utils.TTest;

public class ZScoreClassifier
{
	static class ZHolder implements Comparable<ZHolder>
	{
		double caseAvg;
		double controlAvg;
		double pooledSD;
		double pValue=1;
		double rank;
		
		@Override
		public int compareTo(ZHolder o)
		{
			return Double.compare(this.pValue, o.pValue);
		}
	}
	
	static class ReturnObject
	{
		HashMap<String, ZHolder> taxaMap;
		HashSet<String> includedSamples;
	}
	
	public static void main(String[] args) throws Exception
	{
		for( int t=0;t < RunAllClassifiers.TAXA_ARRAY.length ; t++)
		{
			String taxa = RunAllClassifiers.TAXA_ARRAY[t];
			System.out.println(taxa);
			
			HashSet<String> candidates = new HashSet<String>();
			
			List<HashMap<String, ZHolder>> tTestList = 
					new ArrayList<HashMap<String, ZHolder>>();
			
			for(AbstractProjectDescription apd : AllButOne.getLeaveOneOutBaseProjects())
			{
				HashMap<String, ZHolder> map = getZHolderMap(apd, taxa, null);
				tTestList.add( map );
				
				for(String s : map.keySet())
					candidates.add(s);
				
				List<ZHolder> zList = new ArrayList<ZHolder>(map.values());
				Collections.sort(zList);
				
				for( int x=0; x < zList.size(); x++)
				{
					zList.get(x).rank =((double)x)/zList.size();
				}
			}
			
			System.out.println("Have " + candidates.size());
			
			HashSet<String> inAtThreshold = new HashSet<String>();
			
			for(String s : candidates)
			{
				boolean isInSet = true;
				
				for( HashMap<String, ZHolder> map : tTestList)
				{
					if( ! map.containsKey(s) || map.get(s).rank > .1)
						isInSet = false;
				}
				
				if( isInSet)
					inAtThreshold.add(s);			
			}
			
			System.out.println("Now " + inAtThreshold.size());
			
			for(AbstractProjectDescription apd : AllButOne.getLeaveOneOutBaseProjects())
				writeMap(apd, taxa, getZHolderMap(apd, taxa, null), inAtThreshold);
			
			for(AbstractProjectDescription apd : AllButOne.getLeaveOneOutBaseProjects())
			{
				//getFinalIteration(apd, taxa);
			}	
		}
	}
	
	private static void writeMap( AbstractProjectDescription apd, String taxa ,
			HashMap<String, ZHolder> map, HashSet<String> thresholdMap) throws Exception
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
				ConfigReader.getMergedArffDir() + File.separator + 
				"zHolderMap_"  + apd.getProjectName() + "_" + taxa +".txt")));
		
		writer.write("taxa\tcaseAvg\tcontrolAvg\tpooledSD\tcaseMinusControl\tpValueTTest\tinThresholdGroup\n");
		
		for(String s : map.keySet())
		{
			ZHolder zh = map.get(s);
			
			writer.write( s + "\t" + zh.caseAvg + "\t" + zh.controlAvg + "\t" + zh.pooledSD + "\t");
			writer.write( (zh.caseAvg - zh.controlAvg ) + "\t" );
			writer.write( zh.pValue + "\t" + (thresholdMap == null ? "NA" : thresholdMap.contains(s)) + 
					"\n");
		}
		
		writer.flush(); writer.close();
	}
	
	static ReturnObject getFinalIteration( 
			AbstractProjectDescription apd, String taxa ) throws Exception
	{
		System.out.println(apd.getProjectName());
		HashMap<String, ZHolder> map = getZHolderMap(apd, taxa, null);
		
		HashSet<String> includeSet = writeZScoreVsCategory(apd, taxa, map,0,null);
	
		int oldSize = includeSet.size();
		int iteration = 0;
		boolean keepGoing =true;
		System.out.println(iteration + " " + includeSet.size());
		
		while(keepGoing)
		{
			iteration++;
			map = getZHolderMap(apd, taxa, includeSet);
			includeSet=  writeZScoreVsCategory(apd, taxa, map,iteration,includeSet);
			
			if( includeSet.size() == 0 || oldSize == includeSet.size())
				keepGoing = false;

			System.out.println(iteration + " " + includeSet.size());
			
			oldSize = includeSet.size();
		}
		
		ReturnObject ro = new ReturnObject();
		
		ro.taxaMap = map;
		ro.includedSamples = includeSet;
		return ro;
	}
	
	private static HashSet<String> writeZScoreVsCategory(AbstractProjectDescription apd,
				String taxa,HashMap<String, ZHolder> zMap, int interation ,
				HashSet<String> includeSet) throws Exception
	{
		HashSet<String> set = new HashSet<String>();
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
				ConfigReader.getMergedArffDir() + File.separator + 
				"zScores" + File.separator + apd.getProjectName() + "_" + taxa + 
					"_zScoresVsClass_" + interation + ".txt")));
		 
		writer.write("sampleId\tassignment\tcaseControl\tcaseScore\tcontrolScore\tcall\tdiff\tcorrect\n");
		
		BufferedReader reader = new BufferedReader(new FileReader(new 
				File(apd.getLogFileKrakenCommonScale(taxa))));
		
		String[] topSplits = reader.readLine().split("\t");
		
		for(String s = reader.readLine(); s != null; s = reader.readLine())
		{
			String[] splits = s.split("\t");
			
			if(includeSet == null || includeSet.contains(splits[0]))
			{

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
					
					if(set.contains(splits[0]))
						throw new Exception("Duplicate sample name " + splits[0]);
					
					if( call.equals(classification))
						set.add(splits[0]);				
				}
			}
		}
		
		reader.close();
		writer.flush();  writer.close();
		return set;
	}
	
	static double getScore( HashMap<String, ZHolder> zMap, String[] splits, 
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
					String taxa, HashSet<String> includeSet) throws Exception
	{
		HashMap<String, CaseControlHolder> caseControlmap = 
				getCaseControlMap(apd, taxa, 
							apd.getLogFileKrakenCommonScale(taxa), includeSet);
		
		HashMap<String, ZHolder> returnMap = new HashMap<String, ZHolder>();
		
		for(String s : caseControlmap.keySet())
		{
			boolean tTestOk = false;
			
			CaseControlHolder cch = caseControlmap.get(s);
			
			if( returnMap.containsKey(s))
				throw new Exception("No");
			
			double pValue = 1;
			
			try
			{
				StatisticReturnObject sro = 
						TTest.ttestFromNumberUnequalVariance(cch.caseVals, cch.controlVals);
				tTestOk = true;
				pValue = sro.getPValue();
				
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
				
				zh.pValue = pValue;
			}
		}
		
		return returnMap;
	}
	
	public static HashMap<String, CaseControlHolder> getCaseControlMap( AbstractProjectDescription apd ,
			String taxa, String logNormalizedFilePath, HashSet<String> includeSet)
		throws Exception
	{
		HashMap<String, CaseControlHolder> map = new HashMap<String, CaseControlHolder>();
		
		BufferedReader reader = new BufferedReader(new FileReader(logNormalizedFilePath));
		
		String[] topLine = reader.readLine().split("\t");
		
		for( int i =2; i < topLine.length; i++)
		{
			String key = topLine[i];
			
			if( map.containsKey(key))
				throw new Exception("Duplicate key " + key);
			
			map.put(key, new CaseControlHolder());
			
		}
		
		for(String s = reader.readLine(); s != null; s = reader.readLine())
		{
			String[] splits = s.split("\t");
		
			if(includeSet== null || includeSet.contains(splits[0]))
			{	
				if( splits.length != topLine.length)
					throw new Exception("Parsing error");
				
				for( int i=2; i < topLine.length; i++)
				{
					CaseControlHolder cch = map.get(topLine[i]);
					
					if( apd.getPositiveClassifications().contains(splits[1]) )
						cch.caseVals.add(Double.parseDouble(splits[i]));
					else if( apd.getNegativeClassifications().contains(splits[1]) )
						cch.controlVals.add(Double.parseDouble(splits[i]));
					//else 
					//	System.out.println("Skipping " + splits[1]);
					
				}
			}
		}
		
		return map;
	}
}
