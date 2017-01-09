package projectDescriptors;

import java.io.File;
import java.util.HashSet;

import utils.ConfigReader;

public class China2015_wgs extends AbstractProjectDescription
{

	@Override
	public String getProjectName()
	{
		return "China2015_wgs";
	}

	@Override
	public String getCountFileKraken(String taxa) throws Exception
	{
		return ConfigReader.getMergedArffDir() + File.separator + 
				"kwinglee_kraken" + File.separator + 
				"China_WGS_minikraken_merged_taxaAsCol_" + taxa + ".txt";
	}
	
	@Override
	public HashSet<String> getPositiveClassifications()
	{
		HashSet<String> set = new HashSet<String>();
		set.add("urban");
		return set;
	}

	@Override
	public HashSet<String> getNegativeClassifications()
	{
		HashSet<String> set = new HashSet<String>();
		set.add("rural");
		return set;
	}
	
}
