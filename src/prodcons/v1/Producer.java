package prodcons.v1;

public class Producer implements Runnable{
	private Thread th;
	private long temps;
	private long restants;
	
	public Producer(long nMin, long nMax, long temps) {
		this.temps = temps;
		this.restants = (long)
				(Math.random() * (double)(nMax - nMin)) + nMin;
		this.th = new Thread(this);
		this.th.start();
	}
	
	@Override
	public void run() {
		while (restants > 0) {
			try {
				Thread.sleep(this.temps);
				ProdConsBuffer.getInstance(TestProdCons.TAILLE).put(new Message("q"));
				restants--;
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void join() throws InterruptedException {
		this.th.join();
	}
}
