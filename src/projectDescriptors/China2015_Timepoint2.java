package projectDescriptors;

import java.io.File;
import java.util.HashSet;

import utils.ConfigReader;

public class China2015_Timepoint2 extends AbstractProjectDescription
{
	@Override
	public String getProjectName()
	{
		return "China2015_Time2";
	}
	
	@Override
	public String getCountFileKraken(String taxa) throws Exception
	{
		return ConfigReader.getMergedArffDir() + File.separator + 
				"tables" + File.separator + "China_16S" + File.separator + 
					"China_2015_kraken_" + taxa + "CountsWithMetadata_second_B.txt";
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
