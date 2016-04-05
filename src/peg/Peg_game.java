package peg;
import java.util.*;

public class Peg_game extends Peg_game_structure implements Runnable{
	protected int value=999, pagodavalue=999;
	public Vector<int[]> moves;
	
	private Random generator = new Random(System.nanoTime());
	
	private Engine Eng;
	
	/* Inizializza un classica partita di Peg */
	public Peg_game(){
		Table = new Board[][]{	{E,E,P,P,P,E,E},
								{E,E,P,P,P,E,E},
								{P,P,P,P,P,P,P},
								{P,P,P,V,P,P,P},
								{P,P,P,P,P,P,P},
								{E,E,P,P,P,E,E},
								{E,E,P,P,P,E,E}
		};
		//this.Eng = Eng;
		V_space = 1;
		P_space = 32;
		size = 7;
	}
	
	/* Inizializza una partita di Peg casuale */
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
		
		//this.Eng = Eng;
	}
	
	/* Inizializza una partita di Peg con uno spazio vuoto a scelta */
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
		//this.Eng = Eng;
		
		if(i<0 || i>=size || j<0 || j>=size)throw new IllegalArgumentException();
		if(Table[i][j] == Board.P)Table[i][j] = Board.V;
	}
	
	/* Inizializza un partita di Peg personalizzata*/
	public Peg_game(Board[][] start, int size){
		this.size = size;
		this.Table = new Board[size][size];
		//this.Eng = Eng;
		
		for(int i = 0; i<size;i++){
			for(int j=0;j<size;j++){
				this.Table[i][j] = start[i][j];
				if(start[i][j]==Board.V)V_space++;
				if(start[i][j]==Board.P)P_space++;
			}
		}
	}
	
	/* Continua una partita di Peg, facendo una mossa */
	public Peg_game(Peg_game g, int[] move, Engine Eng) {
		this.V_space = g.V_space;
		this.P_space = g.P_space;
		this.size = g.size;
		this.value = g.value;
		this.pagodavalue = g.pagodavalue;
		this.Eng = Eng;
		
		Table = new Board[size][size];
		for(int i=0;i<size;i++)for(int j=0;j<size;j++)this.Table[i][j]=g.Table[i][j];
		
		this.move(move);
	}

	public int value(){
		return value;
	}
	
	public String toString(){
		if (Table == null) return "Nessuna soluzione trovata :(";
		String ret = "";
		
		for(int k=0;k<size*2;k++){ret+="-";}
		ret+="\n";
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				if(this.Table[i][j] == Board.E)ret+=" ";
				else ret+= BoardtoChar(this.Table[i][j]);
				ret+= " ";
			}
			ret += "\n";
		}
		for(int k=0;k<size*2;k++){ret+="-";}
		ret+="\n";
				
		return ret;
	}

	public Vector<int[]> getMoves(){
		if(this.moves != null)return this.moves;
		
		int vicino_x, vicino_y, lontano_x, lontano_y;
		Vector<int[]> ret = new Vector<int[]>();
		
		for( int i = 0 ; i < size ; i++ ){
			for( int j = 0 ; j < size ; j++ ){
				if(this.Table[i][j] == Board.P){
					for( int k = 0 ; k < 4 ; k++ ){
						vicino_x = lontano_x = i;
						vicino_y = lontano_y = j;
						if( k==0 ){vicino_y--;lontano_y--;lontano_y--;}	
						if( k==1 ){vicino_y++;lontano_y++;lontano_y++;}	
						if( k==2 ){vicino_x--;lontano_x--;lontano_x--;}	
						if( k==3 ){vicino_x++;lontano_x++;lontano_x++;}
						
						if( vicino_x>=0 && vicino_x<size && vicino_y>=0 && vicino_y<size && lontano_x>=0 && lontano_x<size && lontano_y>=0 && lontano_y<size){
							if(this.Table[vicino_x][vicino_y]==Board.P && this.Table[lontano_x][lontano_y]==Board.V){
									ret.add(new int[]{i,j,vicino_x,vicino_y});
							}
						}						
					}
				}
			}
		}
		
		this.moves = ret;
		return ret;
	}
	
	public void evalclassic(){
		// Se non ci sono mosse disponibili, sono in una death_end
		if(getMoves().size() == 0 && this.P_space > 1){value = 10000000; return;}
		
		// Sono in uno stato GOAL
		if(this.P_space == 1){ value=0; return;}
		
		// Uno stato è promettente se è vicino alla soluzione
		value = this.P_space;// + this.pallini_isolati;
		return;
	}

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
	
	public void heuristiceval(){//for 9*9
		if(getMoves().size() == 0 && this.P_space > 1){value = 10000000; return;}
		
		if(this.P_space == 1){ value=0; return;}
		
		int[][] Corners = {{0,3},{0,5},{3,0},{3,8},{5,0},{5,8},{8,3},{8,5}};
		int[][][] Merson_Regions = {{{1,3},{2,3}},{{1,5},{2,5}},
									{{3,1},{3,2}},{{3,6},{3,7}},
									{{3,3},{3,4},{4,3},{4,4}},
									{{5,1},{5,2}},{{5,6},{5,7}},
									{{6,3},{7,3}},{{6,5},{7,5}}};
		
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
	
	public void eval(){
		if(size==9){heuristiceval();return;}
		if(size==7 && (size*size - P_space - V_space)==12){europeanpagodafunctioneval();return;}
		if(size==7 && (size*size - P_space - V_space)==16){pagoda_matrix_eval();return;}
		
		evalclassic();return;
	}
	
	
	private int[/*6*/][/*7*/][/*7*/] pagodamatrix=    {{{0,  0, 0, 0, 0, 0,  0 },
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
	
	private int[/*7*/][/*7*/] costMatrix =  new int[][]{{ 0, 0, 4, 0, 4, 0, 0 },
														{ 0, 0, 0, 0, 0, 0, 0 }, 
														{ 4, 0, 3, 0, 3, 0, 4 },
														{ 0, 0, 0, 1, 0, 0, 0 }, 
														{ 4, 0, 3, 0, 3, 0, 4 },
														{ 0, 0, 0, 0, 0, 0, 0 }, 
														{ 0, 0, 4, 0, 4, 0, 0 }};
														
	private int[/*7*/][/*7*/] europeanpagodamatrix={{0, 0,-1, 0,-1, 0, 0 },
													{0, 0, 1, 1, 1, 0, 0 },
													{-1,1, 0, 1, 0, 1, -1},
													{0, 1, 1, 0, 1, 1, 0 },
													{-1,1, 0, 1, 0, 1, -1},
													{0, 0, 1, 1, 1, 0, 0 },
													{0, 0,-1, 0,-1, 0, 0 }};

	@Override
	public void run() {
		eval();
		if (this.value < Eng.limitValue && !Eng.q.contains(this)){Eng.q.add(this);Eng.addedNodes++;}
	}
}

