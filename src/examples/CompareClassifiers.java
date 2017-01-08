package examples;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.rules.OneR;
import weka.classifiers.trees.RandomForest;
import weka.gui.visualize.ThresholdVisualizePanel;

public class CompareClassifiers
{
	public static void main(String[] args) throws Exception
	{
		long startTime = System.currentTimeMillis();
		// this file is at 
		//https://github.com/afodor/afodor.github.io/blob/master/classes/prog2016/pivoted_genusLogNormalWithMetadata.arff
		File inArff= new File(
				"C:\\Users\\corei7\\git\\afodor.github.io\\classes\\prog2016\\pivoted_genusLogNormalWithMetadata.arff");
				
		ThresholdVisualizePanel tvp = TestClassify.getVisPanel(inArff.getName());
		
		int numPermutations = 50;
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
				"c:\\temp\\classifierComparison.txt")));
		writer.write("randomForest\tnaiveBayes\toneR\tsvm\n");
		List<Double> supportVector = TestClassify.plotRocUsingMultithread(inArff, numPermutations, false, tvp,
				SMO.class.getName(), Color.green);
		
		List<Double> randomForest= TestClassify.plotRocUsingMultithread(inArff, numPermutations, false, tvp,
						RandomForest.class.getName(), Color.black);
		List<Double> naiveBayes=  TestClassify.plotRocUsingMultithread(inArff, numPermutations, false, tvp,
						NaiveBayes.class.getName(), Color.blue);
		List<Double> oneR=  TestClassify.plotRocUsingMultithread(inArff, numPermutations, false, tvp,
				OneR.class.getName(), Color.red);
	
		
		for(int x=0; x < numPermutations; x++)
			writer.write(randomForest.get(x) + "\t" + naiveBayes.get(x) + "\t" + oneR.get(x) +"\t"+ 
						supportVector.get(x) + 	"\n");
		
		System.out.println("Finished in " + (System.currentTimeMillis() - startTime)/1000f + " seconds ");
		
		writer.flush(); writer.close();
	}
}
