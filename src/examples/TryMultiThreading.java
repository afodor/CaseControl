package examples;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;


public class TryMultiThreading
{
	
	public static void main(String[] args) throws Exception
	{
		System.out.println(Runtime.getRuntime().availableProcessors());
		
		File adenomas = new File("C:\\adenomasRelease\\spreadsheets\\pivoted_" + 
				"genus" + "LogNormalWithMetadataBigSpace.arff");
		
		// 4 threads, 1000 each 
		int numThreads = 4;
		Semaphore s = new Semaphore(numThreads);
		long startTime = System.currentTimeMillis();
		Random r = new Random();
		
		for( int x=0; x < 4; x++ )
		{
			s.acquire();
			Worker w = new Worker(adenomas, 10, s,r );
			new Thread(w).start();
		}
		
		for( int x=0; x < 4; x++)
			s.acquire();
		
		System.out.println("Time " + (System.currentTimeMillis() - startTime) / 1000f);
		
		startTime = System.currentTimeMillis();
		s = new Semaphore(1);
		Worker w = new Worker(adenomas, 40, s,r );
		s.acquire();
		new Thread(w).start();
		s.acquire();
		
		System.out.println("Time " + (System.currentTimeMillis() - startTime) / 1000f);
	}
	
	
	public static class Worker implements Runnable
	{
		private final File fileToRun;
		private final int numPermutations;
		private volatile List<Double> rocResults = null;
		private final Semaphore s;
		private final Random random;
		
		//not thread safe should - should only be called
		// after the thread that runs through this object has finished
		public List<Double> getROCResults() throws Exception
		{
			return rocResults;
		}
		
		public Worker(File fileToRun, int numPermutations, Semaphore s, Random random)
		{
			this.fileToRun = fileToRun;
			this.numPermutations = numPermutations;
			this.s = s;
			this.random = random;
		}
		
		@Override
		public void run()
		{
			try
			{

				this.rocResults = TestClassify.plotROCForAnArff(
						fileToRun, numPermutations, random,false,null);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				System.exit(1);
			}
			finally
			{
				s.release();
			}
		}
	}
	
}
