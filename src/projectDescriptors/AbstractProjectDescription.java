package projectDescriptors;

import java.io.File;
import java.util.HashSet;

import utils.ConfigReader;


public abstract class AbstractProjectDescription
{
	public static final String KRAKEN = "KRAKEN";
	public static final String RDP = "RDP";
	public static final String QIIME_CLOSED = "QIIME_CLOSED";
	
	public static final int MIN_SEQUENCE_THRESHOLD = 500;
	public static final int PSEUDO_COUNT = 1;
	
	abstract public String getProjectName();
	
	abstract public String getCountFileKraken(String taxa) throws Exception;
	
	final public String getZScoreFilteredLogNormalKraken(String taxa) throws Exception
	{
		String baseFile = getCountFileKraken(taxa); 
		baseFile = baseFile.substring(0, baseFile.lastIndexOf(".txt") );
		return baseFile + "zScoreFilteredkrakenLogNormCommonScale.txt";
	}

	final public String getZScoreFilteredLogNormalKrakenToArff(String taxa) throws Exception
	{
		String baseFile = getCountFileKraken(taxa); 
		baseFile = baseFile.substring(0, baseFile.lastIndexOf(".txt") );
		return baseFile + "zScoreFilteredkrakenLogNormCommonScale.arff";
	}
	
	final public String getZScoreFilteredLogNormalKrakenToCommonNamespaceArff(String taxa) throws Exception
	{
		String baseFile = getCountFileKraken(taxa); 
		baseFile = baseFile.substring(0, baseFile.lastIndexOf(".txt") );
		return baseFile + "zScoreFilteredkrakenLogNormCommonScaleCommonNamespace.arff";
	}
	
	final public String getLogFileKrakenCommonScale(String taxa) throws Exception
	{
		String baseFile = getCountFileKraken(taxa); 
		baseFile = baseFile.substring(0, baseFile.lastIndexOf(".txt") );
		return baseFile + "krakenLogNormCommonScale.txt";
	}
	
	final public String getNonLogFileKrakenCommonScale(String taxa) throws Exception
	{
		String baseFile = getCountFileKraken(taxa); 
		baseFile = baseFile.substring(0, baseFile.lastIndexOf(".txt") );
		return baseFile + "krakenNonLogNormCommonScale.txt";
	}
	
	final public String getLogArffFileKrakenCommonScale(String taxa) throws Exception
	{
		String baseFile = getCountFileKraken(taxa); 
		baseFile = baseFile.substring(0, baseFile.lastIndexOf(".txt") );
		return baseFile + "krakenLogNormCommonScale.arff";
	}
	

	final public String getTTestResultsFilePath(String taxa, String classificationScheme) throws Exception
	{
		return ConfigReader.getMergedArffDir() + File.separator + 
					"ttests" + File.separator + this.getProjectName() + "_" + taxa 
					+ "_ttests_" + classificationScheme +  ".txt";
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
