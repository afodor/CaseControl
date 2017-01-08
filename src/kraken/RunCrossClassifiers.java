package kraken;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

import com.sun.org.apache.xerces.internal.impl.xpath.XPath;

import examples.TestClassify;
import projectDescriptors.AbstractProjectDescription;
import utils.ConfigReader;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.gui.visualize.ThresholdVisualizePanel;

public class RunCrossClassifiers
{
	private static AtomicLong seedGenerator = new AtomicLong(0);
	
	public static void main(String[] args) throws Exception
	{
		List<AbstractProjectDescription> projectList = RunAllClassifiers.getAllProjects();
		
		for( int t =0; t < RunAllClassifiers.TAXA_ARRAY.length; t++)
		{ 
			HashMap<String, List<Double>> resultsMap = new LinkedHashMap<String,List<Double>>();
			
			for(int x=0; x < projectList.size(); x++)
				for( int y=0; y < projectList.size(); y++)
					if( x != y)
					{
						String taxa = RunAllClassifiers.TAXA_ARRAY[t];
						AbstractProjectDescription xProject = projectList.get(x);
						AbstractProjectDescription yProject = projectList.get(y);
						System.out.println(taxa + " " + xProject + " "+yProject );
						ThresholdVisualizePanel tvp = null;
						//ThresholdVisualizePanel tvp = TestClassify.getVisPanel( taxa+ " "+
							//	xProject.getProjectName() + " " + yProject.getProjectName() );
						String key = xProject.getProjectName() + "_vs_" + yProject.getProjectName();
						System.out.println( taxa + " " +  key);
						List<Double> results = new ArrayList<Double>();
						resultsMap.put(key, results);
						
						File trainFile =new File(xProject.getLogArffFileKrakenCommonScaleCommonNamespace(taxa));
						File testFile = new File(yProject.getLogArffFileKrakenCommonScaleCommonNamespace(taxa));
						String classifierName = new RandomForest().getClass().getName();
						
						results.addAll(getPercentCorrect(trainFile, testFile, 1, false, tvp, classifierName, Color.RED));
						results.addAll(getPercentCorrect(trainFile, testFile, 2000, true, tvp, classifierName, Color.BLACK));
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
				+ File.separator + "cross_" + level + "kraken_" +classifierName+ ".txt"	)));
		
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
	
	public static List<Double> getPercentCorrect( File trainingDataFile, 
			File testDataFile, int numPermutations,
						boolean scramble, ThresholdVisualizePanel tvp,
						String classifierName, Color plotColor) throws Exception
	{
		final List<Double> areaUnderCurve = Collections.synchronizedList(new ArrayList<Double>());
		
		int numProcessors = Runtime.getRuntime().availableProcessors() + 1;
		Semaphore s = new Semaphore(numProcessors);
		
		for( int x=0; x< numPermutations; x++)
		{
			s.acquire();
			Worker w = new Worker(s, areaUnderCurve,trainingDataFile, testDataFile, 
						scramble, tvp, classifierName, plotColor);
					
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
		private final File trainFile;
		private final File testFile;
		private final boolean scramble;
		private final ThresholdVisualizePanel tvp;
		private final String classifierName;
		private final Color plotColor;
		
		
		public Worker(Semaphore semaphore, List<Double> resultsList, File trainFile,
					File testFile, boolean scramble,
				ThresholdVisualizePanel tvp, String classifierName, Color plotColor)
		{
			this.semaphore = semaphore;
			this.resultsList = resultsList;
			this.trainFile = trainFile;
			this.testFile = testFile;
			this.scramble = scramble;
			this.tvp = tvp;
			this.classifierName = classifierName;
			this.plotColor =plotColor;
		}

		@Override
		public void run()
		{
			try
			{
				Random random = new Random(seedGenerator.incrementAndGet());
				Classifier classifier = (Classifier) Class.forName(classifierName).newInstance();
				Instances trainData= DataSource.read(trainFile.getAbsolutePath());
				Instances testData = DataSource.read(testFile.getAbsolutePath());
				
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
