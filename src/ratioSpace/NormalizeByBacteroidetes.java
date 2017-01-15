package ratioSpace;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;

import parsers.NewRDPParserFileLine;
import projectDescriptors.AbstractProjectDescription;
import projectDescriptors.AllButOne;

public class NormalizeByBacteroidetes
{
	public static void main(String[] args) throws Exception
	{
		for(AbstractProjectDescription apd : AllButOne.getLeaveOneOutBaseProjects())
		{
			for( int x=1; x < NewRDPParserFileLine.TAXA_ARRAY.length; x++)
			{
				String taxa = NewRDPParserFileLine.TAXA_ARRAY[x];
				String normString = apd.BACTEROIDETES[x-1];
				
				int normColumn = -1;
				
				BufferedReader reader = new BufferedReader(new FileReader(new File(
						apd.getCountFileKraken(taxa))));
				System.out.println(apd.getCountFileKraken(taxa));
				
				String[] topSplits = reader.readLine().split("\t");
				
				for(int y=0; y < topSplits.length; y++)
				{
					if( topSplits[y].equals(normString))
					{
						if(normColumn != -1)
							throw new Exception("No");
						
						normColumn = y;
					}
				}
				
				
				if(normColumn == -1)
					throw new Exception("Could not find " + normString);
				
				BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
						apd.getNormalizedByBacteroidetes(taxa))));
				
				writer.write(topSplits[0]);
				for(int y=1; y < topSplits.length; y++)
					if( y != normColumn)
						writer.write("\t" + topSplits[y]);
				
				writer.write("\n");
				
				for(String s= reader.readLine(); s != null; s= reader.readLine())
				{
					String[] splits = s.split("\t");
					
					double bactCount = Double.parseDouble(splits[normColumn]) + 1;
					
					writer.write(splits[0] + "\t" + splits[1]);
					
					for( int y=2; y < splits.length; y++)
					{
						if( y != normColumn)
						{
							double thisCount = Double.parseDouble(splits[y]) + 1;
							writer.write("\t" + Math.log10(thisCount/bactCount));
						}
					}
					
					writer.write("\n");
				}
				
				writer.flush();  writer.close();
				
				reader.close();
				
			}
			

		}
		
	}
}
