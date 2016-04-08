package peg;

import org.apache.commons.lang3.builder.HashCodeBuilder;

@SuppressWarnings("unused")
public class Peg_game_structure {
	protected static final Board V = Board.V;
	protected static final Board P = Board.P;
	protected static final Board E = Board.E;
	
	protected Board[][] Table;
	protected int size;
	protected int V_space, P_space;
	
	protected String string;
	
	
	@Override
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof Peg_game))return false;
	    
	    Peg_game otherPeg_game = (Peg_game)other;
	    //if(this.size != otherPeg_game.size)return false;
	    
	    // Controllo le rotazioni
	    //for(int iter=0;iter<4;iter++){
	    //	if(table_equals(otherPeg_game.Table))return true;
	    //	this.Table=rotate(this.Table,size);
	    //}
	    return this.toString() == otherPeg_game.toString();
	}
	
	@Override
	public int hashCode(){
		 return toString(Table,size).hashCode();
	}
	
	private boolean table_equals(Board[][] other){
		for(int i=0;i<size;i++){
	    	for(int j=0;j<size;j++){
	    		if(this.Table[i][j] != other[i][j])return false;
	    	}
	    }
		return true;
	}
	
	
	protected void move(int[] mossa){
		if(mossa.length != 4) throw new IllegalArgumentException();
		
		move(mossa[0], mossa[1], mossa[2], mossa[3]);
		return;
	}
	
	private void move(int xl, int yl, int xd, int yd){ //X_who_Live , Y_... , X_who_Die , Y_...//
		int vuoto_x, vuoto_y;
		
		if(xl==xd){if(yl<yd){vuoto_x = xl; vuoto_y = yl+2;}else { vuoto_x = xl; vuoto_y = yl-2;}}
		else{if(xl<xd){ vuoto_x = xl+2; vuoto_y = yl;}else {vuoto_x = xl-2; vuoto_y = yl;}}
		
		Table[vuoto_x][vuoto_y] = Board.P;
		Table[xl][yl] = Board.V;
		Table[xd][yd] = Board.V;
		
		this.V_space++;
		this.P_space--;
	}

	public Board[][] rotate(Board[][] table, int size){
		Board[][] ret = new Board[size][size];
		
		for(int row=0;row<size;row++){
			for(int col=0;col<size;col++){
				ret[col][size-row-1] = table[row][col];
			}
		}
		
		return ret;
	}
	
	public String toString(Board Table[][], int size){
		if(this.string!=null)return this.string;
		
		String ret = "";
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				ret+=BoardtoChar(Table[i][j]);
			}
		}
		this.string=ret;
		return ret+size;
	}
	
	public byte BoardtoByte(Board x){
		switch(x){
			case P: return 0b11;
			case V: return 0b10;
			case E: return 0b00;
			default: throw new IllegalArgumentException();
		}
	}
	
	public char BoardtoChar(Board x){
		switch(x){
			case P: return 'P';
			case V: return 'V';
			case E: return 'E';
			default: throw new IllegalArgumentException();
		}
	}
}
