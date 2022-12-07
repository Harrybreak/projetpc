package prodcons.v6;

import java.util.concurrent.Semaphore;

public class ProdConsBuffer implements IProdConsBuffer{

	private static ProdConsBuffer instance;
	private static Semaphore s;
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
	}
	
	public static synchronized ProdConsBuffer getInstance(int n) {
		if (instance == null) {
			s = new Semaphore(1);
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
		total++;
		
		if(ind_put == size-1) {
			ind_put = 0;
		} else {
			ind_put++;
		}
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
		
		if(ind_get == size-1) {
			ind_get = 0;
		} else {
			ind_get++;
		}
		
		acquired++;
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
			for (Message msg : msgs) {
				msg = this.get();
			}
			return msgs;
		} finally {
			s.release();
		}
	}

	@Override
	public int nmsg() {
		return nb;
	}

	@Override
	public int totmsg() {
		return total;
	}
}
