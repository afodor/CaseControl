package diverticulosis;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import examples.TestClassify;
import parsers.NewRDPParserFileLine;
import utils.ConfigReader;
import weka.classifiers.trees.RandomForest;
import weka.gui.visualize.ThresholdVisualizePanel;

public class TestClassifyDiverticulosis
{
	public static void main(String[] args) throws Exception
	{
		int numPermutations = 50;

		for( int x=1 ; x < NewRDPParserFileLine.TAXA_ARRAY.length; x++)
		{
			System.out.println(NewRDPParserFileLine.TAXA_ARRAY[x]);
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
				ConfigReader.getTopeOneAtATimeDir()+ File.separator + 
						 NewRDPParserFileLine.TAXA_ARRAY[x] +"_rocs.txt" )));
			
			writer.write("d1\trandom\n");
			
			File inArff= new File(ConfigReader.getTopeOneAtATimeDir() + File.separator + 
					"merged" + File.separator + "pivoted_" +  
					NewRDPParserFileLine.TAXA_ARRAY[x] + "asColumnsLogNormalPlusMetadata.arff");
			
			ThresholdVisualizePanel tvp = TestClassify.getVisPanel(inArff.getName());
			
			List<Double> firstARoc = 
					TestClassify.plotRocUsingMultithread(
							inArff, numPermutations, false, tvp, new RandomForest().getClass().getName(), 
							Color.BLACK);
			List<Double> randomROC = 
					TestClassify.plotRocUsingMultithread(
							inArff, numPermutations, true, tvp, new RandomForest().getClass().getName(), 
							Color.RED);

			
			for(int y=0;y < firstARoc.size(); y++)
				writer.write(firstARoc.get(y) + "\t" + randomROC.get(y) + "\n");
			
			writer.flush();  writer.close();
		}
		
	}	
}
