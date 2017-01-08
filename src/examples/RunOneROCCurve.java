package examples;

import java.awt.Color;
import java.io.File;
import java.util.List;

import weka.classifiers.trees.RandomForest;

public class RunOneROCCurve
{
	// run on the cluster like this:
	//  java -cp .:/users/afodor/gitInstall/WekaExamples/weka.jar examples.RunOneROCCurve
	public static void main(String[] args) throws Exception
	{
		long startTime = System.currentTimeMillis();
		// this file is at 
		//https://github.com/afodor/afodor.github.io/blob/master/classes/prog2016/pivoted_genusLogNormalWithMetadata.arff
		File inArff= new File(
				"/nobackup/afodor_research/arffMerged/Adenomas2012/pivoted_phylumLogNormalWithMetadata.arff");
				
		//ThresholdVisualizePanel tvp = TestClassify.getVisPanel(inArff.getName());
		
		int numPermutations = 5;
		
		//TestClassify.plotROCForAnArff(inArff, numPermutations,random,false,tvp);	
		//TestClassify.plotROCForAnArff(inArff, numPermutations,random,true,tvp);	
		
		String className = RandomForest.class.getName();
		List<Double> notScrambled = TestClassify.plotRocUsingMultithread(inArff, numPermutations, false,null,
						className, Color.black);
		List<Double> scrambled =  TestClassify.plotRocUsingMultithread(inArff, numPermutations, true, null,
						className, Color.red);
		
		for( int x=0; x < numPermutations; x++)
			System.out.println(notScrambled.get(x) + "\t" + scrambled.get(x) + "\n");
		
		System.out.println("Finished in " + (System.currentTimeMillis() - startTime)/1000f + " seconds ");
		
	}
}
