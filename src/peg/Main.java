package peg;

import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
	public static void main(String args[]) {
		for(int i=0;i<1;i++){
			for(int j=0;j<1;j++){
				
				Engine E;
				Peg_game T = new Peg_game(peg_europeo,7);
				E = new Engine(T);
				
				
				
				System.out.println("========== INIZIO =======");
				System.out.println(T);
				
				long before = System.currentTimeMillis();
				T = E.completeSearch();
				long after  = System.currentTimeMillis();
				
				System.out.println("======== SOLUZIONE ======");
				
				if(T!=null)System.out.println(T);
				else System.out.println("Nessuna soluzione trovata :(");
				System.out.println(E);
				
				Date date = new Date(after-before);
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss,SSS"); //FIXME:data non corretta
				System.out.println("Tempo impiegato: " + sdf.format(date) + "\n" + "Traccia");
				//for(int i1=0;i1<T.traceback.size();i1++){
				//	System.out.println(new Peg_game(T.traceback.get(i1),T.size));
				//}
				System.out.println(T.traceback.size());
			}
		}
	}
	
	private static final Board V = Board.V;
	private static final Board P = Board.P;
	private static final Board E = Board.E;
	
	final static Board[][] peg_europeo={{E,E,P,P,P,E,E},
										{E,P,P,P,P,P,E},
										{P,P,P,P,P,P,P},
										{P,P,P,V,P,P,P},
										{P,P,P,P,P,P,P},
										{E,P,P,P,P,P,E},
										{E,E,P,P,P,E,E}};
	
	final static Board[][] peg_per_bambini={{E,E,V,V,V,E,E},
											{E,V,V,V,V,V,E},
											{V,V,V,V,V,V,V},
											{V,V,V,V,V,V,V},
											{V,V,P,P,V,V,V},
											{E,V,V,V,P,V,E},
											{E,E,V,V,V,E,E}};
  
}