package kraken.inference;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

import kraken.RunAllClassifiers;
import projectDescriptors.AbstractProjectDescription;
import projectDescriptors.AllButOne;
import utils.ConfigReader;

public class CompareDiversityCaseContol
{
	public static void main(String[] args) throws Exception
	{
		List<AbstractProjectDescription> list = AllButOne.getLeaveOneOutBaseProjects();
		list.addAll(AllButOne.getLeaveOneOutProjects() );
		
		for( int x=0; x < RunAllClassifiers.TAXA_ARRAY.length; x++)
		{
			String taxa = RunAllClassifiers.TAXA_ARRAY[x];
			System.out.println(taxa);
			BufferedWriter writer =new BufferedWriter(new FileWriter(new File(
				ConfigReader.getMergedArffDir() + File.separator + "diversity_" + taxa +".txt"	)));
			
			writer.write("projectName\tsampleName\tcaseContol\tshannonDiversity\tcompoundKey\n");
			
			for( AbstractProjectDescription apd : RunAllClassifiers.getAllProjects())
			{
				addDiveristy(apd.getNonLogFileKrakenCommonScale(taxa), apd, writer,
						apd.getProjectName() + "_linear");
				
			}
			
			writer.flush(); writer.close();
		}
	}
	
	private static double getShannonDiversity(String[] splits)
	{
		double total = 0;
		
		for( int x=2; x < splits.length; x++)
			total += Double.parseDouble(splits[x]);
		
		double shannon = 0;
		
		for( int x=2; x < splits.length; x++ )
		{
			double p = Double.parseDouble(splits[x]) / total;
			
			if( p > 0 )
				shannon += p * Math.log(p);
		}
		
		return - shannon;
	}
	
	private static void addDiveristy(String inFilePath, AbstractProjectDescription apd,
			BufferedWriter writer, String key ) throws Exception
	{
		if( inFilePath != null)
		{

			File inFile = new File(inFilePath);
			
			if( inFile.exists())
			{

				BufferedReader reader = new BufferedReader(new FileReader(inFile));
				
				reader.readLine();
				
				for(String s = reader.readLine(); s != null; s = reader.readLine())
				{
					String[] splits = s.split("\t");
					
					String caseControl = null;
					
					if( apd.getPositiveClassifications().contains(splits[1]))
						caseControl = "case";
					else if( apd.getNegativeClassifications().contains(splits[1]))
						caseControl = "control";
					
					if( caseControl != null)
					{
						writer.write(apd.getProjectName() + "\t");
						writer.write("sample_" + splits[0] + "\t");
						writer.write(caseControl + "\t");
						writer.write(getShannonDiversity(splits) + "\t");
						writer.write(key + "_" + caseControl +  "\n");
					}
				}
				
				reader.close();
				writer.flush();
			}

		}
	}
}
