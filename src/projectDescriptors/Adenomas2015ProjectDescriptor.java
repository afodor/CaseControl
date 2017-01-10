package projectDescriptors;

import java.io.File;
import java.util.HashSet;

import utils.ConfigReader;


public class Adenomas2015ProjectDescriptor extends AbstractProjectDescription
{
	
	
	@Override
	public String getProjectName()
	{
		return "Adenomas2015";
	}
	
	@Override
	public String getCountFileKraken(String taxa) throws Exception
	{
		return null;
	}
	
	@Override
	public HashSet<String> getPositiveClassifications()
	{
		HashSet<String> set = new HashSet<String>();
		set.add("case");
		return set;
	}
	
	@Override
	public HashSet<String> getNegativeClassifications()
	{
		HashSet<String> set = new HashSet<String>();
		set.add("control");
		return set;
	}
}
