package peg;

import java.util.Vector;

import org.apache.commons.lang3.builder.HashCodeBuilder;

@SuppressWarnings("unused")
public class Peg_game_structure {
	/* Ide for easy Peg board writing */
	protected static final Board V = Board.V;
	protected static final Board P = Board.P;
	protected static final Board E = Board.E;
	
	/* Vector of possible moves */
	public Vector<int[]> moves;
	
	/* Current Peg */
	protected Board[][] Table;
	
	/* Peg size */
	protected int size;
	
	/* The number of gole in Table with or without peg */ 
	protected int V_space, P_space;
	
	/* String for represent the board */
	protected String string;
	
	/* Conversion for print */
	public char BoardtoChar(Board x){
		switch(x){
			case P: return 'P';
			case V: return 'V';
			case E: return 'E';
			default: throw new IllegalArgumentException();
		}
	}

	/* Obtain a list if possible moves on this Peg */
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
	
	/* Convert this to a readable form */
	public String toPrintString(){
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
	
	/* Compares two Peg */ 
	private boolean table_equals(Board[][] other){
		for(int i=0;i<size;i++){
	    	for(int j=0;j<size;j++){
	    		if(this.Table[i][j] != other[i][j])return false;
	    	}
	    }
		return true;
	}
	
	/* Make a move */
	protected void move(int[] mossa){
		move(mossa[0], mossa[1], mossa[2], mossa[3]);
		return;
	}
	
	/* Make a move */
	private void move(int xl, int yl, int xd, int yd){ //X_who_Live , Y_... , X_who_Die , Y_...//
		int vuoto_x, vuoto_y;
		
		if(xl==xd){if(yl<yd){vuoto_x = xl; vuoto_y = yl+2;}else { vuoto_x = xl; vuoto_y = yl-2;}}
		else{if(xl<xd){ vuoto_x = xl+2; vuoto_y = yl;}else {vuoto_x = xl-2; vuoto_y = yl;}}
		
		Table[vuoto_x][vuoto_y] = Board.P;
		Table[xl][yl] = Board.V;
		Table[xd][yd] = Board.V;
		
		this.V_space++;
		this.P_space--;
		return;
	}

	/* Rotate 90° at Dx */
	public Board[][] rotate(Board[][] table, int size){
		Board[][] ret = new Board[size][size];
		
		for(int row=0;row<size;row++){
			for(int col=0;col<size;col++){
				ret[col][size-row-1] = table[row][col];
			}
		}
		
		return ret;
	}
	
	public String rotate(String s){
		String ret = "";
		
		for(int row=0;row<size;row++){
			for(int col=0;col<size;col++){
				ret+= s.charAt(col*size + size-row-1);
			}
		}
		
		return ret;
	}
	
	/* Convert to a sequence of char */
	@Override
 	public String toString(){
		if(this.string==null){
			String ret = "";
			ret+=size;
			for(int i=0;i<size;i++)for(int j=0;j<size;j++)ret+=BoardtoChar(Table[i][j]);
			
			this.string=ret;
		}
		return this.string;
	}
	
	/* Compare two Peg */
	@Override
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof Peg_game))return false;
	    
	    Peg_game otherPeg_game = (Peg_game)other;
	    
	    return this.toString().equals(otherPeg_game.toString());
	}
	
	/* Obtain Hash Code */
	@Override
	public int hashCode(){
		 return toString().hashCode();
	}
}
