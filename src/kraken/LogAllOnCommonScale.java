package kraken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import projectDescriptors.AbstractProjectDescription;

public class LogAllOnCommonScale
{
	public static void main(String[] args) throws Exception
	{
		for( int x= RunAllClassifiers.TAXA_ARRAY.length-1; x >=0 ; x--)
		{
			String taxa= RunAllClassifiers.TAXA_ARRAY[x];
			System.out.println(taxa);

			for(AbstractProjectDescription apd : RunAllClassifiers.getAllProjects())
			{
				BufferedReader reader = new BufferedReader(new FileReader(
						apd.getCountFileKraken(taxa)));
				
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						apd.getLogFileKrakenCommonScale(taxa)));
				
				String topLine = reader.readLine() ;
				writer.write(topLine+ "\n");
				String[] topSplits = topLine.split("\t");
				
				for(String s= reader.readLine(); s != null; s= reader.readLine())
				{
					String[] splits =s.split("\t");
					
					if( splits.length != topSplits.length)
						throw new Exception("Parsing error");
					
					long count = 0;
					
					for( int y=2; y < splits.length; y++)
					{
						count += Long.parseLong(splits[y]);
					}
					
					if( count >= AbstractProjectDescription.MIN_SEQUENCE_THRESHOLD)
					{
						writer.write(splits[0] + "\t" + splits[1] );
						
						for( int y=2; y < splits.length; y++)
						{
							double transform = Math.log10(Double.parseDouble(splits[y])+ 1)/count;
							writer.write("\t" +  transform);
						}
						
						writer.write("\n");
					}
				}
				
				writer.flush();  writer.close();
				reader.close();
			}
		}
		
	}
}
