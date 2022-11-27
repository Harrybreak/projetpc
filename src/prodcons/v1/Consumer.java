package prodcons.v1;

public class Consumer implements Runnable {

	private Thread th;
	private long temps;
	private long restants;
	
	public Consumer(long nombre, long temps) {
		this.temps = temps;
		this.restants = nombre;
		this.th = new Thread(this);
		this.th.start();
	}
	
	@Override
	public void run() {
		while (restants > 0) {
			try {
				Thread.sleep(this.temps);
				ProdConsBuffer.getInstance(TestProdCons.TAILLE).get();
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
