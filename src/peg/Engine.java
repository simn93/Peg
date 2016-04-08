package peg;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings("unused")
public class Engine implements Comparator<Peg_game>{
	/* Coda di stati */
	public PriorityQueue<Peg_game> q;
	public HashSet<Peg_game> q2;
	//public PriorityBlockingQueue<Peg_game> q;
	//public ConcurrentLinkedQueue<Peg_game> q2; // per l'aggiunta dinamica
	//public ConcurrentLinkedQueue<Peg_game> q3; //FIXME aggiusta i nomi...
	
	//public ConcurrentSkipListSet<Peg_game> q2; //coda dei buoni // per l'aggiunta dinamica
	//public ConcurrentSkipListSet<Peg_game> q1;//coda dei fail
	//public PriorityQueue<Peg_game> q; // per l'estrazione ordinata
	
	/* Number of expanded nodes */
	public int expandedNodes = 0;
	
	/* Number of added table */
	public int addedNodes = 0;
	
	/* Limit value for queue insertion */
	public final int limitValue = 1000;
	
	/* Limit size for queue */
	public final int limitSize = 1000000;
	
	/* Number of local cores */
	public final int cores = Runtime.getRuntime().availableProcessors();
	
	public final float loadFactor = 0.65f;
	/* Threadpool */
	public ExecutorService pool;
	
	public AtomicInteger activethread;
	//final Lock lock = new ReentrantLock();
	//final Condition Empty  = lock.newCondition(); 
	
	//public Thread queueSincronizer;
	/* Gestione dell'accesso - modello lettori-scrittori */
	public final Lock mtx = new ReentrantLock();
	public final Condition emptycnd=mtx.newCondition(), fullcnd=mtx.newCondition();
	
	/* Avvio con seme */
	public Engine(Peg_game seed) {
		this.expandedNodes = 0;
		this.q = new PriorityQueue<Peg_game>(limitSize,this);
		//this.q = new HashSet<Peg_game>(limitSize,this);
		this.q.add(seed); 
		
		this.pool = Executors.newFixedThreadPool(cores);
		//this.q2 = new ConcurrentSkipListSet<Peg_game>(this);
		//this.q2 = new ConcurrentLinkedQueue<Peg_game>();
		this.activethread = new AtomicInteger(0);
		
		this.q2 = new HashSet<Peg_game>(limitSize,loadFactor);
		this.q2.add(seed);
		//this.queueSincronizer = new Thread(new ListCopy(this));
		//this.pool.execute(new ListCopy(this));
	}
	
	/**
	 * Expand a state putting its successors in the queue
	 * @return the solution state if the goal has been reached
	 * null otherwise
	 **/
	public Peg_game expand() throws InterruptedException {
		if(pool.isShutdown()) return null; //coda vuota => fallimento
		
		mtx.lock();
		while(q.isEmpty())emptycnd.await();
		Peg_game item = q.poll();
		//if(q.size()<limitSize/2)fullcnd.signalAll();
		mtx.unlock();
		
		expandedNodes++;
		
		if(item.value()==0){ 
			pool.shutdown();
			return item; //controllo se sono allo stato goal
		}
		
		if(item.moves.size()==0 || item.V_space == 0){ 
			pool.shutdown(); 
			return null; 
		} //c'è stato un errore, lo stato passato è quello iniziale senza vuoti
		
		for (int i=0; i<item.moves.size();i++) { //visito TUTTI i figli
			this.addedNodes++;
			this.activethread.getAndIncrement();
			pool.execute((Runnable)new Peg_game(item,item.moves.get(i),this,item));
		}
		
		return null;
	}
	
	/**
	 * Perform a complete search
	 * @return the solution state if the goal has been reached
	 * null otherwise
	 * @throws InterruptedException 
	 */
	public Peg_game completeSearch() throws InterruptedException {
		Peg_game son;
		son = expand();
		//ConcurrentSkipListSet<Peg_game> cloned;
		
		while(	!(q.isEmpty() && activethread.equals(new Integer(0))) // && ho finito tutti i thread ( ma dipende da quale struttura decido di usare... )
				&& q.size() < limitSize 				// Coda troppo lunga
				&& son == null 							// Ho trovato una soluzione
				&& !pool.isShutdown() ){
			son = expand();
		}
		//this.Empty.signal();
		//this.queueSincronizer.interrupt();
		//q.clear();
		//mtx.lock();
		//fullcnd.signalAll();
		//mtx.unlock();
		pool.shutdown();
		java.awt.Toolkit.getDefaultToolkit().beep();
		return son;
	}
	
	/**
	 * @return a string with the queue size
	 * the minimal value and the number of expanded nodes.
	 */
	public String toString() {
		String ret = "";
		
		ret+= "Queue size : " + q.size();
		if(q.isEmpty())ret+=", Empty queue";
		else{ret+= ", min value : " + q.peek().value();} 
		ret+= ", expanded nodes : " + expandedNodes;
		ret+= ", evaluated nodes : " + addedNodes;
		return ret+".";
	}
	
	/**
	 * Comparator for the Priority Queue
	 * @return the sign of the difference arg0.value()-arg1.value()
	 */
	public int compare(Peg_game arg0, Peg_game arg1) {
		return (int) Math.signum(arg0.value() - arg1.value());
	}
}