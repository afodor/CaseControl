package projectDescriptors;

import java.util.HashSet;


public abstract class AbstractProjectDescription
{
	public static final String KRAKEN = "KRAKEN";
	public static final String RDP = "RDP";
	public static final String QIIME_CLOSED = "QIIME_CLOSED";
	
	public static final int MIN_SEQUENCE_THRESHOLD = 500;
	public static final int PSEUDO_COUNT = 1;
	
	abstract public String getProjectName();
	
	abstract public String getCountFileKraken(String taxa) throws Exception;
	
	final public String getLogFileKrakenCommonScale(String taxa) throws Exception
	{
		String baseFile = getCountFileKraken(taxa); 
		baseFile = baseFile.substring(0, baseFile.lastIndexOf(".txt") );
		return baseFile + "krakenLogNormCommonScale.txt";
	}
	
	final public String getLogArffFileKrakenCommonScale(String taxa) throws Exception
	{
		String baseFile = getCountFileKraken(taxa); 
		baseFile = baseFile.substring(0, baseFile.lastIndexOf(".txt") );
		return baseFile + "krakenLogNormCommonScale.arff";
	}
	
	final public String getLogArffFileKrakenCommonScaleCommonNamespace(String taxa) throws Exception
	{
		String baseFile = getCountFileKraken(taxa); 
		baseFile = baseFile.substring(0, baseFile.lastIndexOf(".txt") );
		return baseFile + "krakenLogNormCommonScaleCommonNamespace.arff";
	}
	
	abstract public HashSet<String> getPositiveClassifications();
	
	abstract public HashSet<String> getNegativeClassifications();
	
}
