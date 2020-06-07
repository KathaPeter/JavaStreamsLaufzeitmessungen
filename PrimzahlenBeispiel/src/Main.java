
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.LongStream;

public class Main {
	
	public static void main(String[] args) throws Exception {

		//Warm-up
		findPrimesUsingStreams(100);
		findPrimesUsingStreams(100);
		findPrimesUsingStreams(100);

		new PrimeFinderThread.PrimeFinderThread1(0, 0);
		new PrimeFinderThread.PrimeFinderThread2(0, 0);
		
		long lastCandidate = 1000 * 1000;
		int times = 20;

		System.out.println("test running..");
		System.out.println("Last Candidate: " + (lastCandidate));
		
		runtTest("Eratosthenes", () -> findPrimeEratosthenes(lastCandidate), times);
		runtTest("threads1", () -> findPrimesUsingThreads1(5, lastCandidate), times);
		
		runtTest("for-Loop", () -> findPrimeForLoop(lastCandidate), times);
		runtTest("seq", () -> findPrimesUsingStreamsSequentially(lastCandidate), times);
		runtTest("threads2", () -> findPrimesUsingThreads2(5, lastCandidate), times);
		runtTest("par", () -> findPrimesUsingStreams(lastCandidate), times);
		
		System.out.println("test end");
	}
	

	private static void findPrimesUsingThreads2(int nrOfThreads, long lastCandidate) {

		// RESET
		PrimeFinderThread.PrimeFinderThread2.primes.clear();

		int size = (int) (lastCandidate + 1);

		int n = (int) ((double) (size / nrOfThreads) + 1.0);

		// CREATE NEW THREADS
		List<PrimeFinderThread.PrimeFinderThread2> threads = new ArrayList<>(nrOfThreads);
		int start = 2;
		int end = 0;
		for (int i = 0; i < nrOfThreads; i++) {

			end = Math.min(size, start + n);

			threads.add(new PrimeFinderThread.PrimeFinderThread2(start, end));
			start = end;
			end = -1;
		}

		for (PrimeFinderThread.PrimeFinderThread2 thread : threads) {
			thread.start();
		}

		try {
			// WAIT FOR TERMINATION
			for (PrimeFinderThread.PrimeFinderThread2 thread : threads) {
				thread.join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public static Set<Long> findPrimesUsingThreads1(int nrOfThreads, long lastCandidate) {

		// RESET
		PrimeFinderThread.PrimeFinderThread1.currentPosition = 2;
		PrimeFinderThread.PrimeFinderThread1.primes.clear();

		// CREATE NEW THREADS
		List<PrimeFinderThread.PrimeFinderThread1> threads = new ArrayList<PrimeFinderThread.PrimeFinderThread1>(
				nrOfThreads);
		for (int i = 0; i < nrOfThreads; i++) {
			threads.add(new PrimeFinderThread.PrimeFinderThread1(nrOfThreads, lastCandidate));
			threads.get(i).start();
		}

		try {
			// WAIT FOR TERMINATION
			for (PrimeFinderThread.PrimeFinderThread1 primeFinderThread : threads) {

				primeFinderThread.join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return PrimeFinderThread.PrimeFinderThread1.primes;
	}

	public static void runtTest(String name, ITest t, int times) {

		double sum = 0.0;
		double worst = 0.0;
		double best = Double.MAX_VALUE;

		
		
		for (int i = 0; i < times; i++) {
			long startZeit = System.currentTimeMillis();

			t.test();

			long endZeit = System.currentTimeMillis();
			long difZeit = endZeit - startZeit;
			double timeInSec = difZeit;
			sum += timeInSec;

			worst = Math.max(worst, timeInSec);
			best = Math.min(best, timeInSec);
		}

		System.out.println("" //
				+ name + " " //
				+ "Zeit: " //
				+ "   MEDIAN " + String.format("%.3f", (sum / (times + 0.0))) + "s" //
				+ "   BEST " + best + "s " //
				+ "   WORST " + worst + "s " //
				+ "   SUM " + String.format("%.3f", sum) + "s " //
		);
	}

	

	private static void findPrimeEratosthenes(long lastCandidate) {
		ArrayList<Boolean> zahlenListe = new ArrayList<Boolean>();

		for (int i = 0; i <= lastCandidate; i++) {
			zahlenListe.add(i, true);
		}
		for (int i = 2; i <= lastCandidate; i++) {

			if (zahlenListe.get(i) == true) {
				int j = i;
				do {
					j = j + i;
					if (j <= lastCandidate) {
					
						zahlenListe.set(j, false);
					}
				} while (j <= lastCandidate);
			}
		}
	}

	private static void findPrimeForLoop(long lastCandidate) {

		long zahl; 
		int zaehler; 
		boolean primzahl; 
		Set<Long> primes = new HashSet<>();

		for (zahl = 2; zahl <= lastCandidate; zahl++) {

			primzahl = true;

			for (zaehler = 2; zaehler < Math.sqrt(zahl) + 1; zaehler++) {
				if (zahl % zaehler == 0) {

					primzahl = false;
					break;
				}

			}

			if (primzahl) {
				primes.add(zahl);
			}
		}

	}

	public static long[] findPrimesUsingStreams(long lastCandidate) {
		LongStream primes = LongStream.range(2, lastCandidate).parallel()
				.filter(n -> LongStream.rangeClosed(2, (long) Math.sqrt(n)).noneMatch(divisor -> n % divisor == 0));
		return primes.toArray();
	}

	public static long[] findPrimesUsingStreamsSequentially(long lastCandidate) {
		LongStream primes = LongStream.range(2, lastCandidate)
				.filter(n -> LongStream.rangeClosed(2, (long) Math.sqrt(n)).noneMatch(divisor -> n % divisor == 0));
		return primes.toArray();
	}

}
