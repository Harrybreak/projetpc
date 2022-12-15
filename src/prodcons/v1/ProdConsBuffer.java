package prodcons.v1;

public class ProdConsBuffer implements IProdConsBuffer{

	private static ProdConsBuffer instance;
	private Message buffer[];
	private final int size;

	private int total;
	private int nb;
	
	private int ind_put;
	private int ind_get;
	
	private ProdConsBuffer(int n) {
		this.buffer = new Message[n];
		this.size = n;
		
		this.total = 0;
		this.nb = 0;
		
		this.ind_put = 0;
		this.ind_get = 0;
	}
	
	public static synchronized ProdConsBuffer getInstance(int n) {
		if (instance == null)
			instance = new ProdConsBuffer(n);
		return instance;
	}
	
	@Override
	public synchronized void put(Message m) throws InterruptedException {
		while(nb == size) {
			wait();
		}
		buffer[ind_put] = m;

		System.out.printf("Producer Thread %d has produced 1 message\n",
				Thread.currentThread().getId());
		
		nb++;
		total++;
		
		if(ind_put == size) {
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
		System.out.printf("Consumer Thread %d has consumed 1 message\n",
				Thread.currentThread().getId());
		
		if(ind_get == size) {
			ind_get = 0;
		} else {
			ind_get++;
		}
		
		nb--;
		notifyAll();
		return msg;
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
