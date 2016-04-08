package peg;

import java.io.IOException;

import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

@SuppressWarnings("unused")
public class Main {
	public static void main(String args[]) {
		for(int i=0;i<7;i++){
			for(int j=0;j<7;j++){
				
				Engine E;
				Peg_game T = new Peg_game(i,j);
				T.eval();
				E = new Engine(T);
								
				System.out.println("========== INIZIO =======");
				System.out.println(T);
				
				long before = System.currentTimeMillis();
				try {
					T = E.completeSearch();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				long after  = System.currentTimeMillis();
				
				System.out.println("======== SOLUZIONE ======");
				
				if(T!=null)System.out.println(T);
				else System.out.println("Nessuna soluzione trovata :(");
				System.out.println(E);
				
				Duration time = new Duration(before,after);
				Period p = time.toPeriod();
				PeriodFormatter fmt = new PeriodFormatterBuilder()
						.printZeroAlways()
						.minimumPrintedDigits(1)
						.appendDays()
						.appendLiteral("D ")
						.appendHours()
						.appendLiteral("h ")
						.minimumPrintedDigits(2)
						.appendMinutes()
						.appendSeparator(":")
						.appendSecondsWithMillis()
						.toFormatter();
				
				String final_date = fmt.print(p);
				//time.
				//SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss,SSS"); //FIXME:data non corretta
				System.out.println("Tempo impiegato : " + final_date );
				//TraceBack(T);
			}
		}
	}
	
	public static void TraceBack(Peg_game g) {
		if(g==null){System.out.println( "\n" + "Traccia : "); return;}
		TraceBack(g.father);
		System.out.println(g);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return;
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