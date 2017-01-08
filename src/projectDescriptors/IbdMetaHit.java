package projectDescriptors;

import java.io.File;
import java.util.HashSet;

import utils.ConfigReader;

public class IbdMetaHit extends AbstractProjectDescription
{

	@Override
	public String getProjectName()
	{
		return "IbdMetaHit";
	}

	@Override
	public String getCountFileKraken(String taxa) throws Exception
	{
		return ConfigReader.getMergedArffDir() + File.separator + 
				"kwinglee_kraken" + File.separator + "ibd_minikraken_merged_taxaAsCol_withMeta_"
				+ taxa +".txt";
	}
	
	@Override
	public  HashSet<String> getPositiveClassifications()
	{
		HashSet<String> set = new HashSet<String>();
		set.add("ibd_ulcerative_colitis");
		set.add("ibd_crohn_disease");
		return set;
	}
	
	@Override
	public HashSet<String> getNegativeClassifications()
	{
		HashSet<String> set = new HashSet<String>();
		set.add("n");
		return set;
	}
	
}
