package cluster;
import java.util.List;

import kraken.RunAllClassifiers;
import projectDescriptors.AbstractProjectDescription;

public class RunCrossClassifiersCluster
{
	public static void main(String[] args) throws Exception
	{
		List<AbstractProjectDescription> projectList = RunAllClassifiers.getAllProjects();
		
		for( int t =0; t < RunAllClassifiers.TAXA_ARRAY.length; t++)
		{ 
			
			for(int x=0; x < projectList.size(); x++)
				for( int y=0; y < projectList.size(); y++)
					if( x != y)
					{
						
					}
		}
		
	}
	
	
}
