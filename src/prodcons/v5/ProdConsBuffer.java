package prodcons.v5;

import java.util.concurrent.Semaphore;

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
		
		System.out.printf("Producer Thread %d has produced 1 message, %d remaining messages\n",
				Thread.currentThread().getId(), this.getRemaining());
		
		nb++;
		
		if(ind_put == size-1) {
			ind_put = 0;
		} else {
			ind_put++;
		}
		notifyAll();
	}

	@Override
	public synchronized Message get() throws InterruptedException {
		while(nb == 0) {
			wait();
		}
		Message msg = buffer[ind_get];
		
		if(ind_get == size-1) {
			ind_get = 0;
		} else {
			ind_get++;
		}
		
		acquired++;
		
		System.out.printf("Consumer Thread %d has consumed a message, %d remaining messages\n",
				Thread.currentThread().getId(), this.getRemaining());
		
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
			s.acquire();
			for (@SuppressWarnings("unused") Message msg : msgs) {
				msg = this.get();
			}
			return msgs;
		} finally {
			s.release();
		}
	}

	@Override
	public final int nmsg() {
		return nb;
	}
	
	public final int getRemaining() {
		return total - acquired;
	}

	@Override
	public final int totmsg() {
		return total;
	}

	public void incrTot(long restants) {
		this.total += (int) restants;
	}
}
