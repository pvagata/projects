public abstract class MoveScorer implements IMoveScorer {
		protected int score = 0;
		protected int leaves = 0;
		@Override 
		public int getScore() {
			return score;
			
		}

		public int getNumberLeaves() {
			return leaves;
		}
		
		// only call when game is over
		public int scoreBoard(short [] bd) {
			leaves++;
			int gamescore = 0;
			for (int idx = 0; idx < bd.length; idx++) {
	
				if (bd[idx] == Piece.WHITE) {
					gamescore++;
				} else if(bd[idx] == Piece.BLACK) {
					gamescore--;
				}
		
			}
			
			if (gamescore == 0) {
				return 0;
			} else {
				if (gamescore < 0) {
					return Integer.MIN_VALUE + (bd.length + gamescore) + 1;
				} else {
					return Integer.MAX_VALUE - (bd.length - gamescore) - 1;
				}
			}
		}
		@Override 
		public void reset() {
			leaves = 0;
		}
	}