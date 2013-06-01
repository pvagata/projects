public abstract class MoveScorer implements IMoveScorer {
		protected int score = 0;
		@Override 
		public int getScore() {
			return score;
			
		}

		// only call when game is over
		public int scoreBoard(short [] bd) {
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
					return Integer.MIN_VALUE + (bd.length + gamescore);
				} else {
					return Integer.MAX_VALUE - (bd.length - gamescore);
				}
			}
		}
		@Override 
		public void reset() {
			
		}
	}