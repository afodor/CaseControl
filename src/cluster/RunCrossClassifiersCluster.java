package cluster;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;

import kraken.RunAllClassifiers;
import projectDescriptors.AbstractProjectDescription;

public class RunCrossClassifiersCluster
{
	public static final String SCRIPT_DIR = "/nobackup/afodor_research/clusterArff/crossScriptDir";
	
	private static final int NUMBER_JOBS= 100;
	private static final int NUM_PERMUTATIONS = 1000;
	
	public static void main(String[] args) throws Exception
	{
		BufferedWriter allWriter = new BufferedWriter(new FileWriter(new File(
			SCRIPT_DIR + File.separator + "runAll.sh"	)));
		
		HashMap<Integer, BufferedWriter> writerMap = 
				new HashMap<Integer,BufferedWriter>();
		
		for( int x=0;  x < NUMBER_JOBS; x++)
		{
			File aFile = new File(SCRIPT_DIR + File.separator + "run_" + x + ".sh");
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					aFile.getAbsolutePath())));
			
			writerMap.put(x, allWriter);
			
			allWriter.write("qsub -q \"copperhead\" " +   aFile.getAbsolutePath());
			
			writer.write("#PBS -l procs=8,mem=64GB\n");
		}
		
		List<AbstractProjectDescription> projectList = RunAllClassifiers.getAllProjects();
		
		int index=0;
		
		for( int t =0; t < RunAllClassifiers.TAXA_ARRAY.length; t++)
		{ 
			
			for(int x=0; x < projectList.size(); x++)
				for( int y=0; y < projectList.size(); y++)
					if( x != y)
					{
						
					}
			
			index++;
			
			if (index == NUMBER_JOBS)
				index =0;
		}
		
	}
	
	
}
