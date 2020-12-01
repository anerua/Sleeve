package martins.anerua;

public class Sleeve {
	static int BOARD_ROW = 30, BOARD_COL = 30;
//	static int[][] ROW_CLUES = {{2,1},{6},{7},{1,3},{3,6},{10},{2,2,2},{3},{10},{7}},
//				   COL_CLUES = {{2,1},{3,1},{3,1},{1,1,2},{5,2},{2,3,2},{7,2},{6,3},{9},{3,6}}; // 10x10 medium
	static int[][] ROW_CLUES = {{5,10},{1,5,11},{8,1,2,3},{2,5,12},{7,3,5},{7,3,6},{1,1,1,4,3,2},{3,5,5,2,3},{1,3,1,5,1,3},{13,3,1},
						{17,4},{23},{2,3,5,12},{2,2,1,1,9},{1,2,3,4,3},{5,2},{4,3,1,2,1},{3,1,6,1},{3,4,2},{3,1,8,1},{3,8,3},{1,1,2,12},{1,1,3,12},
						{1,2,2,10,1},{4,4,1,7},{5,3,3,7,1},{7,1,5,1,3},{3,5,2},{9,3},{11}
				},
			   COL_CLUES = {{2,3,4,1,1},{1,2,4,2},{3,3,4,3},{2,1,1,4},{3,1,1,3,5},{5,5,1,6},{6,5,2,1},{6,2,3,1},{14,1},{11,3},{9,3},
					   {3,4,1,3},{1,1,4,2},{3,2,2,1,2},{5,3,1,2},{4,3,1,6},{3,10,1,5},{10,1,5,5},{13,6,3},{1,1,1,8,8,1,3},{4,8,9,2},
					   {4,4,10,2},{2,1,1,1,4,1,7,1},{2,5,6,1,7,2},{2,4,10,9},{2,3,8,8},{2,3,1,6},{6,2,3},{3,2,1,3},{3,3,3,1,1}
			   };

	public String printBoard(String[] board) {
		if (board.length == 1) {
			return "Puzzle has no solution";
		}
		String output = ".==========================================================================================================================.\n";
		for (int i = 0; i < board.length; i++) {
			output += "| ";
			for (int j = 0; j < board[i].length(); j++) {
				String cell = Character.toString(board[i].charAt(j));
				output += (cell.equals("X")) ? " " : "\u2022"; 
				output += " | ";
			}
			output += "\n---------------------------------------------------------------------------------------------------------------------------\n";
		}
		output += "*=============================================================================================================================*";
		return output;
	}
	
	private String colString(String[] board, int focusCol) {
		String col = "";
		for (String row : board)
			col += row.charAt(focusCol);
		return col;
	}

	private boolean checkConstraint(String[] board, int focusRow, int focusCol) {
		int[] targetClues = ROW_CLUES[focusRow];
		int clueCount = 0;
		int focusClue = targetClues[clueCount];
		String row = board[focusRow];
		int targetCount = 0;
		boolean emptyTarget = false;
		for (int i = 0; i <= focusCol; i++) {
			if (Character.toString(row.charAt(i)).equals("1")) {
				if (!emptyTarget) {
					++targetCount;
					if (targetCount == focusClue) {
						if ((i == BOARD_COL - 1) || (!Character.toString(row.charAt(i+1)).equals("1"))) {
							targetCount = 0;
							++clueCount;
							try {
								focusClue = targetClues[clueCount];
							} catch (ArrayIndexOutOfBoundsException e) {
								emptyTarget = true;
							}
						} else {
							return false;
						}
					}
				} else {
					return false;
				}
			} else {
				if (targetCount > 0 & targetCount != focusClue) {
					return false;
				}
			}
		}
		
		targetClues = COL_CLUES[focusCol];
		clueCount = 0;
		focusClue = targetClues[clueCount];
		String col = colString(board, focusCol);
		targetCount = 0;
		emptyTarget = false;
		for (int j = 0; j <= focusRow; j++) {
			if (Character.toString(col.charAt(j)).equals("1")) {
				if (!emptyTarget) {
					++targetCount;
					if (targetCount == focusClue) {
						if ((j == BOARD_ROW - 1) || (!Character.toString(col.charAt(j+1)).equals("1"))) {
							targetCount = 0;
							++clueCount;
							try {
								focusClue = targetClues[clueCount];
							} catch (ArrayIndexOutOfBoundsException e) {
								emptyTarget = true;
							}
						} else {
							return false;
						}
					}
				} else {
					return false;
				}
			} else {
				if (targetCount > 0 & targetCount != focusClue) {
					return false;
				}
			}
		}
		
		return true;
	}

	private String writeCell(String row, String candidate, int focusCol) {
		String newRow = "";
		for (int i = 0; i < row.length(); i++)
			newRow += (i == focusCol) ? candidate : row.charAt(i);
		return newRow;
	}

	public String[] DFS(String[] board) {
		int focusRow = BOARD_ROW, focusCol = BOARD_COL;
		for (int i = 0; i < BOARD_ROW; i++) {
			if (board[i].contains("0")) {
				focusRow = i;
				focusCol = board[i].indexOf("0");
				break;
			}
		}
		if (focusRow == BOARD_ROW && focusCol == BOARD_COL) {
			return board; // puzzle is solved
		}
		String[] candidates = { "1", "X" };
		for (String candidate : candidates) {
			String[] tempBoard = board.clone();
			tempBoard[focusRow] = writeCell(tempBoard[focusRow], candidate, focusCol);
			if (checkConstraint(tempBoard, focusRow, focusCol)) {
				String[] newBoard = DFS(tempBoard);
				if (newBoard.length != 1) 
					return newBoard;
			}
		}
		return new String[] { "0" };
	}
	
	public String[] emptyBoard(int row, int col) {
		String[] board = new String[row];
		for (int i = 0; i < row; i++) {
			String thisRow = "";
			for (int j = 0; j < col; j++) {
				thisRow += "0";
			}
			board[i] = thisRow;
		}
		return board;
	}
	
	public static void main(String[] args) {
		Sleeve sv = new Sleeve();
		String[] board = sv.emptyBoard(BOARD_ROW, BOARD_COL);
		long startTime = System.nanoTime();
		String[] solution = sv.DFS(board);
		long endTime = System.nanoTime();
		float programTime = (float) (endTime - startTime) / 1000000000;
		System.out.println("Solution: ");
		System.out.println(sv.printBoard(solution));
		System.out.println("Program took:    " + programTime + " seconds.");
	}

}
