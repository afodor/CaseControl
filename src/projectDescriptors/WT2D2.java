package projectDescriptors;

import java.io.File;
import java.util.HashSet;

import utils.ConfigReader;

public class WT2D2 extends AbstractProjectDescription
{
	@Override
	public String getProjectName()
	{
		return "wt2d";
	}
	
	@Override
	public String getCountFileKraken(String taxa) throws Exception
	{
		return ConfigReader.getMergedArffDir() + File.separator + 
				"kwinglee_kraken" + File.separator + "wt2d_minikraken_merged_taxaAsCol_withMeta_"
				+ taxa +".txt";
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
