package peg;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Engine implements Comparator<Peg_game>{
	/* Queue of states */
	public PriorityBlockingQueue<Peg_game> q;
	
	/* Map of visited states */
	public Set<String> q2 = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
		
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
	
	/* Load factor for hashmap */
	public final float loadFactor = 0.65f;
	
	/* Threadpool */
	public ExecutorService pool;
	
	/* Number of job started */
	public AtomicInteger activethread;
		
	/* Avvio con seme */
	public Engine(Peg_game seed) {
		this.q = new PriorityBlockingQueue<Peg_game>(limitSize,this);
		this.q.add(seed); 
		
		//this.q2 = new ConcurrentHashMap<Peg_game,Peg_game>(limitSize,loadFactor);
		this.q2.add(seed.toString());
		this.pool = Executors.newFixedThreadPool(cores);
		this.activethread = new AtomicInteger(0);
	}
	
	/**
	 * Expand a state putting its successors in the queue
	 * @return the solution state if the goal has been reached
	 * null otherwise
	 **/
	public Peg_game expand() throws InterruptedException {
		if(pool.isShutdown()) return null; //must exit
		
		Peg_game item = q.take();
		expandedNodes++;
		
		if(item.value()==0){ 
			pool.shutdown();
			return item; //controllo se sono allo stato goal
		}
		
		if(item.V_space==0){ 
			pool.shutdown();
			return null; //controllo se sono allo stato fail
		}
		
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
				
		while(	!(q.isEmpty() && activethread.compareAndSet(0,0)) // && ho finito tutti i thread ( ma dipende da quale struttura decido di usare... )
				&& q.size() < limitSize 				// Coda troppo lunga
				&& son == null 							// Ho trovato una soluzione
				&& !pool.isShutdown() ){
			son = expand();
		}
		
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