import java.util.Iterator;


public class BotTests {
	final static int depth = 6;
	public void run() {
		boolean passed = true;

		passed &= testIterable();
		System.out.println("testIterable: " + passed);
		
		passed &= test1();
		System.out.println("test1: " + passed);

		passed &= test2(20);
		System.out.println("test2: " + passed);
		


	}
	public boolean testIterable() {
		AlphaBetaSearchSortAlgorithm algo = new AlphaBetaSearchSortAlgorithm(4, 16, 8, 3, 3);
		AlphaBetaSearchSortAlgorithm.AlphaBetaFullIterable it = algo.getFullIterable();
		
		Iterator<AlphaBetaSearchSortAlgorithm.AlphaBetaSortMove> iter = it.iterator();
		for (int i=0; i < 4; i++) {
			for (int j=0; j < 4; j++) {
				if(!iter.hasNext()){
					return false;
				}
				AlphaBetaSearchSortAlgorithm.AlphaBetaSortMove m = iter.next();
				if (m.row != i || m.col != j) {
					return false;
				}
			}
		}
		return true;
	}
	public boolean test1() {
		Board minimaxBoard = new Board(8);
		Board alphabetaBoard = new Board(8);
		Bot minimaxBot = getBot(true, Piece.BLACK, minimaxBoard);
		Bot alphabetaBot = getBot(false, Piece.BLACK, alphabetaBoard);
		minimaxBot.makeMove(0);
		alphabetaBot.makeMove(0);
		boolean sameMoveMade = boardsEqual(minimaxBoard.board, alphabetaBoard.board);
		return sameMoveMade &&
				alphabetaBot.scorer.scoreBoard(alphabetaBoard.board) == 
				minimaxBot.scorer.scoreBoard(minimaxBoard.board);
	}
	
	public boolean test2(int numMoves){
		boolean passed = true;
		Board minimaxBoard = new Board(8);
		Board alphabetaBoard = new Board(8);
		Bot minimaxBotBlack = getBot(true, Piece.BLACK, minimaxBoard);
		Bot alphabetaBotBlack = getBot(false, Piece.BLACK, alphabetaBoard);
		Bot minimaxBotWhite = getBot(true, Piece.WHITE, minimaxBoard);
		Bot minimaxBotWhite2 = getBot(true, Piece.WHITE, alphabetaBoard);
		short prev = Piece.BLACK;
		short cur = Piece.BLACK;
		for (int i = 0; i < numMoves; i++) {
			if(cur == Piece.BLACK) {
				minimaxBotBlack.makeMove(i);
				alphabetaBotBlack.makeMove(i);
				prev = cur;
			} else {
				minimaxBotWhite.makeMove(i);
				minimaxBotWhite2.makeMove(i);
			}
			cur = Piece.otherPiece(cur);

			passed &= boardsEqual(minimaxBoard.board, alphabetaBoard.board);
			passed &= alphabetaBotBlack.scorer.scoreBoard(alphabetaBoard.board) == 
					minimaxBotBlack.scorer.scoreBoard(minimaxBoard.board);
			if (!passed) {
				return passed;
			}
		}
		return passed;
				
	}
	
	private boolean boardsEqual(short [] a, short [] b) {
		for (int i = 0; i < a.length; i++) {
			if (a[i] != b[i]) return false;
		}
		return true;
	}
	private Bot getBot(boolean minimax, short botColor, Board b) {
		return new Bot(b, botColor, depth, new EightMatrixMoveScorer(), minimax, false, false);

	}
}
