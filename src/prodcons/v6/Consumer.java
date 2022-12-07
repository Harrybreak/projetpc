package prodcons.v6;

public class Consumer implements Runnable {

	private Thread th;
	private long temps;
	private long restants;
	private int nomnom;
	
	public Consumer(long nombre, long temps) {
		this.temps = temps;
		this.restants = nombre;
		this.nomnom = 0;
		this.th = new Thread(this);
		this.th.start();
		System.out.printf(
				"Consumer Thread %d is about to consume %d messages.\n",
				this.th.getId(), this.restants);
	}
	
	@Override
	public void run() {
		while (restants > 0) {
			try {
				Thread.sleep(this.temps);
				ProdConsBuffer.getInstance(TestProdCons.TAILLE).
				get(this.nomnom = (int)(Math.random() * ((double)this.restants - 1.0)) + 1);
				restants -= this.nomnom;
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
