
public class LibertyMoveScorer extends MoveScorer {
	@Override 
	public void scoreMove(short[] bd) {
		leaves++;
		score = 0;
		int remainingMoves = 0;
		int [] scoringMatrix = new int [] 
				{64, -16, 8, 8, 8, 8, -16, 64,
				-16, -16, -1, -1, -1, -1, -16, -16,
				8, -1, -1, -1, -1, -1, -1, 8,
				8, -1, -1, -1, -1, -1, -1, 8,
				8, -1, -1, -1, -1, -1, -1, 8,
				8, -1, -1, -1, -1, -1, -1, 8,
				-16, -16, -1, -1, -1, -1, -16, -16,
				64, -16, 8, 8, 8, 8, -16, 64};
		for (int idx = 0; idx < bd.length; idx++) {
			int s = scoringMatrix[idx];
					// 0: 1, 8, 9
					// 7: 6, 15, 14
					// 56: 57,48, 49
					// 63: 62, 54, 55
			if(s == -16) {
				int filledCornerBonus = 24;
				switch (idx){
					case 1:
					case 8:
					case 9:
						if(bd[0] != Piece.NULL){
							s = filledCornerBonus;
						}
						break;
					case 6:
					case 14:
					case 15:
						if(bd[7] != Piece.NULL){
							s = filledCornerBonus;
						}
						break;
					case 57:
					case 48:
					case 49:
						if(bd[56] != Piece.NULL){
							s = filledCornerBonus;
						}
						break;
					case 62:
					case 54:
					case 55:
						if(bd[63] != Piece.NULL){
							s = filledCornerBonus;
						}
						break;
					default:
						break;
				}
			}
			if(bd[idx] == Piece.WHITE) {
				score += s;
			} else if(bd[idx] == Piece.BLACK) {
				score -= s;
			} else {
				remainingMoves++;

			}
		}
		int occupied = 64 - remainingMoves;
		if(occupied > 48) {
			int centerBonus = 1;
			int cornerBonus = 0;
			int cornerAdjBonus = 0;
			for (int idx = 0; idx < bd.length; idx++) {
				int bonus = 0;
				switch (scoringMatrix[idx]) {
					case -1:
						bonus = centerBonus;
						break;
					case -16:
						bonus = cornerAdjBonus;
						break;
					case 64:
						bonus = cornerBonus;
						break;
					default:
						bonus = 0;
						break;
						
				}
				if(bd[idx] == Piece.WHITE) {
					score += bonus;
				} else if(bd[idx] == Piece.BLACK) {
					score -= bonus;
				} 
			
				
			}
		}
	}
}

