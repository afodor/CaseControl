package ratioSpace;

import java.io.BufferedReader;
import java.io.File;
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
					else
					{
						System.out.println( normString + " _" +  topSplits[y] + "_");

					}
				}
				
				
				
				reader.close();
				
				if(normColumn == -1)
					throw new Exception("Could not find " + normString);
			}
			

		}
		
	}
}
