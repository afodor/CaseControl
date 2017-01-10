package projectDescriptors;

import java.io.File;
import java.util.HashSet;

import utils.ConfigReader;


public class Divitriculosis2015ProjectDescriptor extends AbstractProjectDescription
{
	@Override
	public String getProjectName()
	{
		return "Divitriculosis2015";
	}
	
	@Override
	public String getCountFileKraken(String taxa) throws Exception
	{
		return ConfigReader.getMergedArffDir() + File.separator + "tables" + File.separator + 
					"Diverticulosis2015" + File.separator + 
						"pivoted_diverticulosis_merged_kraken_" + taxa +  "PlusMetadata.txt";
	}
	
	
	@Override
	public HashSet<String> getPositiveClassifications()
	{
		HashSet<String> set = new HashSet<String>();
		set.add("1");
		return set;
	}
	
	@Override
	public HashSet<String> getNegativeClassifications()
	{
		HashSet<String> set = new HashSet<String>();
		set.add("0");
		return set;
	}
}
