package projectDescriptors;

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

import kraken.BringIntoOneNameSpaceForKraken;
import kraken.LogAllOnCommonScale;
import kraken.RunAllClassifiers;
import kraken.WriteKrakenToArff;
import utils.ConfigReader;

public class AllButOne extends AbstractProjectDescription
{
	private final List<AbstractProjectDescription> projectList;
	private final AbstractProjectDescription oneToSkip;

	public AllButOne(List<AbstractProjectDescription> projectList, AbstractProjectDescription oneToSkip)
		throws Exception
	{
		this.projectList = projectList;
		this.oneToSkip = oneToSkip;
	}
	
	@Override
	public String getCountFileKraken(String taxa) throws Exception
	{
		return ConfigReader.getMergedArffDir() + File.separator + 
					"tables" + File.separator + "allButOne" + File.separator + 
						"allBut" + oneToSkip.getProjectName() + "_" + taxa + ".txt";
	}
	
	@Override
	public HashSet<String> getPositiveClassifications()
	{
		HashSet<String> set = new HashSet<String>();
		
		for(AbstractProjectDescription apd : projectList)
			set.addAll(apd.getPositiveClassifications());
		
		return set;
	}
	
	@Override
	public HashSet<String> getNegativeClassifications()
	{
		HashSet<String> set = new HashSet<String>();
		
		for(AbstractProjectDescription apd : projectList)
			set.addAll(apd.getNegativeClassifications());
			
		return set;
	}
	
	@Override
	public String getProjectName()
	{
		return "AllBut_" + oneToSkip.getProjectName();
	}
	
	public void writeMergedCountFile(String taxa) throws Exception
	{
		List<String> taxaList = getAllTaxa(taxa);
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
				this.getCountFileKraken(taxa))));
		
		writer.write("sample\tcaseContol");
		
		for( String s : taxaList)
			writer.write("\t" + s);
		
		writer.write("\n");
		
		for(AbstractProjectDescription apd : projectList)
			if( !apd.getProjectName().equals(oneToSkip.getProjectName()))
			{
				BufferedReader reader = new BufferedReader(new FileReader(
						apd.getCountFileKraken(taxa)));
				
				String[] topSplits = reader.readLine().split("\t");
				
				for(String s = reader.readLine(); s != null; s =reader.readLine())
				{
					String[] splits = s.split("\t");
					
					writer.write( apd.getProjectName() + "_"+  splits[0] + "\t" + splits[1] );
					
					HashMap<String, Double> countMap = getLineAsMap(topSplits, s);
					
					for( String s2 : taxaList)
					{
						Double val = countMap.get(s2);
						
						if( val == null)
							val =0.0;
						
						writer.write("\t" + val);
					}
					
					writer.write("\n");
				}
				
				reader.close();
			}
		
		writer.flush();  writer.close();
	}
	
	private static HashMap<String, Double> getLineAsMap( String[] topSplits, String s)
		throws Exception
	{
		HashMap<String, Double>  map = new HashMap<String,Double>();
		String[] splits = s.split("\t");
		
		if(splits.length != topSplits.length)
			throw new Exception("Parsing error");
		
		for( int x=2; x < topSplits.length; x++)
		{
			if(map.containsKey(topSplits[x]))
				throw new Exception("Duplicate key");
			
			map.put(topSplits[x], Double.parseDouble(splits[x]));
		}
		
		return map;
	}
	
	private List<String> getAllTaxa(String taxa) throws Exception
	{
		HashSet<String> set = new HashSet<String>();
		
		for(AbstractProjectDescription apd : projectList)
		{
			BufferedReader reader = new BufferedReader(
					new FileReader(apd.getCountFileKraken(taxa)));
			
			String[] splits = reader.readLine().split("\t");
			
			for( int x=2; x < splits.length; x++)
				set.add(splits[x]);
			
			reader.close();
		}
		
		List<String> list = new ArrayList<String>(set);
		Collections.sort(list);
		return list;
		
	}
	
	public static List<AbstractProjectDescription> getLeaveOneOutBaseProjects()
	{
		List<AbstractProjectDescription> list = new ArrayList<AbstractProjectDescription>();
		list.add(new China2015_wgs());
		list.add(new WT2D2());
		list.add(new T2D());
		list.add( new CirrhosisQin());
		list.add( new IbdMetaHit());
		list.add( new Obesity());
		return list;
	}
	
	public static List<AbstractProjectDescription> getLeaveOneOutProjects()
		throws Exception
	{
		List<AbstractProjectDescription> list = new ArrayList<AbstractProjectDescription>();
		
		for(AbstractProjectDescription abd : getLeaveOneOutBaseProjects())
			list.add(new AllButOne(getLeaveOneOutBaseProjects(), abd));
		
		return list;
	}
	
	
	public static void main(String[] args) throws Exception
	{
		List<AbstractProjectDescription> list = getLeaveOneOutBaseProjects();
		
		for( int x=0; x <  RunAllClassifiers.TAXA_ARRAY.length ; x++)
		{
			List<AbstractProjectDescription> bigList = new ArrayList<AbstractProjectDescription>(list);
			String taxa = RunAllClassifiers.TAXA_ARRAY[x];
			System.out.println(taxa);
			
			for(AbstractProjectDescription apd : list)
			{
				AllButOne abo = new AllButOne(list, apd);
				abo.writeMergedCountFile(taxa);
				LogAllOnCommonScale.logOne(abo, taxa,true);
				LogAllOnCommonScale.logOne(abo, taxa,false);
				WriteKrakenToArff.writeArffFromLogNormalKrakenCounts(abo, taxa);
				bigList.add(abo);
			}
			
			BringIntoOneNameSpaceForKraken.writeMergedForOneLevel(bigList, taxa);
		}
		
	}
	
}
