package peg;
import java.util.*;

public class Peg_game extends Peg_game_structure implements Runnable{
	/* Value for state analysis */
	protected int value=999, pagodavalue=999;
	
	/* Vector of rotations */
	@SuppressWarnings("unused")/* Not used. Rates of excessive rotation */
	private String[] rotation = new String[4];
		
	/* Engine link */
	private Engine Eng;
	
	/* Father link */
	public Peg_game father;
	
	/* Random generator */
	private Random generator = new Random(System.nanoTime());
	
	/* Start a classic English Peg */
	public Peg_game(){
		Table = new Board[][]{	{E,E,P,P,P,E,E},
								{E,E,P,P,P,E,E},
								{P,P,P,P,P,P,P},
								{P,P,P,V,P,P,P},
								{P,P,P,P,P,P,P},
								{E,E,P,P,P,E,E},
								{E,E,P,P,P,E,E}
		};
		V_space = 1;
		P_space = 32;
		size = 7;
		this.father=null;
	}
	
	/* Start an English Peg with a random V space */
	public Peg_game(char not_importat){
		Table = new Board[][]{	{E,E,P,P,P,E,E},
								{E,E,P,P,P,E,E},
								{P,P,P,P,P,P,P},
								{P,P,P,P,P,P,P},
								{P,P,P,P,P,P,P},
								{E,E,P,P,P,E,E},
								{E,E,P,P,P,E,E}
		};
		
		int x=0,y=0;
		while(Table[x][y] == Board.E){
			x = Math.abs(generator.nextInt()%7);
			y = Math.abs(generator.nextInt()%7);
		}
		Table[x][y] = Board.V;
		
		V_space = 1;
		P_space = 32;
		size = 7;
		
		this.father=null;
	}
	
	/* Start an English Peg with a chosen V space */
	public Peg_game(int i, int j){
		Table = new Board[][]{	{E,E,P,P,P,E,E},
								{E,E,P,P,P,E,E},
								{P,P,P,P,P,P,P},
								{P,P,P,P,P,P,P},
								{P,P,P,P,P,P,P},
								{E,E,P,P,P,E,E},
								{E,E,P,P,P,E,E}
		};
		
		V_space = 1;
		P_space = 32;
		size = 7;
				
		if(i<0 || i>=size || j<0 || j>=size)throw new IllegalArgumentException();
		if(Table[i][j] == Board.P)Table[i][j] = Board.V;
		this.father=null;
	}
	
	/* Start a custom Peg */
	public Peg_game(Board[][] start, int size){
		this.size  = size;
		this.Table = new Board[size][size];
		this.father= null;
		
		for(int i = 0; i<size;i++){
			for(int j=0;j<size;j++){
				this.Table[i][j] = start[i][j];
				if(start[i][j]==Board.V)V_space++;
				if(start[i][j]==Board.P)P_space++;
			}
		}
	}
	
	/* Continue a Peg, doing a move */
	public Peg_game(Peg_game g, int[] move, Engine Eng, Peg_game father) {
		this.V_space = g.V_space;
		this.P_space = g.P_space;
		this.size = g.size;
		this.value = g.value;
		this.pagodavalue = g.pagodavalue;
		this.Eng = Eng;
		this.father = father;
		
		Table = new Board[size][size];
		for(int i=0;i<size;i++)for(int j=0;j<size;j++)this.Table[i][j]=g.Table[i][j];
		
		this.move(move);
	}

	/* Get value of this state */
	public int value(){return value;}
	
	/* Evaluates this Peg counting P */
	public void evalclassic(){
		// Se non ci sono mosse disponibili, sono in una death_end
		if(getMoves().size() == 0 && this.P_space > 1){value = 10000000; return;}
		
		// Sono in uno stato GOAL
		if(this.P_space == 1){ value=0; return;}
		
		// Uno stato è promettente se è vicino alla soluzione
		value = this.P_space;
		
		return;
	}

