package projectDescriptors;

import java.io.File;
import java.util.HashSet;

import utils.ConfigReader;

public class CirrhosisQin extends AbstractProjectDescription
{

	@Override
	public String getProjectName()
	{
		return "CirrhosisQin";
	}
	@Override
	public String getCountFileKraken(String taxa) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public HashSet<String> getNegativeClassifications()
	{
		HashSet<String> set = new HashSet<String>();
		set.add("n");
		return set;
	}
	
	@Override
	public  HashSet<String> getPositiveClassifications()
	{
		HashSet<String> set = new HashSet<String>();
		set.add("cirrhosis"); 
		return set;
	}
	
}
