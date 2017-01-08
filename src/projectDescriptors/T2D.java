package projectDescriptors;

import java.io.File;
import java.util.HashSet;

import utils.ConfigReader;

public class T2D extends AbstractProjectDescription
{
	@Override
	public String getProjectName()
	{
		return "t2d";
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
		set.add("t2d"); set.add("T2D");
		return set;
	}
	
	@Override
	public HashSet<String> getNegativeClassifications()
	{
		HashSet<String> set = new HashSet<String>();
		set.add("n"); set.add("N");
		return set;
	}
}
