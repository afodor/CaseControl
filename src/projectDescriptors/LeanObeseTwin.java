package projectDescriptors;

import java.io.File;
import java.util.HashSet;

import utils.ConfigReader;

public class LeanObeseTwin extends AbstractProjectDescription
{
	@Override
	public String getProjectName()
	{
		return "leanobesetwin";
	}
	
	@Override
	public String getCountFileKraken(String taxa) throws Exception
	{
		return ConfigReader.getMergedArffDir() + File.separator + 
				"mz16Sstudies" + File.separator + "leanobesetwin" + File.separator +"leanobesetwin_minikraken_taxaAsCol_withMeta_"
				+ taxa +".txt";
	}
	
	@Override
	public HashSet<String> getPositiveClassifications()
	{
		HashSet<String> set = new HashSet<String>();
		set.add("Obese");
		//set.add("Overweight");
		return set;
	}
	
	@Override
	public HashSet<String> getNegativeClassifications()
	{
		HashSet<String> set = new HashSet<String>();
		set.add("Lean");
		return set;
	}
}
