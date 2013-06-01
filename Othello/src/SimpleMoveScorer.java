
public class SimpleMoveScorer extends MoveScorer {
	@Override
	//only call when game is not over/making a move
	public void scoreMove(short[] bd) {
		score = 0;
		for (int idx = 0; idx < bd.length; idx++) {
			if (bd[idx] == Piece.WHITE) {
				score++;
			} else if(bd[idx] == Piece.BLACK) {
				score--;
			}
		}
	}
	
}
