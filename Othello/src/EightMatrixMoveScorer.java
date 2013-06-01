
public class EightMatrixMoveScorer extends MoveScorer {
		@Override 
		public void scoreMove(short[] bd) {
			score = 0;
			/*
			 64 -8 8 8 8 8 -8 64
			 -8 -8 0 0 0 0 -8 -8
			  8  0 0 0 0 0  0  8
			  8  0 0 0 0 0  0  8
			  8  0 0 0 0 0  0  8
			  8  0 0 0 0 0  0  8
			 -8 -8 0 0 0 0 -8 -8
			 64 -8 8 8 8 8 -8 64
			 */
			int [] scoringMatrix = new int [] 
					{64, -8, 8, 8, 8, 8, -8, 64,
					-8, -8, 0, 0, 0, 0, -8, -8,
					8, 0, 0, 0, 0, 0, 0, 8,
					8, 0, 0, 0, 0, 0, 0, 8,
					8, 0, 0, 0, 0, 0, 0, 8,
					8, 0, 0, 0, 0, 0, 0, 8,
					-8, -8, 0, 0, 0, 0, -8, -8,
					64, -8, 8, 8, 8, 8, -8, 64};
			for (int idx = 0; idx < bd.length; idx++) {
				if(bd[idx] == Piece.WHITE) {
					score += scoringMatrix[idx];
				} else if(bd[idx] == Piece.BLACK) {
					score -= scoringMatrix[idx];
				}
			}
		}
	}