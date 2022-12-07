package prodcons.v3;

public class Producer implements Runnable{
	private Thread th;
	private long temps;
	private long restants;
	
	public Producer(long nMin, long nMax, long temps) throws InterruptedException {
		this.temps = temps;
		this.restants = (long)
				(Math.random() * (double)(nMax - nMin)) + nMin;
		this.th = new Thread(this);
		this.th.start();
		System.out.printf(
				"Producer Thread %d will create %d messages.\n",
				this.th.getId(), this.restants);
		ProdConsBuffer.getInstance(TestProdCons.TAILLE).incrTot(this.restants);
	}
	
	@Override
	public void run() {
		while (restants > 0) {
			try {
				Thread.sleep(this.temps);
				ProdConsBuffer.getInstance(TestProdCons.TAILLE).put(new Message("q"));
				restants--;
				System.out.printf("Producer Thread %d has produced 1 message, %d remaining messages\n", this.th.getId(), this.restants);
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
