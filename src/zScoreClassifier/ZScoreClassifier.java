package zScoreClassifier;

import java.util.HashMap;

import kraken.RunAllClassifiers;
import kraken.inference.RunAllTTests;
import kraken.inference.RunAllTTests.CaseControlHolder;
import projectDescriptors.AbstractProjectDescription;
import utils.Avevar;
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
			
		}
		
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
