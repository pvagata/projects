import java.io.IOException;
import java.util.Scanner;

// This is the main game class	
public class Othello {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		boolean playAgain = true;
		try {
			while (playAgain) {
				System.out.println("May the odds be ever in your favor...");
				Scanner sc = new Scanner(System.in);
				Board b = getBoard(sc);
				boolean runTestChoice = runTestChoice(sc);

				if(runTestChoice) {
					runTests();
				} else {
					boolean playBots = runBotVsBot(sc);
					MoveMaker blackPlayer;
					MoveMaker whitePlayer;
					if (!playBots) {
						boolean playComputer = playAgainstComputerChoice(sc);
						short player = getPlayerChoice(sc);
						if(playComputer) {
							MoveMaker botPlayer;
							MoveMaker humanPlayer;
		
							humanPlayer = new HumanMoveMaker(sc, b, player);
							botPlayer = getBot(sc, b, Piece.otherPiece(player));
							
							if (player == Piece.BLACK) {
								blackPlayer = humanPlayer;
								whitePlayer = botPlayer;
							} else {
								blackPlayer = botPlayer;
								whitePlayer = humanPlayer;
							}
						} else {
							blackPlayer = new HumanMoveMaker(sc, b, player);
							whitePlayer = new HumanMoveMaker(sc, b, Piece.otherPiece(player));
						}
		
					} else {
						blackPlayer = getBot(sc, b, Piece.BLACK);
						whitePlayer = getBot(sc, b, Piece.WHITE);
						
					}
					repl(b, blackPlayer, whitePlayer);
					System.out.println("Again? ");
					playAgain = getYesNo(sc);
				}
				
			}

		} catch (Exception ex) {
			System.out.println("The gamemaker is currently being executed. " +
					"Please come back later when a new gamemaker has been appointed." );
			throw ex;
		}
	}
	
	private static Bot getBot(Scanner sc, Board b, short botColor) {
		System.out.println("Choices for " + Piece.toString(botColor) + ":");
		System.out.println("1) MatrixScorer \n2) SimpleScorer");
		int scorerChoice = parseInt(sc, 1);
		System.out.println("Use 1) Minimax 2) Alpha-Beta Pruning");
		int algoChoice = parseInt(sc, 1);
		int depth = getBotDepth(sc);
		IMoveScorer moveScorer;
		if(scorerChoice == 1) {
			moveScorer = new EightMatrixMoveScorer();
		} else {
			moveScorer = new SimpleMoveScorer();
		}
		return new Bot(b, botColor, depth, moveScorer, algoChoice == 1);
	}
	
	private static int parseInt (Scanner sc, int defaultValue){
		try {
			return Integer.parseInt(sc.nextLine());
		} catch (NumberFormatException ex) {
			System.out.println("\nThe gamemaker has deemed you incapable of making this choice. " +
					"Defaulting to " + defaultValue);
		}
		return defaultValue;
	}
	private static Board getBoard(Scanner sc){
		System.out.print("What size board would you like?");

		int dim = parseInt(sc, 8);
		
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
	private static void runTests() {
		BotTests bt = new BotTests();
		bt.run();
	}
	private static boolean runTestChoice(Scanner sc) {
		System.out.println("run test? (y/n)");
		return getYesNo(sc);
	}
	private static boolean runBotVsBot(Scanner sc) {
		System.out.println("Bot-on-Bot action? (y/n)");
		return getYesNo(sc);
	}
	
	private static boolean getYesNo(Scanner sc) {
		if(sc.nextLine().toLowerCase().equals("y")) {
			return true;
		}
		return false;
	}
	
	private static int getBotDepth(Scanner sc) {
		System.out.println("Depth? ");
		int value = parseInt(sc, 4);
		if (value > 0 && value < 15) {
			return value;
		}
		return getBotDepth(sc);
	}

	private static void repl(Board b, MoveMaker blackPlayer, MoveMaker whitePlayer) {

		short nextPlayer = Piece.NULL;
		short curPlayer = Piece.NULL;
		curPlayer = blackPlayer.getColor();
		
		
		boolean gameOver = false;
		while (!gameOver) {
			if (curPlayer == blackPlayer.getColor()) {
				blackPlayer.makeMove();
			} else {
				whitePlayer.makeMove();
			}

			nextPlayer = Piece.otherPiece(curPlayer);
			// swap players if other player has remaining moves
			if(b.hasRemainingMoves(nextPlayer)){
				curPlayer = nextPlayer; 
			} else {
				// check if curPlayer has remaining moves
				if(!b.hasRemainingMoves(curPlayer)) {
					// if neither player has any moves left, GAME IS OVER!
					gameOver = false;
					break;
				}
				
			}
			
			b.printBoard();
			System.out.println(blackPlayer + " took " + blackPlayer.getElapsedTime());
			System.out.println(whitePlayer + " took " + whitePlayer.getElapsedTime());

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
	

}
