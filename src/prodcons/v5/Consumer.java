package prodcons.v5;

/**
 * Un flot d'exécution correct :
 * 
 * P14 : 7 *+1*+1*+1*+1*+1*  *+1*+1|
 * P15 : 5 *  *+1*+1*+1*+1*  *+1|
 * P16 : 4 *  *+1*+1*+1*  *+1|
 * C17 : 5 *  *  *  *-1*  *  *  *-1*-2*
 * C18 : 5 *  *  *  *-2*  *  *  *-1*-1*
 * C19 : 5 *  *  *  *-1*  *  *  *-2*-1*
 * C20 : 5 *  *  *  *  *-1*  *  *-1*  *
 * C21 : 5 *  *  *  *  *  *-1*  *-1*  *
 * Total = * 1* 4* 7* 6* 7* 7* 9* 4* 0 --> FINI !
 * 
 * @author lilian
 *
 */

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
		System.out.printf("Consumer Thread %d is about to consume %d messages.\n", this.th.getId(), this.restants);
	}
	
	@Override
	public void run() {
		while (restants > 0) {
			try {
				Thread.sleep(this.temps);
				ProdConsBuffer.getInstance(TestProdCons.TAILLE).
				get(this.nomnom = (int)(Math.random() * ((double)this.restants - 1.0)) + 1);
				restants -= this.nomnom;
				System.out.printf("Consumer Thread %d has consumed %d message, %d remaining messages\n", this.th.getId(), this.nomnom, this.restants);
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
