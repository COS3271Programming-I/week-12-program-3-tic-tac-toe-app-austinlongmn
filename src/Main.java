import java.util.Scanner;

class TTT {

  static Scanner userinput = new Scanner(System.in);
  char[][] board = { { '.', '.', '.' }, { '.', '.', '.' }, { '.', '.', '.' } };
  int turn = 1;
  char player = 'X';

  public void printBoard() {
    int i, j;
    System.out.println("");
    for (i = 0; i <= 2; i++) {
      for (j = 0; j <= 2; j++) {
        System.out.print(board[i][j] + " ");
      }
      System.out.println("");
    }
  }

  public void move(int i, int j) {
    board[i][j] = player;
    turn++;
  }

  public void unDoMove(int i, int j) {
    board[i][j] = '.';
    turn--;
  }

  public void switchPlayers() {
    if (player == 'X') {
      player = 'O';
    } else player = 'X';
  }

  public boolean isLegal(int i, int j) {
    if (board[i][j] == '.') return true;
    else return false;
  }

  public boolean winner() {
    int i;
    boolean test = false;
    for (i = 0; i <= 2; i++) {
      if (
        (board[i][0] == board[i][1]) &&
        (board[i][1] == board[i][2]) &&
        (board[i][0] != '.')
      ) {
        test = true;
      }
      if (
        (board[0][i] == board[1][i]) &&
        (board[1][i] == board[2][i]) &&
        (board[0][i] != '.')
      ) {
        test = true;
      }
    }
    if (
      (board[0][0] == board[1][1]) &&
      (board[1][1] == board[2][2]) &&
      (board[0][0] != '.')
    ) {
      test = true;
    }

    if (
      (board[2][0] == board[1][1]) &&
      (board[1][1] == board[0][2]) &&
      (board[2][0] != '.')
    ) {
      test = true;
    }
    return test;
  }

  public void human() {
    int i, j;

    boolean test = false; //have I found a place to go
    while (test == false) {
      System.out.println(
        "\nEnter Coordinates Where To Go Separated By A Space..."
      );
      i = userinput.nextInt();
      j = userinput.nextInt();
      userinput.nextLine();
      if (isLegal(i - 1, j - 1) == true) {
        test = true;
        move(i - 1, j - 1);
      }
    }
  }

