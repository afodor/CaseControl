package china;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import parsers.NewRDPParserFileLine;
import projectDescriptors.AbstractProjectDescription;
import projectDescriptors.China2015_Timepoint1;
import projectDescriptors.China2015_Timepoint2;
import utils.ConfigReader;

public class WriteToCaseControlSpreadsheets
{
	public static void main(String[] args) throws Exception
	{
		/*
		for(int x=1; x < NewRDPParserFileLine.TAXA_ARRAY.length; x++)
		{
			String[] timepoints = { "first_A", "second_B" };
			String taxa = NewRDPParserFileLine.TAXA_ARRAY[x];
			System.out.println(taxa);
			
			for(String time : timepoints)
			{
				AbstractProjectDescription apb = 
						time.equals( timepoints[0]) 
							? new China2015_Timepoint1() : new China2015_Timepoint2();
				
				File inFile = new File(ConfigReader.getChinaDir() + File.separator + 
						taxa + "_taxaAsColumnsLogNorm_WithMetadata.txt");
				
				BufferedReader reader = new BufferedReader(new FileReader(inFile));
				
				BufferedWriter writer = new BufferedWriter(
						new FileWriter(apb.getLogNormalizedRDPCounts(taxa)));
				
				String[] topSplits = reader.readLine().split("\t");
				
				writer.write("key\tcaseContol");
				
				for( int y=9; y < topSplits.length; y++)
					writer.write("\t" + topSplits[y]);
				
				writer.write("\n");
				
				for(String s= reader.readLine(); s != null; s = reader.readLine())
				{
					String[] splits= s.split("\t");
					
					if( Integer.parseInt(splits[1]) == 1 && splits[8].equals(time))
					{
						
						writer.write(splits[0] + "\t" + splits[7]);
							
						for (int y=9; y < topSplits.length; y++)
							writer.write("\t" + splits[y]);
							
						writer.write("\n");
					}
				}
				
				writer.flush();  writer.close();
				reader.close();
			}
		}
		*/
	}
}
