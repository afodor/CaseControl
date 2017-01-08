package examples;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

import javax.swing.SwingUtilities;

import parsers.NewRDPParserFileLine;
import utils.ConfigReader;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.ThresholdVisualizePanel;

public class TestClassify
{
	private static final AtomicLong seedGenerator = new AtomicLong(0);
	
	public static void main(String[] args) throws Exception
	{
		Random random = new Random(0);
		int numPermutations = 50;

		for( int x=1 ; x < NewRDPParserFileLine.TAXA_ARRAY.length; x++)
		{
			
			System.out.println(NewRDPParserFileLine.TAXA_ARRAY[x]);
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
				ConfigReader.getAdenomasWekaDir() + File.separator + 
						 NewRDPParserFileLine.TAXA_ARRAY[x] +"_Adenomas.txt" )));
			
			writer.write("ad1\tad2\tcross\n");
			
			File adenomas = new File("C:\\adenomasRelease\\spreadsheets\\pivoted_" + 
					NewRDPParserFileLine.TAXA_ARRAY[x] + 	"LogNormalWithMetadataBigSpace.arff");
			
			ThresholdVisualizePanel tvp = getVisPanel(adenomas.getName());
			
			List<Double> firstARoc = 
					plotROCForAnArff(adenomas, numPermutations,random,false,tvp);
			plotROCForAnArff(adenomas, numPermutations,random,true,tvp);
			
			File ad2 = new File("C:\\tope_Sep_2015\\spreadsheets\\" + 
					NewRDPParserFileLine.TAXA_ARRAY[x] + "asColumnsLogNormalPlusMetadataBigSpace.arff");
			
			tvp = getVisPanel(ad2.getName());
			
			List<Double> secondROC = plotROCForAnArff(ad2, numPermutations,random,false,tvp);
			plotROCForAnArff(ad2, numPermutations,random,true,tvp);
			
			Instances trainData = DataSource.read(ad2.getAbsolutePath());
			Instances testData = DataSource.read(adenomas.getAbsolutePath());
			trainData.setClassIndex(trainData.numAttributes() -1);
			testData.setClassIndex(testData.numAttributes() -1);
			
			List<Double> crossROC = 
						getRocForTrainingToTest(trainData, testData, random, numPermutations);
			
			for(int y=0;y < firstARoc.size(); y++)
				writer.write(firstARoc.get(y) + "\t" + secondROC.get(y) + "\t" + crossROC.get(y) + "\n");
			
			writer.flush();  writer.close();
		}
		
	}
	
	/*
	public static void main(String[] args) throws Exception
	{
		Random random = new Random(0);
		int numPermutations = 1000;

		for( int x=1 ; x < NewRDPParserFileLine.TAXA_ARRAY.length; x++)
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					ConfigReader.getAdenomasWekaDir() + File.separator + 
						"adenomasShuffled" + NewRDPParserFileLine.TAXA_ARRAY[x] + ".txt")));
			
			writer.write("type\tvalue\n");
			
			System.out.println(NewRDPParserFileLine.TAXA_ARRAY[x]);
			File adenomas = new File("C:\\adenomasRelease\\spreadsheets\\pivoted_" + 
					NewRDPParserFileLine.TAXA_ARRAY[x] + 	"LogNormalWithMetadataBigSpace.arff");

			File ad2 = new File("C:\\tope_Sep_2015\\spreadsheets\\" + 
					NewRDPParserFileLine.TAXA_ARRAY[x] + "asColumnsLogNormalPlusMetadataBigSpace.arff");
			
			Instances testData = DataSource.read(adenomas.getAbsolutePath());

			List<Double> percentCorrect =
			getPercentCorrectFromScrambles(ad2, testData, random, 1,false);
			
			writer.write("true\t" + percentCorrect.get(0) + "\n");
			
			percentCorrect =
					getPercentCorrectFromScrambles(ad2, testData, random, numPermutations,true);
			
			for( Double d : percentCorrect)
				writer.write("shuffle\t" + d + "\n");
					
			writer.flush(); writer.close();
			
		}
	}
	*/

	
	public static void scrambeLastColumn( Instances instances, Random random )
	{
		List<Double> list = new ArrayList<Double>();
		
		for(Instance i : instances)
			list.add(i.value(i.numAttributes()-1));
		
		Collections.shuffle(list,random);
		
		for( int x=0; x < instances.size(); x++)
		{
			Instance i = instances.get(x);
			i.setValue(i.numAttributes()-1, list.get(x));
		}
	}
	
	public static List<Double> getPercentCorrectFromScrambles( File trainingDataFile, 
				Instances testData, Random random, int numPermutations,
							boolean scramble) throws Exception
	{
		System.out.println("In scramble");
		List<Double> aList = new ArrayList<Double>();
		Instances trainData = DataSource.read(trainingDataFile.getAbsolutePath());
		
		for( int x=0; x < numPermutations; x++)
		{
			if( scramble)
				scrambeLastColumn(trainData, random);
			
			trainData.setClassIndex(trainData.numAttributes() -1);
			testData.setClassIndex(testData.numAttributes() -1);
			
			AbstractClassifier rf = new RandomForest();
			rf.buildClassifier(trainData);
			Evaluation ev = new Evaluation(trainData);
			ev.evaluateModel(rf, testData);
			if( x % 20 ==0)
			System.out.println("cross " + x + " " + ev.areaUnderROC(0) + " " + ev.pctCorrect());
			aList.add(ev.pctCorrect());
		}
		
		return aList;
	}
	
	private static class Holder implements Comparable<Holder>
	{
		double predicted;
		double actual;
		double distribution;
		
		Holder(Prediction p)
		{
			NominalPrediction np = (NominalPrediction) p;
			this.predicted = np.predicted();
			this.actual = np.actual();
			this.distribution = np.distribution()[0];
		}
		
		@Override
		public int compareTo(Holder o)
		{
			return Double.compare(o.distribution,this.distribution);		
		}
	}
	
	public static void writeROCToFile( Evaluation eval , File file ) throws Exception
	{
		List<Holder> list = new ArrayList<Holder>();
		for( Prediction p : eval.predictions())
			list.add(new Holder(p));
		Collections.sort(list);
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		
		writer.write("predicted\tactual\tdistribution\n");
		
		for(Holder h : list)
			writer.write(h.predicted + "\t" + h.actual + "\t" + h.distribution + "\n");
		
		
		writer.flush();  writer.close();
	}
	
	// modded from https://weka.wikispaces.com/Generating+ROC+curve
	public static void addROC(Evaluation eval, final ThresholdVisualizePanel vmc,
			Color color) throws Exception
	{
		final ThresholdCurve tc = new ThresholdCurve();
	    final Instances result = tc.getCurve(eval.predictions(), 0);
	    final Object visibilityLock = new Object();
	     
	    final PlotData2D tempd = new PlotData2D(result);
	    tempd.setPlotName(result.relationName());
	    tempd.addInstanceNumberAttribute();
	    
	    // specify which points are connected
	    boolean[] cp = new boolean[result.numInstances()];
	    for (int n = 1; n < cp.length; n++)
	       cp[n] = true;
	    tempd.setConnectPoints(cp);
	    tempd.setCustomColour(color);
	   
	    //writeROCToFile(eval, new File("c:\\temp\\temp.txt"));
	    
	    // make sure everything in this thread will be visible to AWT thread
	    synchronized(visibilityLock) 
	    {
	    	SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
				    // make sure everything is visible to the AWT thread
				    synchronized(visibilityLock) 
				    {						
				    	try
				    	{
						     vmc.addPlot(tempd);
						}
						catch(Exception ex)
						{
							throw new RuntimeException(ex);
						}
				    }
				}
			});    
    	}
	}
	
	public static List<Double> getRocForTrainingToTest(Instances trainingData, Instances testData,
				Random random, int numIterations) throws Exception
	{
		System.out.println("Go");

		List<Double> rocAreas = new ArrayList<Double>();
		
		for( int x=0; x < numIterations; x++)
		{

			Instances halfTrain = new Instances(trainingData, trainingData.size() / 2 );
			
			for(Instance i : trainingData)
				if( random.nextFloat() <= 0.5)
					halfTrain.add(i);
			
			System.out.println(halfTrain.size() + " " + trainingData.size());
			
			AbstractClassifier rf = new RandomForest();
			rf.buildClassifier(halfTrain);
			Evaluation ev = new Evaluation(halfTrain);
			ev.evaluateModel(rf, testData);
			System.out.println("cross " + x + " " + ev.areaUnderROC(0) + " " + ev.pctCorrect());
			rocAreas.add(ev.pctCorrect());
		}
		
		return rocAreas;
	}
	
	public static ThresholdVisualizePanel getVisPanel(String title) throws Exception
	{

		 ThresholdVisualizePanel vmc = new ThresholdVisualizePanel();
		 vmc.setName(title);
		 
		// display curve
	     final javax.swing.JFrame jf =
	       new javax.swing.JFrame("Weka Classifier Visualize: "+title);
	     jf.setSize(500,400);
	     jf.getContentPane().setLayout(new BorderLayout());
	     jf.getContentPane().add(vmc, BorderLayout.CENTER);
	     jf.addWindowListener(new java.awt.event.WindowAdapter() {
	       public void windowClosing(java.awt.event.WindowEvent e) {
	       jf.dispose();
	       }
	     });
	     jf.setVisible(true);
	     
	     return vmc;
	
	}
	
	

	
	
	private static class Worker implements Runnable
	{
		private final Semaphore semaphore;
		private final List<Double> resultsList;
		private final File inFile;
		private final boolean scramble;
		private final ThresholdVisualizePanel tvp;
		private final String classifierName;
		private final Color plotColor;
		
		
		public Worker(Semaphore semaphore, List<Double> resultsList, File inFile, boolean scramble,
				ThresholdVisualizePanel tvp, String classifierName, Color plotColor)
		{
			this.semaphore = semaphore;
			this.resultsList = resultsList;
			this.inFile = inFile;
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
				Instances data = DataSource.read(inFile.getAbsolutePath());
				
				if(scramble)
					scrambeLastColumn(data, random);
				
				data.setClassIndex(data.numAttributes() -1);
				Evaluation ev = new Evaluation(data);
				
				ev.crossValidateModel(classifier, data, 10, random);
				resultsList.add(ev.areaUnderROC(0));
				
				System.out.println(inFile.getAbsolutePath()+ " " + classifierName + " " 
			 	+ scramble + " " + ev.areaUnderROC(0));
				
				if( tvp != null)
					addROC(ev,tvp, plotColor);
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
	
	public static List<Double> plotRocUsingMultithread( File inFile, 
			int numPermutations,  boolean scramble, 
			ThresholdVisualizePanel tvp, String className, Color color) throws Exception
	{

		final List<Double> areaUnderCurve = Collections.synchronizedList(new ArrayList<Double>());
		
		int numProcessors = Runtime.getRuntime().availableProcessors() - 1;
		
		if( numProcessors < 1)
			numProcessors = 1;
		
		Semaphore s = new Semaphore(numProcessors);
		
		for( int x=0; x< numPermutations; x++)
		{
			s.acquire();
			Worker w = new Worker(s, areaUnderCurve, inFile, scramble, tvp, className, color);
			new Thread(w).start();
		}
		
		for( int x=0; x < numProcessors; x++)
			s.acquire();
		
		
		return areaUnderCurve;
	}
	
	public static List<Double> plotROCForAnArff( File inFile, 
			int numPermutations, Random random , boolean scramble, 
				ThresholdVisualizePanel tvp) 
				throws Exception
	{	 
		return plotROCForAnArff(inFile, numPermutations, random, scramble, tvp, 
				new RandomForest(), Color.black);
	}
	
	public static List<Double> plotROCForAnArff( File inFile, 
			int numPermutations, Random random , boolean scramble, 
				ThresholdVisualizePanel tvp, Classifier classifier, Color nonScrambleColor) 
				throws Exception
	{	 
		
		List<Double> areaUnderCurve = new ArrayList<Double>();
		
		for( int x=0; x< numPermutations; x++)
		{
			Instances data = DataSource.read(inFile.getAbsolutePath());
			
			if(scramble)
				scrambeLastColumn(data, random);
			
			data.setClassIndex(data.numAttributes() -1);
			Evaluation ev = new Evaluation(data);
			
			classifier.buildClassifier(data);
			ev.crossValidateModel(classifier, data, 10, random);
			//System.out.println(ev.toSummaryString("\nResults\n\n", false));
			//System.out.println(x + " " + ev.areaUnderROC(0) + " " + ev.pctCorrect());
			areaUnderCurve.add(ev.areaUnderROC(0));
			//System.out.println(x + " " + ev.areaUnderROC(0));
			
			if( tvp != null)
				addROC(ev,tvp, scramble ? Color.red: nonScrambleColor);
		}
		
		return areaUnderCurve;
		
	}
}
