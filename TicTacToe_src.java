package peace;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TicTacToe_src {
    private int[][] board;
    private final int size;
    private final int winLength;
    private int currentPlayer;
    private char PlayerIcon;
    private char AIIcon;

    public TicTacToe_src(int size, int winLength, char PlayerIcon, char AIIcon) {
        this.size = size;
        this.winLength = winLength;
        this.board = new int[size][size];
        this.currentPlayer = 1;
        this.PlayerIcon = PlayerIcon;
        this.AIIcon = AIIcon;
    }

    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size && board[row][col] == 0;
    }

    private void makeMove(int row, int col) {
        if (isValidMove(row, col)) {
            board[row][col] = currentPlayer;
            currentPlayer = 3 - currentPlayer;
        } else {
            throw new IllegalArgumentException("Invalid move");
        }
    }

    private void undoMove(int row, int col) {
        board[row][col] = 0;
        currentPlayer = 3 - currentPlayer;
    }

    private boolean isWinner(int player) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (checkLine(i, j, 0, 1, player) || checkLine(i, j, 1, 0, player) ||
                        checkLine(i, j, 1, 1, player) || checkLine(i, j, 1, -1, player)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkLine(int row, int col, int dr, int dc, int player) {
        for (int i = 0; i < winLength; i++) {
            int r = row + i * dr;
            int c = col + i * dc;
            if (r < 0 || r >= size || c < 0 || c >= size || board[r][c] != player) {
                return false;
            }
        }
        return true;
    }

    private boolean isFull() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private List<int[]> getEmptyCells() {
        List<int[]> emptyCells = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == 0) {
                    emptyCells.add(new int[]{i, j});
                }
            }
        }
        return emptyCells;
    }


    private int[] alphaBeta(int depth, int alpha, int beta, boolean maximizingPlayer) {
        if (depth == 0 || isWinner(1) || isWinner(2) || isFull()) {
            return new int[]{evaluate(), -1, -1};
        }

        List<int[]> moves = getEmptyCells();
        int[] bestMove = {-1, -1};

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (int[] move : moves) {
                makeMove(move[0], move[1]);
                int eval = alphaBeta(depth - 1, alpha, beta, false)[0];
                undoMove(move[0], move[1]);
                if (eval > maxEval) {
                    maxEval = eval;
                    bestMove = move;
                }
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;
            }
            return new int[]{maxEval, bestMove[0], bestMove[1]};
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int[] move : moves) {
                makeMove(move[0], move[1]);
                int eval = alphaBeta(depth - 1, alpha, beta, true)[0];
                undoMove(move[0], move[1]);
                if (eval < minEval) {
                    minEval = eval;
                    bestMove = move;
                }
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
            return new int[]{minEval, bestMove[0], bestMove[1]};
        }
    }


    public int[] getBestMove(int depth) {
        int[] result = alphaBeta(depth, Integer.MIN_VALUE, Integer.MAX_VALUE, currentPlayer == 1);
        return new int[]{result[1], result[2]};
    }

    public void printBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(board[i][j] == 0 ? ". " : (board[i][j] == 1 ? PlayerIcon+" " : AIIcon+" "));
                
                
            }
            System.out.println();
        }
        System.out.println();
    }
    private int evaluate() {
        int score = 0;
        score += evaluateLines(1);
        score -= evaluateLines(2);
        return score;
    }

    private int evaluateLines(int player) {
        int score = 0;
        int opponent = 3 - player;

        // Check horizontal, vertical, and diagonal lines
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                score += evaluateLine(i, j, 1, 0, player, opponent);
                score += evaluateLine(i, j, 0, 1, player, opponent);
                score += evaluateLine(i, j, 1, 1, player, opponent);
                score += evaluateLine(i, j, 1, -1, player, opponent);
            }
        }

        return score;
    }

    private int evaluateLine(int row, int col, int dr, int dc, int player, int opponent) {
        int score = 0;
        int ownCount = 0;
        int emptyCount = 0;

        for (int i = 0; i < winLength; i++) {
            int r = row + i * dr;
            int c = col + i * dc;

            if (r < 0 || r >= size || c < 0 || c >= size) {
                return 0; // Line goes out of bounds
            }

            if (board[r][c] == player) {
                ownCount++;
            } else if (board[r][c] == 0) {
                emptyCount++;
            } else {
                return 0; // Opponent piece found, line is blocked
            }
        }

        if (ownCount == winLength) {
            score += 10000; // Winning move
        } else if (ownCount == winLength - 1 && emptyCount == 1) {
            score += 1000; // One move away from winning
        } else if (ownCount == winLength - 2 && emptyCount == 2) {
            score += 100; // Two moves away from winning
        } else if (ownCount == winLength - 3 && emptyCount == 3) {
            score += 10; // Three moves away from winning
        }

        return score;
    }
    public static void main(String[] args) {
    	Scanner scanner = new Scanner(System.in);
    	System.out.print("Do you want to go first? (y/n): ");
        boolean playerFirst = scanner.next().toLowerCase().equals("y");
        char PlayerIcon;
        char AIIcon;
        if (playerFirst) {
        	System.out.print("Enter Player icon: ");
            PlayerIcon = scanner.next().charAt(0);
            System.out.print("Enter AI icon: ");
            AIIcon = scanner.next().charAt(0);
        }else {
        	System.out.print("Enter Player icon: ");
            AIIcon = scanner.next().charAt(0);
            System.out.print("Enter AI icon: ");
            PlayerIcon = scanner.next().charAt(0);
        }
        
        TicTacToe_src game = new TicTacToe_src(8, 5, PlayerIcon, AIIcon);

        System.out.print("Enter AI difficulty (1-10): ");
        int difficulty = scanner.nextInt();

        int currentPlayer = playerFirst ? 1 : 2;

        while (true) {
            game.printBoard();

            if (game.isWinner(1)) {
                System.out.println("Player wins!");
                break;
            } else if (game.isWinner(2)) {
                System.out.println("AI wins!");
                break;
            } else if (game.isFull()) {
                System.out.println("It's a draw!");
                break;
            }

            if (currentPlayer == 1) {
                while (true) {
                    try {
                        System.out.print("Enter your move (row column): ");
                        int row = scanner.nextInt();
                        int col = scanner.nextInt();
                        game.makeMove(row, col);
                        break;
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid move. Try again.");
                    }
                }
            } else {
                int[] aiMove = game.getBestMove(difficulty);
                System.out.println("AI moves: " + aiMove[0] + " " + aiMove[1]);
                game.makeMove(aiMove[0], aiMove[1]);
            }

            currentPlayer = 3 - currentPlayer;
        }

        scanner.close();
    }
}
