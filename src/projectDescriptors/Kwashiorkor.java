package projectDescriptors;

import java.io.File;
import java.util.HashSet;

import utils.ConfigReader;

public class Kwashiorkor extends AbstractProjectDescription
{
	@Override
	public String getProjectName()
	{
		return "kwashiorkor";
	}
	
	@Override
	public String getCountFileKraken(String taxa) throws Exception
	{
		return ConfigReader.getMergedArffDir() + File.separator + 
				"mz16Sstudies" + File.separator + "kwashiorkor" + File.separator +"kwashiorkor_minikraken_taxaAsCol_withMeta_"
				+ taxa +".txt";
	}
	
	@Override
	public HashSet<String> getPositiveClassifications()
	{
		HashSet<String> set = new HashSet<String>();
		set.add("Marasmus");
		set.add("Kwashiorkor");
		return set;
	}
	
	@Override
	public HashSet<String> getNegativeClassifications()
	{
		HashSet<String> set = new HashSet<String>();
		set.add("Healthy");
		set.add("Moderate");
		return set;
	}
}