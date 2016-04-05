package peg;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

public class Engine implements Comparator<Peg_game>{
	/* Coda di stati */
	public PriorityBlockingQueue<Peg_game> q;
	
	/* Number of expanded nodes */
	public int expandedNodes=0;
	
	/* Number of added table */
	public int addedNodes = 0;
	
	/* Limit value for queue insertion */
	public final int limitValue = 1000;
	
	/* Limit size for queue */
	public final int limitSize = 100000;
	
	/* Number of local cores */
	public final int cores = Runtime.getRuntime().availableProcessors();
	
	/* Threadpool */
	public ExecutorService pool;
	
	/* Avvio con seme */
	public Engine(Peg_game seed) {
		this.expandedNodes = 0;
		this.q = new PriorityBlockingQueue<Peg_game>(limitSize,this);
		this.q.add(seed); 
		this.pool = Executors.newFixedThreadPool(cores);
	}
	
	/**
	 * Expand a state putting its successors in the queue
	 * @return the solution state if the goal has been reached
	 * null otherwise
	 **/
	public Peg_game expand() {
		expandedNodes++;
		if(pool.isShutdown()) return null; //coda vuota => fallimento
		
		try {
			Peg_game item = q.take();//prendo ed estraggo il primo elemento da q.attendo se la coda è vuota
						
			if(item.value()==0){
				pool.shutdown();
				return item; //controllo se sono allo stato goal
			}
			
			if(item.moves==null){item.getMoves();}
			for (int i=0; i<item.moves.size();i++) { //visito TUTTI i figli
				Runnable worker = new Peg_game(item,item.moves.get(i),this);
				pool.execute(worker);
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(0);
		} 
		
		return null;
	}
	
	/**
	 * Perform a complete search
	 * @return the solution state if the goal has been reached
	 * null otherwise
	 */
	public Peg_game completeSearch() {
		Peg_game son = expand();
		while (/*!q.isEmpty() && */ q.size() < limitSize && son == null && !pool.isShutdown() )
			son = expand();
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