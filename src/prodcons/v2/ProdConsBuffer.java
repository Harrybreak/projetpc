package prodcons.v2;

public class ProdConsBuffer implements IProdConsBuffer{

	private static ProdConsBuffer instance;
	private Message buffer[];
	private final int size;

	private int total;
	private int acquired;
	private int nb;
	
	private int ind_put;
	private int ind_get;
	
	private ProdConsBuffer(int n) {
		this.buffer = new Message[n];
		this.size = n;
		
		this.total = 0;
		this.acquired = 0;
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
		
		nb++;
		
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
		
		if(ind_get == size) {
			ind_get = 0;
		} else {
			ind_get++;
		}
		
		acquired++;
		nb--;
		if (acquired == total) {
			System.out.println("Everything has been acquired !\n");
			System.exit(0);
		}
		else
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

	public void incrTot(long restants) {
		this.total += (int) restants;
	}
}
