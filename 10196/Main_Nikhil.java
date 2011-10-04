import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class Main {

	Map<Character, ArrayList<Integer[]>> moves = new HashMap<Character, ArrayList<Integer[]>>();
	
	public static void main(String args[]) {
		new Main();
	}
	public void initialize() {
		ArrayList<Integer[]> king = new ArrayList<Integer[]>();
		king.add(new Integer[]{0,1});
		king.add(new Integer[]{0,-1});
		king.add(new Integer[]{1,0});
		king.add(new Integer[]{-1,0});
		king.add(new Integer[]{1,1});
		king.add(new Integer[]{1,-1});
		king.add(new Integer[]{-1,1});
		king.add(new Integer[]{-1,-1});
		moves.put('k', king);
		moves.put('K', king);
		
		ArrayList<Integer[]> rooks = new ArrayList<Integer[]>();
		for ( int i = 1; i < 8; i++ ) {
			rooks.add(new Integer[]{0, i});
			rooks.add(new Integer[]{i, 0});
			rooks.add(new Integer[]{0, -i});
			rooks.add(new Integer[]{-i, 0});
		}
		moves.put('r', rooks);
		moves.put('R', rooks);
		
		ArrayList<Integer[]> bishops = new ArrayList<Integer[]>();
		for ( int i = 1; i < 8; i++ ) {
			bishops.add(new Integer[]{i, i});
			bishops.add(new Integer[]{i, -1});
			bishops.add(new Integer[]{-i, i});
			bishops.add(new Integer[]{-i, -i});
		}	
		moves.put('b', bishops);
		moves.put('B', bishops);
		
		ArrayList<Integer[]> queens = new ArrayList<Integer[]>();
		queens.addAll(bishops);
		queens.addAll(rooks);
		moves.put('q', queens);
		moves.put('Q', queens);
		
		ArrayList<Integer[]> knights = new ArrayList<Integer[]>();
		knights.add(new Integer[]{2,1});
		knights.add(new Integer[]{2,-1});
		knights.add(new Integer[]{-2,1});
		knights.add(new Integer[]{-2,-1});
		knights.add(new Integer[]{1,2});
		knights.add(new Integer[]{1,-2});
		knights.add(new Integer[]{-1,2});
		knights.add(new Integer[]{-1,-2});
		moves.put('n', knights);
		moves.put('N', knights);
		
		ArrayList<Integer[]> whitePawns = new ArrayList<Integer[]>();
		whitePawns.add(new Integer[]{1, 1});
		whitePawns.add(new Integer[]{1, -1});
		moves.put('P', whitePawns);
		
		ArrayList<Integer[]> blackPawns = new ArrayList<Integer[]>();
		blackPawns.add(new Integer[]{-1, 1});
		blackPawns.add(new Integer[]{-1, -1});
		moves.put('p', blackPawns);
	}
	public Main() {
		initialize();
		Scanner scan = new Scanner(System.in);
		
		int count = 0;
		char[][] board = readBoard(scan);
		while ( board != null ) {
			int status = processBoard(board);
			if ( status == 0 ) {
				System.out.printf("Game #%d: no king is in check.\n", ++count);
			} else if ( status == 1 ) {
				System.out.printf("Game #%d: white king is in check.\n", ++count);
			} else if ( status == 2 ) {
				System.out.printf("Game #%d: black king is in check.\n", ++count);
			}
			
			board = readBoard(scan);
		}
	}
	
	public int processBoard(char[][] board) {
		int status = 0;
		if ( inCheck(board, 'A', 'Z') ) {
			return 1;
		}
		if ( inCheck(board, 'a', 'z') ) {
			return 2;
		}
		return status;
	}
	
	public boolean inCheckIn(char[][] board, int[] king, int rowMult, int colMult, char lowerBound, char upperBound) {
		for ( int i = 1; i < 8; i++ ) { 
			int rowPosn = king[0] + rowMult * i;
			int colPosn = king[1] + colMult * i;
			
			//If overflowed then we're not in check
			if ( rowPosn > 7 || rowPosn < 0 || colPosn > 7 || colPosn < 0 ) {
				return false;
			}
			char value = board[rowPosn][colPosn];
			
			//If the same color, it's not a threat
			if ( value >= lowerBound && value <= upperBound ) {
				return false;
			}
			
			if ( value == '.' ) {
				continue;
			}
						
			ArrayList<Integer[]> possibleMoves = moves.get(value);
			for ( Integer[] possibleMove : possibleMoves ) {
				if ( possibleMove[0] == rowMult * i && possibleMove[1] == colMult * i ) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean inCheck(char[][] board, char lowerBound, char upperBound) {		
		int[] king = findKing(board, lowerBound, upperBound);
		
		boolean inCheck = false;
		inCheck |= inCheckIn(board, king, 0, 1, lowerBound, upperBound);
		inCheck |= inCheckIn(board, king, 1, 0, lowerBound, upperBound);
		inCheck |= inCheckIn(board, king, 0, -1, lowerBound, upperBound);
		inCheck |= inCheckIn(board, king, -1, 0, lowerBound, upperBound);
		inCheck |= inCheckIn(board, king, 1, 1, lowerBound, upperBound);
		inCheck |= inCheckIn(board, king, 1, -1, lowerBound, upperBound);
		inCheck |= inCheckIn(board, king, -1, 1, lowerBound, upperBound);
		inCheck |= inCheckIn(board, king, -1, -1, lowerBound, upperBound);
		
		//Now check for horses
		ArrayList<Integer[]> knights = moves.get('n');
		for ( Integer[] knight : knights ) {
			int rowPosn = king[0] + knight[0];
			int colPosn = king[1] + knight[1];
			
			if ( rowPosn < 0 || rowPosn > 7 || colPosn < 0 || rowPosn > 7 ) {
				continue;
			}
			
			char value = board[rowPosn][colPosn];
			if ( ( value == 'n' || value == 'N' ) && ( value > upperBound || value < lowerBound ) ) {
				inCheck = true;
			}
		}
		
		return inCheck;
	}
	
	public int[] findKing(char[][] board, char lowerBound, char upperBound) {
		
		for ( int i = 0; i < board.length; i++ ) {
			for ( int j = 0; j < board[i].length; j++ ) {
				if ( board[i][j] == 'k' || board[i][j] == 'K' ) {
					if ( board[i][j] >= lowerBound && board[i][j] <= upperBound ) {
						return new int[]{i, j};
					}
				}
			}
		}
		return null;
	}
	
	public char[][] readBoard(Scanner scan) {
		char[][] board = new char[8][8];
		boolean nullBoard = true;
		
		for ( int i = 0; i < 8; i++ ) {
			String line = scan.nextLine();
			board[i] = line.toCharArray();
			
			if ( !line.equals("........") ) {
				nullBoard = false;
			}
		}
		scan.nextLine();
		
		if ( nullBoard ) {
			return null;
		}
		return board;
	}
	
	public void print(char[][] board) {
		for ( int i = 0; i < board.length; i++ ) {
			for ( int j = 0; j < board[i].length; j++ ) {
				System.out.print(board[i][j]);
			}
			System.out.println();
		}
	}
	
}