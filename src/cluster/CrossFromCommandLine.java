package cluster;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import kraken.RunCrossClassifiers;
import projectDescriptors.AbstractProjectDescription;
import weka.classifiers.trees.RandomForest;
import weka.gui.visualize.ThresholdVisualizePanel;

public class CrossFromCommandLine
{
	public static final String CROSS_DIR = 
			"/nobackup/afodor_research/clusterArff/ArffMerged/clusterCross";
	
	public static void main(String[] args) throws Exception
	{
		if( args.length != 4)
		{
			System.out.println("usage " + CrossFromCommandLine.class.getName() + " " + 
					"xProject yProject taxaLevel numPermutations");
			
			System.exit(1);
		}
		
		AbstractProjectDescription xProject = 
				(AbstractProjectDescription) Class.forName(args[0]).newInstance();
		
		AbstractProjectDescription yProject = 
				(AbstractProjectDescription) Class.forName(args[1]).newInstance();
		
		String taxa = args[2];
		int numPermutation = Integer.parseInt(args[3]);
		
		ThresholdVisualizePanel tvp = null;
		
		List<Double> results = new ArrayList<Double>();
						
		File trainFile =new File(xProject.getLinearArffFileKrakenCommonScaleCommonNamespace(taxa));
		File testFile = new File(yProject.getLinearArffFileKrakenCommonScaleCommonNamespace(taxa));
		String classifierName = new RandomForest().getClass().getName();
						
		File outFile = new File("/nobackup/afodor_research/clusterArff/ArffMerged/clusterCross/" +
				File.separator + xProject.getProjectName() + "_" + yProject.getProjectName() + "_"+
						taxa + ".txt");
		
		if( ! outFile.exists())
		{
			results.addAll(  RunCrossClassifiers.
					getPercentCorrect(trainFile, testFile, 1, false, tvp, classifierName, Color.RED));
					
			results.addAll(  RunCrossClassifiers.
					getPercentCorrect(trainFile, testFile, numPermutation, true, tvp, classifierName, Color.RED));
			
			writeResults(results, xProject, yProject, taxa, outFile);
		}
		
	}
	
	private static void writeResults(List<Double> results, 
			AbstractProjectDescription xProject, AbstractProjectDescription yProject,
				String taxa, File outFile) throws Exception
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
		
		writer.write("roc\tisPermuted\n");
		
		writer.write(results.get(0) + "\tfalse\n");
		
		for( int x=1; x < results.size(); x++)
			writer.write(results.get(x) + "\ttrue\n");
		
		writer.flush();  writer.close();
	}
}
