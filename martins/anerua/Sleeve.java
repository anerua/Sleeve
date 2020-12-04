package martins.anerua;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Sleeve {
	int BOARD_ROW, BOARD_COL;
	int[][] ROW_CLUES, COL_CLUES;
	
	static float timeWC = 0, timeCC = 0, timeClone = 0, timeCopy = 0;

	public static String printBoard(String[] board) {
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
//		String row = board[focusRow];
		int targetCount = 0;
		boolean emptyTarget = false;
//		for (int i = 0; i <= focusCol; i++) {
//			if (Character.toString(row.charAt(i)).equals("1")) {
//				if (!emptyTarget) {
//					++targetCount;
//					if (targetCount == focusClue) {
//						if ((i == BOARD_COL - 1) || (!Character.toString(row.charAt(i+1)).equals("1"))) {
//							targetCount = 0;
//							++clueCount;
//							if (clueCount < targetClues.length) {
//								focusClue = targetClues[clueCount];
//							} else {
//								emptyTarget = true;
//							}
//						} else {
//							return false;
//						}
//					}
//				} else {
//					return false;
//				}
//			} else {
//				if (targetCount > 0 && targetCount != focusClue) {
//					return false;
//				}
//			}
//		}
//		if ((focusCol == BOARD_COL - 1) && (clueCount != targetClues.length)) {
//			return false;
//		}
		
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
							if (clueCount < targetClues.length) {
								focusClue = targetClues[clueCount];
							} else {
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
				if (targetCount > 0 && targetCount != focusClue) {
					return false;
				}
			}
		}
		if ((focusRow == BOARD_ROW - 1) && (clueCount != targetClues.length)) {
			return false;
		}
			
		
		return true;
	}

	private String writeCell(String row, String candidate, int focusCol, int endCol) {
		String newRow = "";
		for (int i = 0; i < row.length(); i++) {
			if (i == endCol + 1 && candidate.equals("1"))
				newRow += "X";
			else
				newRow += (i >= focusCol && i <= endCol) ? candidate : row.charAt(i);
		}
		return newRow;
	}

	public String[] DFS(String[] board, Map<Integer, Integer> rowClueFocus) {
		int focusRow = BOARD_ROW, focusCol = BOARD_COL;
		long timeA = System.nanoTime();
		for (int i = 0; i < BOARD_ROW; i++) {
			if (board[i].contains("0")) {
				focusRow = i;
				focusCol = board[i].indexOf("0");
				break;
			}
		}
		long timeB = System.nanoTime();
		timeClone += (float) (timeB - timeA) / 1000000000;
		if (focusRow == BOARD_ROW && focusCol == BOARD_COL) {
			return board; // puzzle is solved
		}
		String[] candidates = { "1", "X" };
		for (String candidate : candidates) {
			String[] tempBoard = board.clone();
			int endCol, tempFocusCol;
			long t1 = System.nanoTime();
			Map<Integer, Integer> tempRowClueFocus = new HashMap<Integer, Integer>(rowClueFocus);
			long t2 = System.nanoTime();
			timeCopy += (float) (t2 - t1) / 1000000000;
			if (candidate.equals("1") && tempRowClueFocus.get(focusRow) != -32) {
				int focusClue = ROW_CLUES[focusRow][tempRowClueFocus.get(focusRow)];
				if (BOARD_COL - focusCol < focusClue) {
					continue;
				} else {
					endCol = focusCol + (focusClue - 1);
					tempFocusCol = (endCol + 1 < BOARD_COL) ? endCol + 1 : endCol;
					int t = tempRowClueFocus.get(focusRow) + 1;
					if (t >= ROW_CLUES[focusRow].length) {
						t = -32; //row is complete
					}
					tempRowClueFocus.put(focusRow, t);
				}
			} else {
				endCol = tempFocusCol = focusCol;
			}
			
			long time1 = System.nanoTime();
			tempBoard[focusRow] = writeCell(tempBoard[focusRow], candidate, focusCol, endCol);
			long time2 = System.nanoTime();
			timeWC += (float) (time2 - time1) / 1000000000;
			
			boolean constraint = checkConstraint(tempBoard, focusRow, tempFocusCol);
			long time3 = System.nanoTime();
			timeCC += (float) (time3 - time2) / 1000000000;
			if (constraint) {
				String[] newBoard = DFS(tempBoard, tempRowClueFocus);
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
	
	public void setGame(String rawInput, Map<Integer, Integer> rowClueFocus) {
		String boardSize = rawInput.split(":")[0],
				gameClues = rawInput.split(":")[1];
		BOARD_ROW = Integer.parseInt(boardSize.split("x")[0]);
		BOARD_COL = Integer.parseInt(boardSize.split("x")[1]);
		String[] clues = gameClues.split("/");
		String[] rawColClues = Arrays.copyOfRange(clues, 0, BOARD_COL),
				rawRowClues = Arrays.copyOfRange(clues, BOARD_COL, BOARD_COL + BOARD_ROW);
		
		ROW_CLUES = new int[BOARD_ROW][];
		for (int i = 0; i < BOARD_ROW; i++) {
			String[] tempStr = rawRowClues[i].split("\\.");
			int[] tempInt = new int[tempStr.length];
			for (int j = 0; j < tempStr.length; j++) {
				tempInt[j] = Integer.parseInt(tempStr[j]);
			}
			ROW_CLUES[i] = tempInt;
			rowClueFocus.put(i, 0);
		}
		
		COL_CLUES = new int[BOARD_COL][];
		for (int i = 0; i < BOARD_COL; i++) {
			String[] tempStr = rawColClues[i].split("\\.");
			int[] tempInt = new int[tempStr.length];
			for (int j = 0; j < tempStr.length; j++) {
				tempInt[j] = Integer.parseInt(tempStr[j]);
			}
			COL_CLUES[i] = tempInt;
		}
	}
	
	
	public static void main(String[] args) {
		String game1 = "10x10:2.2/4/2/6/3.4/4.1/1.1.1.2/2.2/4.2/4.3/1/2/1.7/6.3/7.2/1.1.1/2/2.1/1.6/1.1.4";
		String game2 = "10x10:3/3/3/9/7/1.2/1/6/6.2/5.2/2.3/4.1.3/4.3/3.3/2.4/2.2/3/3/2.2/2.2";
		String game3 = "10x10:1.4.1/4/3.1/3.1.2/3.1.4/2.1/2.2/4/5.1/5/1.6/6/5.3/4.3/6.2/2/2/1/2.1/1.3.1.1";
		String game4 = "30x30:9.5.3.4/9.1.3.7/9.5.4.1/2.6.3.3/2.5.3.2/1.5.3.4/3.1.5.2.4/2.1.5.3.1.2/3.4.2.2/3.2.5/4.1.4/4.3.3.6.1/1.6.1.4.1/2.5.4.3/1.1.3.5.4.1/5.1.3.7.4/10.5.4/7.3.6.7/6.3.7.3.1/6.14.3/7.4.1.8/1.6.9/1.5.2.1.4/1.7.3/1.3.1.4.2/1.6.4.1.1.2/2.1.4.6.1/1.3.1.6/1.1.8/1.1.5/6.4.11/5.4.7/3.5.6/4.3.3.8/7.9.2/7.2.10/6.1.3.8/6.1.1.1.6/5.5.3.1.1/6.1.1.1/4.3.6/1.1.2/3.3.1.2/1.3.2.1.3.3/5.3.10.1/5.3.9.3/3.1.7.3/3.8.4/4.1.1.3.1.1.1/4.1.4.1/1.1/1.2.1.1.1.1.2/3.1.11.5/4.1.14.3/3.1.11.2.5/5.10.3.4/2.3.3.2.4.4/2.2.1.1.5.2/2.3.1.1.7.2/1.1.2.3.3.3.1";
		String game5 = "20x20:18/18/3.13/3.3.7/3.6.1/5.5.2/4.3.2/1.4.1.2/3.3.1/1.1.3/6.2/1.6/9.3/8.1.1/4.4.5/2.3.5/3.1.1.5/2.2.3/1.2.1/1.2/2.1.1.1.4/1.2.3/4.1/2.3.2/2.4.3/10.1/8.3/6.4/3.1.5/3.5/4.8/6.7/7.7/7.1.3/9.1/6.1.4/5.1.6/3.6.1/2.5.1.1.3/1.3.1.5";
		String game6 = "25x25:3.2.2/8.3.1/8.13/11.6/6.2.3/2.3.2/5.4.1.2/3.4.1/1.1.1.4.6/1.4.3.4/3.1.2.1.3/5.3/3.1.4/3.1.3/3.1.1.1.1.3/14.2/2.1.7.1.3/1.1.5.1.1.6/1.8.7/9.1.7/1.9.4/1.6.1.4/3.6.1.2/3.12.1/3.12.2/3.1.1.6.2.5/5.1.7.3/18.3/7.1.1/4.3.2.2/4.1.1.3.1/5.9.1/4.10/1.5.11/4.5.10/11.3.7/10.6.2/1.3.7/1.1.4.2/1.1.2/2.3.2/2.2.1.1.2/2.2.1/3.3.2.1/4.2.3.1/3.4.4.1/1.4.6/6.7/2.9.2/2.9.1";
		String game7 = "25x25:10.2.6/13.6/2.17/3.1.6.3/15.2/5.3.4.3/1.6.3.4/1.4.4.3/2.2.1.2/1.4.1.3/2.1.3/3.6/1.2.2.1.2.1.2/4.2.9.2/4.1.8.2/4.5.2.2/1.4.2.1/7.4.1/2.2.3.1.1/11.3/5.2.6/1.2.10/3.1.4/3.2.3/7.1.5/2.3.3/3.3.1.5.1/1.4.3/2.3.3.1/2.3.1.1.1/2.3.3.3/8.3.6/3.1.4.4.6/10.1.1.1/6.1.1.1.3.3/6.1.2.3.2/5.3.1.1/5.7/5.9/3.3.10/3.3.1.5.1.1.1/1.3.4.1/1.1.3.5/1.1.1.1.4.1/4.1.1.10/6.1.1.3.5/7.3.5/3.6.2.1/2.10/2.2.1.7";
		
		Map<Integer, Integer> rowClueFocus = new HashMap<Integer, Integer>();
		
		Sleeve sv = new Sleeve();
		sv.setGame(game1, rowClueFocus);
		
		String[] board = sv.emptyBoard(sv.BOARD_ROW, sv.BOARD_COL);
		long startTime = System.nanoTime();
		String[] solution = sv.DFS(board, rowClueFocus);
		long endTime = System.nanoTime();
		float programTime = (float) (endTime - startTime) / 1000000000;
		System.out.println("Solution: ");
		System.out.println(printBoard(solution));
		System.out.println("Program took:    " + programTime + " seconds.");
		System.out.println();
		System.out.println("Time Clone: " + (int)((timeClone/programTime)*100) + "%");
		System.out.println("Time Copy: " + (int)((timeCopy/programTime)*100) + "%");
		System.out.println("Write Cell: " + (int)((timeWC/programTime)*100) + "%");
		System.out.println("Check Constraint: " + (int)((timeCC/programTime)*100) + "%");
	}

}
