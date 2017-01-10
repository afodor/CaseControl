package projectDescriptors;

import java.io.File;
import java.util.HashSet;

import utils.ConfigReader;


public class China2015_Timepoint1 extends AbstractProjectDescription
{
	@Override
	public String getProjectName()
	{
		return "China2015_Time1";
	}
	
	@Override
	public String getCountFileKraken(String taxa) throws Exception
	{
		return ConfigReader.getMergedArffDir() + File.separator + 
				"tables" + File.separator + "China_16S" + File.separator + 
					"China_2015_kraken_" + taxa + "CountsWithMetadata_first_A.txt";
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
