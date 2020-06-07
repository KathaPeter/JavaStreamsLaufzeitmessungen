import java.util.List;

public class MapThread {

	public static List<Integer> list;

	static class MapThread2 extends Thread {

		final int i0;
		final int iN;

		public MapThread2(int i0, int iN) {
			this.i0 = i0;
			this.iN = iN;
		}

		public void run() {
			startRunning();
		}

		private void startRunning() {
			for (int i = i0; i < iN; i++) {
				list.get(i).toString();

			}
		}

	}
}
