import java.util.ArrayList;
import java.util.List;

public class Main {

	private static final int number = 1000000;
	private static final int nrOfThreads = 3;

	public static void main(String[] args) {

		int times = 30;

		//Start twice a time for shorter runtime (it's like a warm-up)
		runtTest("ForLoop", Main::testForLoop, times);
		runtTest("ForLoop", Main::testForLoop, times);
		runtTest("SeqStream", Main::testSeqStream, times);
		runtTest("SeqStream", Main::testSeqStream, times);
		runtTest("ParStream", Main::testParStream, times);
		runtTest("ParStream", Main::testParStream, times);
		runtTest("Threads2", Main::convertElementsThread2Impl, times);
		runtTest("Threads2", Main::convertElementsThread2Impl, times);	
	}

	public static void runtTest(String name, ITest t, int times) {

		double sum = 0.0;
		double worst = 0.0;
		double best = Double.MAX_VALUE;

		ArrayList<Integer> list = new ArrayList<>();
		for (int i = 0; i < number; i++) {
			list.add(i);
		}

		for (int i = 0; i < times; i++) {
			long startZeit = System.currentTimeMillis();

			t.test(list);

			long endZeit = System.currentTimeMillis();
			long difZeit = endZeit - startZeit;
			double timeInSec = difZeit;
			sum += timeInSec;

			worst = Math.max(worst, timeInSec);
			best = Math.min(best, timeInSec);
		}

		System.out.println("" //
				+ "Test: " + String.format("%10s", name) + " " //
				+ "Zeit: " //
				+ "   MEDIAN " + String.format("%.2f", (sum / (times + 0.0))) + "ms" //
				+ "   BEST " + String.format("%.2f", best) + "ms " //
				+ "   WORST " + String.format("%.2f", worst) + "ms " //
				+ "   SUM " + String.format("%.2f", sum) + "ms " //
		);
	}

	interface ITest {
		void test(List<Integer> list);
	}

	static void testForLoop(List<Integer> list) {
		for (int i = 0; i < list.size(); i++) {
			list.get(i).toString();
		}

	}

	static void testSeqStream(List<Integer> list) {

		list.stream().map(s -> s.toString()).forEach(s -> {
		});

	}

	static void testParStream(List<Integer> list) {
		list.parallelStream().map(s -> s.toString()).forEach(s -> {
		});
	}

	public static void convertElementsThread2Impl(List<Integer> list) {

		// RESET
		MapThread.list = list;

		int size = list.size();
		
		int n = (int) ((double) (size / nrOfThreads) + 1.0);

		// CREATE NEW THREADS
		List<MapThread.MapThread2> threads = new ArrayList<>(nrOfThreads);
		int start = 0;
		int end = 0;
		for (int i = 0; i < nrOfThreads; i++) {

			end = Math.min(size, start + n);

			threads.add(new MapThread.MapThread2(start, end));
			start = end;
			end = -1;
		}

		for (MapThread.MapThread2 mapThread : threads) {
			mapThread.start();
		}

		try {
			// WAIT FOR TERMINATION
			for (MapThread.MapThread2 mapThread : threads) {
				mapThread.join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
