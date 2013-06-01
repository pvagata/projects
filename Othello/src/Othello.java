import java.io.IOException;
import java.util.Scanner;


public class Othello {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		try {

			System.out.println("May the odds be ever in your favor...");
			Scanner sc = new Scanner(System.in);
			Board b = getBoard(sc);
			boolean playComputer = playAgainstComputerChoice(sc);
			short player = getPlayerChoice(sc);
			repl(sc, b, playComputer, player);
		} catch (Exception ex) {
			System.out.println("The gamemaker is currently being executed. " +
					"Please come back later when a new gamemaker has been appointed." );
			throw ex;
		}
	}
	private static Board getBoard(Scanner sc){
		System.out.print("What size board would you like?");

		int dim = 8;
		try {
			dim = Integer.parseInt(sc.nextLine());
		} catch (NumberFormatException ex) {
			System.out.println("\nThe gamemaker has deemed you incapable of making this choice. " +
					"Defaulting to " + dim);
		}
		Board b = new Board(dim);
		b.printBoard();
		return b;
	}
	private static boolean playAgainstComputerChoice(Scanner sc) {
		System.out.println("2 Player? (y/n)");
		if(sc.nextLine().toLowerCase().equals("y")) {
			return false;
		}
		return true;
	}
	
	private static int getBotDepth(Scanner sc) {
		
		System.out.println("Depth? ");
		int value = sc.nextInt();
		if (value > 0 && value < 15) {
			return value;
		}
		return getBotDepth(sc);
		
	}
	private static void repl(Scanner sc, Board b, boolean playComputer, short player) throws IOException {

		short otherPlayer = Piece.NULL;
		short curPlayer = Piece.NULL;
		
		if (player == Piece.BLACK) {
			otherPlayer = Piece.WHITE;
			curPlayer = player;
		} else {
			otherPlayer = Piece.BLACK;
			curPlayer = otherPlayer;
		}
		
		Bot bot = null;
		if(playComputer) {
			
			bot = new Bot(b, otherPlayer, getBotDepth(sc));
		}
		boolean gameOver = false;
		while (!gameOver) {
			if (playComputer && curPlayer == bot.player) {
				bot.makeMove();
			} else {
				playerMove(sc, b, curPlayer);
			}
			
			otherPlayer = Piece.otherPiece(curPlayer);
			// swap players if other player has remaining moves
			if(b.hasRemainingMoves(otherPlayer)){
				curPlayer = otherPlayer; 
			} else {
				// check if curPlayer has remaining moves
				if(!b.hasRemainingMoves(curPlayer)) {
					// if neither player has any moves left, GAME IS OVER!
					gameOver = false;
					break;
				}
				
			}
			
			b.printBoard();
		}
		short winner = b.getWinner();
		if(winner == Piece.NULL) {
			
		} else {
			System.out.println(String.format("we have a victor(%s)! the loser(%s) will be executed in a slow and painful manner. ", Piece.toString(winner), Piece.toString(Piece.otherPiece(winner))));
		}
		b.printBoard();
	}
	
	private static short getPlayerChoice(Scanner sc){
		short player = Piece.NULL;
		while (player == Piece.NULL) {
		System.out.print("Would you like to be black or white (b/w)?");
			String playerChoice = sc.nextLine();
			if(playerChoice.toLowerCase().equals("b") || playerChoice.toLowerCase().equals("black")) {
				player = Piece.BLACK;
			} else if(playerChoice.toLowerCase().equals("w") || playerChoice.toLowerCase().equals("white")) {
				player = Piece.WHITE;
			} else {
				System.out.println("\n You cannot be " + playerChoice + ".");
			}
		}
		return player;
	}
	
	private static void playerMove(Scanner sc, Board b, short player) {
		boolean validMove = false;
		System.out.println("Talking to you, " + Piece.toString(player));
		while (!validMove) {
			System.out.print("row? ");
			int row = sc.nextInt();
			System.out.print("col? ");
			int col = sc.nextInt();
			validMove = b.makeMove(row, col, player);
			if (!validMove) {
				System.out.println("(" + row + ", " + col + ") is an invalid move." +
						" A robot is balking at your clowniness.");
			}
		}
	}
}
