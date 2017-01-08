package diverticulosis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import parsers.NewRDPParserFileLine;
import projectDescriptors.AbstractProjectDescription;
import projectDescriptors.Divitriculosis2015ProjectDescriptor;
import utils.ConfigReader;

public class WriteToCaseContolSpreadsheet
{
	public static void main(String[] args) throws Exception
	{
		/*
		for(int x=1; x < NewRDPParserFileLine.TAXA_ARRAY.length; x++)
		{
			String taxa = NewRDPParserFileLine.TAXA_ARRAY[x];
			System.out.println(taxa);
			AbstractProjectDescription apb = new Divitriculosis2015ProjectDescriptor();
			
			File inFile = new File(ConfigReader.getTopeOneAtATimeDir() + File.separator + 
					"merged" + File.separator + "pivoted_" +  
					NewRDPParserFileLine.TAXA_ARRAY[x] + "asColumnsLogNormalPlusMetadata.txt");
			
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
				

				if( Integer.parseInt(splits[2]) == 1)
				{
					String caseContol = splits[6];
					
					if( caseContol.equals("0") || caseContol.equals("1"))
					{
						writer.write(splits[0] + "\t" + caseContol);
						
						for (int y=9; y < topSplits.length; y++)
							writer.write("\t" + splits[y]);
						
						writer.write("\n");
					}
				}
			}
			
			writer.flush();  writer.close();
			reader.close();
	
		}*/
	}
}
