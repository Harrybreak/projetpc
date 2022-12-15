package prodcons.v3;

import java.util.concurrent.Semaphore;

/**
 * L'affichage peut être incohérent si les threads sont commutés
 * au 'mauvais' endroit dans le run de Consumer.java et Producer.java
 */

public class ProdConsBuffer implements IProdConsBuffer{

	private static ProdConsBuffer instance;
	private Message buffer[];
	private final int size;

	private int total;
	private int acquired;
	private int nb;
	
	private int ind_put;
	private int ind_get;
	
	private static Semaphore sinst = new Semaphore(1);
	private Semaphore sput;
	private Semaphore sget;
	
	private ProdConsBuffer(int n) {
		this.buffer = new Message[n];
		this.size = n;
		
		this.total = 0;
		this.acquired = 0;
		this.nb = 0;
		
		this.ind_put = 0;
		this.ind_get = 0;		
		this.sput = new Semaphore(n);
		this.sget = new Semaphore(n);
	}
	
	public static ProdConsBuffer getInstance(int n) throws InterruptedException {
		try {
			ProdConsBuffer.sinst.acquire();
			if (instance == null){
				instance = new ProdConsBuffer(n);
			}
			return instance;
		} finally {
			ProdConsBuffer.sinst.release();
		}
	}
	
	@Override
	public void put(Message m) throws InterruptedException {
		sput.acquire();
		buffer[ind_put] = m;
		
		total++;
		
		if(ind_put == size) {
			ind_put = 0;
		} else {
			ind_put++;
		}
		sget.release();
	}

	@Override
	public Message get() throws InterruptedException {
		sget.acquire();
		Message msg = buffer[ind_get];
		
		if(ind_get == size) {
			ind_get = 0;
		} else {
			ind_get++;
		}
		
		acquired++;
		sput.release();
		if (acquired == total) {
			System.out.println("Everything has been acquired !\n");
			System.exit(0);
		}
		return msg;
	}

	@Override
	public final int nmsg() {
		return nb;
	}

	@Override
	public final int totmsg() {
		return total;
	}
	
	public final int getRemaining() {
		return total - acquired;
	}
	
	public void incrTot(long restants) {
		this.total += (int) restants;
		System.out.printf("UPDATE : %d messages to consume !\n", this.total);
	}
}
