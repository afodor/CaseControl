package kraken;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

import examples.TestClassify;
import projectDescriptors.AbstractProjectDescription;
import projectDescriptors.CRCZeller;
import projectDescriptors.CirrhosisQin;
import projectDescriptors.Hmp_wgs;
import projectDescriptors.IbdMetaHit;
import projectDescriptors.Obesity;
import projectDescriptors.T2D;
import projectDescriptors.WT2D2;
import utils.ConfigReader;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.gui.visualize.ThresholdVisualizePanel;

public class LeaveOneExperimentOut
{
	private static AtomicLong seedGenerator = new AtomicLong(0);
	
	public static List<AbstractProjectDescription> getAllExperiments() throws Exception
	{
		List<AbstractProjectDescription> list = new ArrayList<AbstractProjectDescription>();
		list.add(new T2D());
		list.add(new WT2D2());
		
		//list.add(new Divitriculosis2015ProjectDescriptor());
		//list.add(new China2015_wgs());
		//list.add(new Adenomas2015ProjectDescriptor());
		list.add( new CRCZeller());
		list.add( new CirrhosisQin());
		list.add( new IbdMetaHit());
		list.add(new Hmp_wgs());
		list.add( new Obesity());
		
		return Collections.unmodifiableList(list);
	}
	
	private static Instances getAllButOne(List<AbstractProjectDescription> list,
									AbstractProjectDescription one, String taxa) throws Exception
	{
		HashSet<String> skipSet=  new HashSet<String>();
		skipSet.add(one.getProjectName());
		
		boolean firstIsTheOne = false;
		
		if( list.get(0).getProjectName().equals(one.getProjectName()))
			firstIsTheOne = true;
		
		AbstractProjectDescription apb = firstIsTheOne ? list.get(1) : list.get(0);
		
		Instances data = DataSource.read( apb.getLogArffFileKrakenCommonScaleCommonNamespace(taxa));
		skipSet.add(apb.getProjectName());
		
		for( AbstractProjectDescription apd : list )
		{
			if( ! skipSet.contains(apd.getProjectName()))
			{
				data.addAll(DataSource.read(apd.getLogArffFileKrakenCommonScaleCommonNamespace(taxa)));
				System.out.println("\tadded " + apd.getProjectName() + " " + data.size());
			}
		}
		data.setClassIndex(data.numAttributes() -1);
		return data;
	}
	
	public static void main(String[] args) throws Exception
	{
		for( int x=4; x < RunAllClassifiers.TAXA_ARRAY.length; x++)
		{
			String taxa = RunAllClassifiers.TAXA_ARRAY[x];
			HashMap<String, List<Double>> resultsMap = new LinkedHashMap<String,List<Double>>();
			String classifierName = new RandomForest().getClass().getName();
			
			
			for(AbstractProjectDescription apd : getAllExperiments())
				if( apd.getPositiveClassifications().size() > 0)
			{
				String key = apd.getProjectName() + "_" +taxa;
				ThresholdVisualizePanel tvp = TestClassify.getVisPanel( key );
				System.out.println( taxa + " " +  key);
				List<Double> results = new ArrayList<Double>();
				resultsMap.put(key, results);
				results.addAll(getAoc(
						apd, 1, false, tvp, classifierName, Color.red, taxa));
				results.addAll(getAoc(
						apd, 50, true, tvp, classifierName, Color.BLACK, taxa));
				writeResults(resultsMap, taxa, classifierName);
			
			}
		}
	}
	
	private static void writeResults( HashMap<String, List<Double>> resultsMap , String level,
			String classifierName)
		throws Exception
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
			ConfigReader.getMergedArffDir() 
				+ File.separator + "crossLeaveOneExperimentOut_" + level + "kraken_" +classifierName+ ".txt"	)));
		
		writer.write( "count\tisScrambled"  );
		
		List<String> keys = new ArrayList<>(resultsMap.keySet());
		
		for(String s : keys)
			writer.write("\t" + s);
		
		writer.write("\n");
		
		int size = resultsMap.get(keys.get(0)).size();
		
		for( int x=0; x < size; x++)
		{
			writer.write((x+1) + "\t");
			writer.write( (x != 0) + "");
			
			for(String key : keys)
				writer.write("\t" + resultsMap.get(key).get(x));
			
			writer.write("\n");
		}
		
		writer.flush();  writer.close();
	}
	

	public static List<Double> getAoc( 
			AbstractProjectDescription oneToLeaveOut, int numPermutations,
						boolean scramble, ThresholdVisualizePanel tvp,
						String classifierName, Color plotColor, String taxa) throws Exception
	{
		final List<Double> areaUnderCurve = Collections.synchronizedList(new ArrayList<Double>());
		
		int numProcessors = Runtime.getRuntime().availableProcessors() + 1;
		Semaphore s = new Semaphore(numProcessors);
		
		for( int x=0; x< numPermutations; x++)
		{
			s.acquire();
			Worker w = new Worker(s, areaUnderCurve,oneToLeaveOut, 
						scramble, tvp, classifierName, plotColor, taxa);
					
			new Thread(w).start();
		}
		
		for( int x=0; x < numProcessors; x++)
			s.acquire();
		
		
		return areaUnderCurve;
	}
	
	private static class Worker implements Runnable
	{
		private final Semaphore semaphore;
		private final List<Double> resultsList;
		private final AbstractProjectDescription oneToLeaveOut;
		private final boolean scramble;
		private final ThresholdVisualizePanel tvp;
		private final String classifierName;
		private final Color plotColor;
		private final String taxa;
		
		
		public Worker(Semaphore semaphore, List<Double> resultsList,
				AbstractProjectDescription oneToLeaveOut, boolean scramble,
				ThresholdVisualizePanel tvp, String classifierName, Color plotColor,
				String taxa)
		{
			this.semaphore = semaphore;
			this.resultsList = resultsList;
			this.oneToLeaveOut = oneToLeaveOut;
			this.scramble = scramble;
			this.tvp = tvp;
			this.classifierName = classifierName;
			this.plotColor =plotColor;
			this.taxa = taxa;
		}

		@Override
		public void run()
		{
			try
			{
				List<AbstractProjectDescription> experimentList = getAllExperiments();
				Random random = new Random(seedGenerator.incrementAndGet());
				Classifier classifier = (Classifier) Class.forName(classifierName).newInstance();
				Instances trainData= getAllButOne(experimentList, oneToLeaveOut, taxa);
				Instances testData = DataSource.read(
						oneToLeaveOut.getLogArffFileKrakenCommonScaleCommonNamespace(taxa));
				
				if(scramble)
					TestClassify.scrambeLastColumn(trainData, random);
				
				trainData.setClassIndex(trainData.numAttributes() -1);
				testData.setClassIndex(testData.numAttributes() -1);
				
				classifier.buildClassifier(trainData);
				Evaluation ev = new Evaluation(trainData);
				ev.evaluateModel(classifier, testData);
				resultsList.add(ev.areaUnderROC(0));
				
				
				if( tvp != null)
					TestClassify.addROC(ev,tvp, plotColor);
			}
			catch(Exception ex)
			{
				throw new RuntimeException(ex);
			}
			finally
			{
				semaphore.release();
			}
			
		}
	}
}
