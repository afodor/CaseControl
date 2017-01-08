package examples;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;
import weka.core.*;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.*;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.classifiers.trees.RandomForest;
import weka.gui.visualize.*;

/**
  * Generates and displays a ROC curve from a dataset. Uses a default
  * NaiveBayes to generate the ROC data.
  *
  * @author FracPete
  * 
  * modded from  https://weka.wikispaces.com/Generating+ROC+curve
  */
public class BuildROCCurves{

	private static void addOneCurve(Instances result,  ThresholdVisualizePanel vmc,
				Color color, String plotName) throws Exception
	{
		 PlotData2D tempd = new PlotData2D(result);
		    tempd.setPlotName(plotName);
		    tempd.addInstanceNumberAttribute();
		    // specify which points are connected
		    boolean[] cp = new boolean[result.numInstances()];
		    for (int n = 1; n < cp.length; n++)
		      cp[n] = true;
		    tempd.setConnectPoints(cp);
		    tempd.setCustomColour(color);
		    // add plot
		    vmc.addPlot(tempd);

	}
	
  /**
   * takes one argument: dataset in ARFF format (expects class to
   * be last attribute)
   */
  public static void main(String[] args) throws Exception {
    // load data
    Instances data = DataSource.read(args[0]);
    data.setClassIndex(data.numAttributes() - 1);

    // train classifier
    Classifier cl = new RandomForest();
    cl.buildClassifier(data);
    Evaluation eval = new Evaluation(data);
    eval.crossValidateModel(cl, data, 10, new Random(1));

    // generate curve
    ThresholdCurve tc = new ThresholdCurve();
    int classIndex = 0;
    Instances result = tc.getCurve(eval.predictions(), classIndex);
    
    
    BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
    		"c:\\temp\\someTemp.txt")));
    
    writer.write("distribution\tweight\tactual\tpredicted\n");
    
    for( Prediction p : eval.predictions() )
    {
    	 NominalPrediction pred = (NominalPrediction) p;
    	
    	 
    	 // the sort will happen on distribution...
    	writer.write(pred.distribution()[0] + "\t" + pred.weight() + "\t" + 
    				pred.actual() + "\t" + pred.predicted() + "\n" );
    }
    writer.flush();  writer.close();

    // plot curve
    ThresholdVisualizePanel vmc = new ThresholdVisualizePanel();
    addOneCurve(result, vmc, Color.black, "RandomForest");
    
    vmc.setROCString("(Area under ROC = " +
        Utils.doubleToString(tc.getROCArea(result), 4) + ")");
    vmc.setName(result.relationName());
    
    cl = new NaiveBayes();
    cl.buildClassifier(data);
    eval = new Evaluation(data);
    eval.crossValidateModel(cl, data, 10, new Random(1));
    result = tc.getCurve(eval.predictions(), classIndex);
    addOneCurve(result, vmc, Color.red, "Naive Bayes");
   
    // display curve
    String plotName = vmc.getName();
    final javax.swing.JFrame jf =
      new javax.swing.JFrame("Weka Classifier Visualize: "+plotName);
    jf.setSize(500,400);
    jf.getContentPane().setLayout(new BorderLayout());
    jf.getContentPane().add(vmc, BorderLayout.CENTER);
    jf.addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent e) {
      jf.dispose();
      }
    });
    jf.setVisible(true);
  }
}
