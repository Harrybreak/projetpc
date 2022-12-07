package prodcons.v6;

import java.util.concurrent.Semaphore;

/**
 * Pour cette version, nous avons corrigé le problème d'incohérence des flux de message
 * de debug en déplaçant les printf dans le fichier ProdConsBuffer.java !
 */

public class ProdConsBuffer implements IProdConsBuffer{

	private static ProdConsBuffer instance;
	private Semaphore s;
	private Message buffer[];
	private final int size;

	private int acquired;
	private int total;
	private int nb;
	
	private int ind_put;
	private int ind_get;
	
	private ProdConsBuffer(int n) {
		this.buffer = new Message[n];
		this.size = n;
		
		this.total = 0;
		this.nb = 0;
		this.acquired = 0;
		
		this.ind_put = 0;
		this.ind_get = 0;
		
		this.s = new Semaphore(1);
	}
	
	public static synchronized ProdConsBuffer getInstance(int n) {
		if (instance == null) {
			instance = new ProdConsBuffer(n);
		}
		return instance;
	}
	
	@Override
	public synchronized void put(Message m) throws InterruptedException {
		while(nb == size) {
			wait();
		}
		buffer[ind_put] = m;
		
		nb++;
		
		ind_put = (ind_put == size-1) ? 0 : ind_put + 1;
		
		System.out.printf("Producer Thread %d has produced 1 message\n", 
				Thread.currentThread().getId());
		
		notifyAll();
	}

	@Override
	public void put(Message m, int n) throws InterruptedException {
		
	}

	@Override
	public synchronized Message get() throws InterruptedException {
		while(nb == 0) {
			wait();
		}
		Message msg = buffer[ind_get];
		
		ind_get = (ind_get == size-1) ? 0 : ind_get + 1;
		
		acquired++;
		
		System.out.printf("Consumer Thread %d has consumed 1 message\n", 
				Thread.currentThread().getId());
		
		if (acquired == total) {
			System.out.println("Everything has been acquired !");
			System.exit(0);
		}
		
		nb--;
		notifyAll();
		return msg;
	}

	@Override
	public Message[] get(int k) throws InterruptedException {
		try {
			Message[] msgs = new Message[k];
			this.s.acquire();
			for (@SuppressWarnings("unused") Message msg : msgs) {
				msg = this.get();
			}
			return msgs;
		} finally {
			System.out.printf("Consumer Thread %d has consumed %d message\n", 
				Thread.currentThread().getId(), k);
			this.s.release();
		}
	}

	@Override
	public int nmsg() {
		return this.nb;
	}

	@Override
	public int totmsg() {
		return this.total;
	}

	public void incrTot(long restants) {
		this.total += (int) restants;
	}
}
