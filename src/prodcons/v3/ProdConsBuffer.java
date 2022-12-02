package prodcons.v3;

import java.util.concurrent.Semaphore;

public class ProdConsBuffer implements IProdConsBuffer{

	private static ProdConsBuffer instance;
	private Message buffer[];
	private final int size;

	private int total;
	private int acquired;
	private int nb;
	
	private int ind_put;
	private int ind_get;
	
	private static Semaphore sinst;
	private static Semaphore sput;
	private static Semaphore sget;
	
	private ProdConsBuffer(int n) {
		this.buffer = new Message[n];
		this.size = n;
		
		this.total = 0;
		this.acquired = 0;
		this.nb = 0;
		
		this.ind_put = 0;
		this.ind_get = 0;
	}
	
	public static ProdConsBuffer getInstance(int n) throws InterruptedException {
		try {
			if (instance == null){
				instance = new ProdConsBuffer(n);		
				ProdConsBuffer.sinst= new Semaphore(0);	
				ProdConsBuffer.sput = new Semaphore(n);
				ProdConsBuffer.sget = new Semaphore(n);
			}
			ProdConsBuffer.sinst.acquire();
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
}
