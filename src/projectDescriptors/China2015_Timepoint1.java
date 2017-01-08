package projectDescriptors;

import java.util.HashSet;


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
		// TODO Auto-generated method stub
		return null;
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
