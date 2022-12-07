package prodcons.v6;

public class Producer implements Runnable {
	private Thread th;
	private long temps;
	private long restants;
	private int producedAtOnce;
	
	public Producer(long nMin, long nMax, long temps) {
		this.temps = temps;
		this.restants = (long)
				(Math.random() * (double)(nMax - nMin)) + nMin;
		this.producedAtOnce = 0;
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
				ProdConsBuffer.getInstance(TestProdCons.TAILLE).
				put(new Message(), 
						this.producedAtOnce = (int)(Math.random() * ((double)this.restants - 1.0)) + 1);
				restants -= this.producedAtOnce;
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