  // Returns an array of moves that the AI must perform
  // Each move has three integers: the first is the priority (higher is more
  // critical). The second is the i value and the third is the j value. If the
  // priority is zero, then there is no mandatory move for that location.
  int[][] mandatoryMoves() {
    int[][] result = new int[3 * 3 * 4][3];

    int moveCounter = 0;
    // Iterate over all the possible places in the board
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        // If the place is taken, move on.
        if (board[i][j] != '.') {
          continue;
        } else {
          // Otherwise, check to see all the possible winning combinations we could make from there.
          int[][][] winningCombinations = new int[4][3][2];
          int numberOfWinningCombinations = TTT.winningCombinations(
            i,
            j,
            winningCombinations
          );
          // System.out.format(
          //   "Found %d combinations for i: %d j: %d:\n",
          //   numberOfWinningCombinations,
          //   i,
          //   j
          // );

          int counter = 0;
          for (int[][] combination : winningCombinations) {
            if (counter >= numberOfWinningCombinations) {
              break;
            }

            // System.out.format("Found a combination for i: %d j: %d:\n", i, j);
            // for (int[] point : combination) {
            //   System.out.println(point[0] + " " + point[1]);
            // }

            // Set priority and value of point
            int priority = getPriority(combination);
            result[moveCounter][0] = priority;
            int[] move = getPossibleMove(combination);
            result[moveCounter][1] = move[0];
            result[moveCounter][2] = move[1];

            // System.out.format(
            //   "Found a possible move with priority %d, i: %d, j: %d\n",
            //   priority,
            //   move[0],
            //   move[1]
            // );

            counter++;
            moveCounter++;
          }
        }
      }
    }
    return result;
  }

  // Looks through a list of moves and chooses which one is most important to make.
  // If there are no important moves, it returns { -1, -1 }.
  int[] getMandatoryMove() {
    int[] priority1Move = { -1, -1 };
    int[][] mandatoryMoves = mandatoryMoves();
    // System.out.println("Length: " + mandatoryMoves.length);
    for (int[] possibleMove : mandatoryMoves) {
      int priority = possibleMove[0];
      // System.out.format("Priority: %d\n", priority);
      if (priority == 2) {
        return new int[] { possibleMove[1], possibleMove[2] };
      } else if (priority == 1) {
        priority1Move = new int[] { possibleMove[1], possibleMove[2] };
      }
    }

    return priority1Move;
  }

  // From a line across the board, returns at which point a move can be made.
  int[] getPossibleMove(int[][] combination) {
    for (int[] point : combination) {
      if (isLegal(point[0], point[1])) {
        return point;
      }
    }

    return new int[] { -1, -1 };
  }

  // Returns an array of point combinations that could be used to win the game
  static int winningCombinations(int i, int j, int[][][] result) {
    int numberOfCombinations = 0;
    // Winning combination on the X plane
    for (int k = 0; k < 3; k++) {
      result[numberOfCombinations][k][0] = i;
      result[numberOfCombinations][k][1] = k;
    }
    numberOfCombinations++;

    // Winning combination on the Y plane
    for (int k = 0; k < 3; k++) {
      result[numberOfCombinations][k][0] = k;
      result[numberOfCombinations][k][1] = j;
    }
    numberOfCombinations++;

    // Winning combination on the top-left to bottom-right diagonal
    if (i == j) {
      for (int k = 0; k < 3; k++) {
        result[numberOfCombinations][k][0] = k;
        result[numberOfCombinations][k][1] = k;
      }
      numberOfCombinations++;
    }

    // Winning combination on the bottom-left to top-right diagonal
    if (j == 2 - i) {
      for (int k = 0; k < 3; k++) {
        result[numberOfCombinations][k][0] = k;
        result[numberOfCombinations][k][1] = 2 - k;
      }
      numberOfCombinations++;
    }

    return numberOfCombinations;
  }

  char getOpponent() {
    return player == 'X' ? 'O' : 'X';
  }

  // Returns how important it is to make a move on this line
  // 0 means not important at all
  // 1 means not moving there would allow the opponent to win
  // 2 means moving there would win the game
  int getPriority(int[][] combination) {
    int opponentCount = 0;
    int currentPlayerCount = 0;
    for (int[] point : combination) {
      if (board[point[0]][point[1]] == player) {
        currentPlayerCount++;
      } else if (board[point[0]][point[1]] == getOpponent()) {
        opponentCount++;
      }
    }

    if (currentPlayerCount == 2) {
      return 2;
    } else if (opponentCount == 2) {
      return 1;
    } else {
      return 0;
    }
  }

  public void ai() {
    Integer i, j;
    //random
    boolean test = false; //have I found a place to go

    int[] mandatoryMove = getMandatoryMove();
    // System.out.format(
    //   "Mandatory move: i: %d, j: %d\n",
    //   mandatoryMove[0],
    //   mandatoryMove[1]
    // );
    if (mandatoryMove[0] != -1) {
      move(mandatoryMove[0], mandatoryMove[1]);
      test = true;
    }

    // The strategy I am using prioritizes corners over the middle
    int[][] goodMoves = { { 0, 0 }, { 2, 0 }, { 2, 2 }, { 0, 2 } };

    // However, if the user chooses one of the corners first, the middle is necessary.
    if (test == false) {
      if (turn == 2) {
        for (int[] move : goodMoves) {
          if (board[move[0]][move[1]] == getOpponent() && isLegal(1, 1)) {
            this.move(1, 1);
            test = true;
            break;
          }
        }
      }
    }

    // If we can go in one of the corners, do so.
    if (test == false) {
      for (int[] move : goodMoves) {
        if (isLegal(move[0], move[1])) {
          this.move(move[0], move[1]);

          test = true;
          break;
        }
      }
    }

    // Fallback in case my logic is faulty
    while (test == false) {
      i = (int) (Math.random() * 3.0);
      j = (int) (Math.random() * 3.0);
      if (isLegal(i, j) == true) {
        test = true;
        move(i, j);
      }
    }
    System.out.println("AI is moving ... ");
  }
}

public class Main {

  static Scanner userinput = new Scanner(System.in);

  public static void main(String[] args) {
    TTT game = new TTT();
    game.printBoard();
    for (int i = 1; i <= 5; i++) {
      game.human();
      game.printBoard();
      if ((game.winner() == true) || (i == 5)) {
        break;
      }
      game.switchPlayers();
      game.ai();
      game.printBoard();
      if ((game.winner() == true) || (i == 5)) {
        break;
      }
      game.switchPlayers();
    }

    if (game.winner() == true) {
      System.out.println("\nThe winner is " + game.player);
    } else {
      System.out.println("\nCat Game.");
    }
  } //end main line
} //end class
