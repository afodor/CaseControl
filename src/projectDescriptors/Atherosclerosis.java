package projectDescriptors;

import java.io.File;
import java.util.HashSet;

import utils.ConfigReader;

public class Atherosclerosis  extends AbstractProjectDescription
{
	@Override
	public String getProjectName()
	{
		return "atherosclerosis";
	}
	
	@Override
	public String getCountFileKraken(String taxa) throws Exception
	{
		return ConfigReader.getMergedArffDir() + File.separator + 
				"mz16Sstudies" + File.separator + "atherosclerosis" 
				+ File.separator +"atherosclerosis_minikraken_taxaAsCol_withMeta_"
				+ taxa +".txt";
	}
	
	@Override
	public HashSet<String> getPositiveClassifications()
	{
		HashSet<String> set = new HashSet<String>();
		set.add("TRUE");
		return set;
	}
	
	@Override
	public HashSet<String> getNegativeClassifications()
	{
		HashSet<String> set = new HashSet<String>();
		set.add("FALSE");
		return set;
	}
}

