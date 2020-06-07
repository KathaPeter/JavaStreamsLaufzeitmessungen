import java.util.HashSet;
import java.util.Set;

public class PrimeFinderThread extends Thread {

	static class PrimeFinderThread1 extends Thread {
		public final int nrOfParallelThreads;
		public final long lastCandidateToCheck;
		public static long currentPosition = 2;
		public static Set<Long> primes = new HashSet<>();

		public PrimeFinderThread1(int nrOfParallelThreads, long lastCandidateToCheck) {
			this.nrOfParallelThreads = nrOfParallelThreads;
			this.lastCandidateToCheck = lastCandidateToCheck;
		}

		@Override
		public void run() {
			long currentCandidate;
			while ((currentCandidate = getNextCandidate()) <= lastCandidateToCheck) {
				if (isPrime2(currentCandidate)) {
					synchronized (primes) {
						primes.add(currentCandidate);
					}
				}
			}
		}

		private boolean isPrime2(long currentCandidate) {
			boolean foundPrime = true;

			long max = (long) Math.sqrt(currentCandidate);
			for (long test = 2L; test < max; test++) {

				if (0 == currentCandidate % test) {
					foundPrime = false;
					break;
				}
			}

			return foundPrime;
		}

		public synchronized long getNextCandidate() {
			long nextCandidate = currentPosition;
			currentPosition++;
			return nextCandidate;
		}
	}

	static class PrimeFinderThread2 extends Thread {

		public static Set<Long> primes = new HashSet<>();
		private long i0;
		private long iN;

		public PrimeFinderThread2(long i0, long iN) {
			this.i0 = i0;
			this.iN = iN;
		}

		@Override
		public void run() {
			
			for (  long currentCandidate = i0 ; currentCandidate < iN ; currentCandidate++) {
				if (isPrime(currentCandidate)) {
					synchronized (primes) {
						primes.add(currentCandidate);
					}
				}
			}
		}

		private static boolean isPrime(long currentCandidate) {
			boolean foundPrime = true;
			long root = (long) Math.sqrt(currentCandidate);
			
			for (long divisor = 2L; divisor <= root; divisor++) {
				if (0 == currentCandidate % divisor) {
					foundPrime = false;
					break;
				}
			}

			return foundPrime;
		}

		
		
	}
}