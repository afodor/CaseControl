package zScoreClassifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import kraken.RunAllClassifiers;
import projectDescriptors.AbstractProjectDescription;
import zScoreClassifier.ZScoreClassifier.ZHolder;

public class ZScoreCrossClassifier
{
	public static void main(String[] args) throws Exception
	{
		List<AbstractProjectDescription> list = 
				RunAllClassifiers.getAllProjects();
		
		String taxa = "genus";
		
		for( int x=0; x < list.size(); x++)
		{
			HashMap<String, ZHolder> xMap = 
					ZScoreClassifier.getFinalIteration(list.get(x), taxa);
			
			if( xMap != null)
			{
				for(int y=0; y < list.size(); y++)
				{
					if( x != y)
					{
						HashMap<String, ZHolder> yMap = 
								ZScoreClassifier.getFinalIteration(list.get(y), taxa);
						
						if(yMap != null)
						{
							
						}
					}
				}
			}
		}
	}
}
