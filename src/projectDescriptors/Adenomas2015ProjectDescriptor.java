package projectDescriptors;

import java.util.HashSet;


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
		// TODO Auto-generated method stub
		return null;
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