	/* Evaluates this Peg using costMatrix */
	public void matrixeval() {
		if(getMoves().size() == 0 && this.P_space > 1){value = 10000000; return;}
		
		if(this.P_space == 1){ value=0; return;}
		
		value =0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (Table[i][j] == Board.P) {
					value += costMatrix[i][j];
					
				}
			}
		}
		value++;
		return;
	}
	
	/* Evaluates this Peg using Pagoda function's property */
	public void pagodafunctioneval(){
		if(getMoves().size() == 0 && this.P_space > 1){value = 10000000; return;}
		
		if(this.P_space == 1){ value=0; return;}
	
		int previous_value = value;
		int[] value_vector={1,1,1,1,1,1};
		
		for(int pmi = 0; pmi<6;pmi++){ //PagodaMatrixIndex
			for(int i=0;i<size;i++){
				for(int j=0;j<size;j++){
					if(Table[i][j]==Board.P)value_vector[pmi]+=pagodamatrix[pmi][i][j];	
				}
			}
		}
		
		int max=1;
		for(int i=0;i<6;i++){
			if(value_vector[i]>previous_value){value=10000000;return;}
			if(value_vector[i]>max){max=value_vector[i];}
		}
		value=max;
		return;
	}
	
	/* Evaluates this Peg, using costMatrix for obtain value, and Pagoda function's property for detect Dead-end road */
	public void pagoda_matrix_eval(){
		if(getMoves().size() == 0 && this.P_space > 1){value = 10000000; return;}
		
		if(this.P_space == 1){ value=0; return;}
	
		int previous_value = pagodavalue;
		int[] value_vector={1,1,1,1,1,1};
		
		for(int pmi = 0; pmi<6;pmi++){ //PagodaMatrixIndex
			for(int i=0;i<size;i++){
				for(int j=0;j<size;j++){
					if(Table[i][j]==Board.P)value_vector[pmi]+=pagodamatrix[pmi][i][j];	
				}
			}
		}
		
		int max=1;
		for(int i=0;i<6;i++){
			if(value_vector[i]>previous_value){value=10000000;return;}
			if(value_vector[i]>max){max=value_vector[i];}
		}
		pagodavalue=max;

		value =0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (Table[i][j] == Board.P) {
					value += costMatrix[i][j];
					
				}
			}
		}
		value++;
		
		return;
	}
	
	/* Evaluates this European Peg using Pagoda function's property */
	public void europeanpagodafunctioneval(){
		if(getMoves().size() == 0 && this.P_space > 1){value = 10000000; return;}
		
		if(this.P_space == 1){ value=0; return;}
	
		int previous_value = value;
		
		value=0;
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				if(Table[i][j]==Board.P)value+=europeanpagodamatrix[i][j];	
			}
		}
		
		value = Math.abs(value);
		value++;
		if(value>previous_value){value=10000000;return;}
			
		return;
	}
	
	/* Evaluates 9*9 Peg using an Heuristic */
	public void heuristiceval(){
		if(getMoves().size() == 0 && this.P_space > 1){value = 10000000; return;}
		
		if(this.P_space == 1){ value=0; return;}
		
		value=1;
		
		for(int i=0;i<8;i++){ if(Table[Corners[i][0]][Corners[i][1]] == Board.P)value++;}
		
		boolean full;
		for(int i=0;i<9;i++){
			full=true;
			for (int j=0; j<Merson_Regions[i].length;j++){
				if(Table[Merson_Regions[i][j][0]][Merson_Regions[i][j][1]] == Board.V)full=false;
			}
			if(full)value++;
		}
		
		return;
	}
	
	/* Eval switcher */
	public void eval(){
		getMoves();
		if(size==9){heuristiceval();return;}
		if(size==7 && (size*size - P_space - V_space)==12){europeanpagodafunctioneval();return;}
		if(size==7 && (size*size - P_space - V_space)==16){pagoda_matrix_eval();return;}
		
		evalclassic();return;
	}
	
	
	private static final int[/*6*/][/*7*/][/*7*/] pagodamatrix= 
													{{{0,  0, 0, 0, 0, 0,  0 },
													  { 0,  0, 0, 1, 0, 0,  0 }, 
													  { -1, 1, 0, 1, 0, 1, -1 },
													  { 0,  0, 0, 0, 0, 0,  0 }, 
													  { -1, 1, 0, 1, 0, 1, -1 },
													  { 0,  0, 0, 1, 0, 0,  0 }, 
													  { 0,  0, 0, 0, 0, 0,  0 }},
															
													  {{ 0, 0, 1, 0, 1, 0, 0 },
													  { 0, 0, 0, 0, 0, 0, 0 }, 
													  { 1, 0, 1, 0, 1, 0, 1 },
													  { 0, 0, 0, 0, 0, 0, 0 }, 
													  { 1, 0, 1, 0, 1, 0, 1 },
													  { 0, 0, 0, 0, 0, 0, 0 }, 
													  { 0, 0, 1, 0, 1, 0, 0 }},
													  
						 							  {{ 0, 0, 0, 1, 0, 0, 0 },
													  { 0, 0, 0, 0, 0, 0, 0 }, 
													  { 0, 1, 0, 1, 0, 1, 0 },
													  { 0, 0, 0, 0, 0, 0, 0 }, 
													  { 0, 1, 0, 1, 0, 1, 0 },
													  { 0, 0, 0, 0, 0, 0, 0 }, 
													  { 0, 0, 0, 1, 0, 0, 0 }}, 
						 							  
						 							  {{ 0, 0, -1, 0, -1, 0, 0 },
													  { 0, 0,  1, 0, 1, 0, 0 }, 
													  { 0, 0, 0, 0, 0, 0, 0 },
													  { 0, 1, 1, 0, 1, 1, 0 }, 
													  { 0, 0, 0, 0, 0, 0, 0 },
													  { 0, 0, 1, 0, 1, 0, 0 }, 
													  { 0, 0, -1, 0, -1, 0, 0 }}, 
						 							  
						 							  {{ 0, 0, 0, 0, 0, 0, 0 },
													  { 0, 0, 0, 1, 0, 0, 0 }, 
													  { 0, 0, 0, 0, 0, 0, 0 },
													  { 0, 1, 0, 1, 0, 1, 0 }, 
													  { 0, 0, 0, 0, 0, 0, 0 },
													  { 0, 0, 0, 1, 0, 0, 0 }, 
													  { 0, 0, 0, 0, 0, 0, 0 }},
						 							  
													  {{ 0,  0, 0, 0, 0, 0,  0 },
													  { 0,  0, 1, 0, 1, 0,  0 }, 
													  { 0,  0, 0, 0, 0, 0,  0 },
													  { 1,  0, 1, 0, 1, 0,  1 }, 
													  { 0,  0, 0, 0, 0, 0,  0 },
													  { 0,  0, 1, 0, 1, 0,  0 }, 
													  { 0,  0, 0, 0, 0, 0,  0 }}};
	
	private static final int[/*7*/][/*7*/] costMatrix = 
														{{ 0, 0, 4, 0, 4, 0, 0 },
														{ 0, 0, 0, 0, 0, 0, 0 }, 
														{ 4, 0, 3, 0, 3, 0, 4 },
														{ 0, 0, 0, 1, 0, 0, 0 }, 
														{ 4, 0, 3, 0, 3, 0, 4 },
														{ 0, 0, 0, 0, 0, 0, 0 }, 
														{ 0, 0, 4, 0, 4, 0, 0 }};
														
	private static final int[/*7*/][/*7*/] europeanpagodamatrix = 
													{{0, 0,-1, 0,-1, 0, 0 },
													{0, 0, 1, 1, 1, 0, 0 },
													{-1,1, 0, 1, 0, 1, -1},
													{0, 1, 1, 0, 1, 1, 0 },
													{-1,1, 0, 1, 0, 1, -1},
													{0, 0, 1, 1, 1, 0, 0 },
													{0, 0,-1, 0,-1, 0, 0 }};

	private static final int[][] Corners = 
								{{0,3},{0,5},{3,0},{3,8},{5,0},{5,8},{8,3},{8,5}};
	
	private static final int[][][] Merson_Regions = 
								{{{1,3},{2,3}},{{1,5},{2,5}},
								{{3,1},{3,2}},{{3,6},{3,7}},
								{{3,3},{3,4},{4,3},{4,4}},
								{{5,1},{5,2}},{{5,6},{5,7}},
								{{6,3},{7,3}},{{6,5},{7,5}}};
	
	@Override
	public void run() {
		eval();
		
		//rotation[0]=this.toString();
		//rotation[1]=rotate(rotation[0]);
		//rotation[2]=rotate(rotation[1]);
		//rotation[3]=rotate(rotation[2]);
		
		if (this.value < Eng.limitValue && !Eng.q2.contains(this.toString())){//Controlla in automatico se non è già presente
			Eng.q.add(this);
			//for(int i=0;i<4;i++){Eng.q2.add(rotation[i]);}
			Eng.q2.add(this.toString());
		} 
		
		rotation = null;
		Eng.activethread.getAndDecrement();
		return;
	}
}

