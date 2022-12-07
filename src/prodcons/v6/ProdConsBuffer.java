package prodcons.v6;

/**
 * Pour cette version, nous avons corrigé le problème d'incohérence des flux de message
 * de debug en déplaçant les printf dans le fichier ProdConsBuffer.java !
 * 
 * Néanmoins, nous ne parvenons pas à afficher la dernière consommation,
 * elle est systématiquement absente tandis que toutes les autres
 * taĉhes s'affichent parfaitement bien.
 * 
 * Il faudrait inventer de nouvelles méthodes et de nouveaux attributs
 * afin d'afficher correctement chaque message consommé avec un compteur
 * etc...
 */

public class ProdConsBuffer implements IProdConsBuffer {
	
	class Couple {
		int exemplaires;
		Message msg;
		Couple(Message m) {
			msg = m;
			exemplaires = 1;
		}
		Couple(Message m, int exes) {
			msg = m;
			exemplaires = exes;
		}
	}
	
	private static ProdConsBuffer instance;
	private Couple buffer[];
	
	private int nb;
	private final int size;
	
	// Variables spécifiques au comptage du printf
	private int acquired;
	private int total;
	
	private int ind_put;
	private int ind_get;
	
	private ProdConsBuffer(int n) {
		this.buffer = new Couple[n];
		for (int i = 0 ; i < n ; i++)
			this.buffer[i] = new Couple(new Message(), 0);
		this.size = n;
		
		this.total = 0;
		this.nb = 0;
		this.acquired = 0;
		
		this.ind_put = 0;
		this.ind_get = 0;
	}
	
	public static synchronized ProdConsBuffer getInstance(int n) {
		if (instance == null) {
			instance = new ProdConsBuffer(n);
		}
		return instance;
	}
	
	@Override
	public void put(Message m) throws InterruptedException {
		this.put(m, 1);
	}
	
	@Override
	public synchronized void put(Message m, int n) throws InterruptedException {
		while (nb >= size)
			wait();
		
		nb++;
		// Pour les threads Consumer qui attendent que des
		// nouveaux messages arrivent pour les consommer !
		notifyAll();
		
		int index = ind_put;
		this.buffer[index] = new Couple(m, n);
		
		ind_put = (ind_put < size) ? ind_put + 1 : 0;
		
		System.out.printf("Producer Thread %d has produced %d messages\n",
				Thread.currentThread().getId(), n);
		
		while (buffer[index].exemplaires > 0)
			wait();
		// Pour les Threads Consumer qui attendent des messages.
		notifyAll();
	}
	
	@Override
	public synchronized Message get() throws InterruptedException {
		while(nb == 0) {
			wait();
		}
		
		Message msg = buffer[ind_get].msg;
		buffer[ind_get].exemplaires--;
		acquired++;
		if (buffer[ind_get].exemplaires <= 0) {
			ind_get = (ind_get < size) ? ind_get + 1 : 0;
			nb--;
			// Pour des threads Producer qui voudraient savoir si tous les exemplaires
			// de leur dernier message produit ont été consommés, ainsi que les threads
			// Producer qui attendraient que de la place se libère pour pouvoir mettre
			// leur message à eux en n exemplaires.
			notifyAll();
		}
		
		if (acquired == total) {
			System.out.println("Everything has been acquired !");
			if (ind_get == ind_put && nb == 0)
				System.out.println("Every task has been correctly done.");
			else
				System.out.println(
						"ind_get != ind_put || nb != 0");
			System.exit(0);
		}
		
		notifyAll();
		return msg;
	}

	@Override
	public Message[] get(int k) throws InterruptedException {
		Message[] msgs = new Message[k];
		for (@SuppressWarnings("unused") Message msg : msgs) {
			msg = this.get();
		}		
		System.out.printf("Consumer Thread %d has consumed %d messages\n", 
				Thread.currentThread().getId(), k);
		return msgs;
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
