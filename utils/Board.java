package utils;

import java.util.*;

interface BoardView {
    void setTile(int i, int j, int v);
    int getTile(int i, int j);
}

class NormalBoardView implements BoardView {
    int[][] board;

    public NormalBoardView(int[][] board) {
        this.board = board;
    }

    public void setTile(int i, int j, int v) {
        board[i][j] = v;
    }

    public int getTile(int i, int j) {
        return board[i][j];
    }
}

class TransposedBoardView implements BoardView {
    int[][] board;

    public TransposedBoardView(int[][] board) {
        this.board = board;
    }

    public void setTile(int i, int j, int v) {
        board[j][i] = v;
    }

    public int getTile(int i, int j) {
        return board[j][i];
    }
}

public class Board {
    public int[][] board;
    public int boardSize;
    public int boardSocre;

    public Board(int size) {
        boardSize = size;
        boardSocre = 0;
        initialBoard();
        generateNewTile();
        generateNewTile();
        displayBoard();
    }


    public void initialBoard() {
        // create an n*n size board
        System.out.println("Initialize a board with size: " + boardSize);
        board = new int[boardSize][boardSize];
    }

    public void generateNewTile() {
        int min = 0;
        int max = boardSize - 1;
        int a = 2;
        int b = 4;
        while (true) {
            int randomRow = (int) Math.floor(Math.random() * (max - min + 1) + min);
            int randomCol = (int) Math.floor(Math.random() * (max - min + 1) + min);
            if (board[randomRow][randomCol] == 0) {
                board[randomRow][randomCol] = new Random().nextBoolean() ? a : b;
                System.out.println("generate a new tile at: (" + randomRow + ", " + randomCol + ").");
                break;
            }
        }
    }

    public void displayBoard() {
        System.out.println("display current score: " + boardSocre);
        System.out.println("display current board:");
        for (int[] row: board) {
            for (int tile: row) {
                System.out.print((tile > 0 ? tile : " ") + " | ");
            }
            System.out.println();
            System.out.println("---------------------------");
        }
    }

    public String transferInput(String inputMovement) {
        String output;
        switch (inputMovement) {
            case "w":
            case "W":
                output = "up";
                break;
            case "s":
            case "S":
                output = "down";
                break;
            case "a":
            case "A":
                output = "left";
                break;
            case "d":
            case "D":
                output = "right";
                break;
            default:
                output = "input error: input must be W/S/A/D.";
        }
        return output;
    }

    public void moveDirection(String direction) {
        if (direction=="up") {
            moveUP();
        } else if (direction=="down") {
            moveDown();
        } else if (direction=="left") {
            moveLeft();
        } else if (direction=="right") {
            moveRight();
        }
        displayBoard();
        generateNewTile();
        displayBoard();
    }

    void move(int dir, boolean transposed) {
        BoardView board = new NormalBoardView(this.board);
        if (transposed) {
            board = new TransposedBoardView(this.board);
        }
        int addScore = 0;
        for (int i = 0; i < boardSize; i++) {
            int prevTile = 0;
            int j0 = 0;
            int jn = boardSize;
            int merged = 0;
            if (dir < 0) {
                j0 = boardSize - 1;
                jn = -1;
                merged = boardSize - 1;
            }

            for (int j = j0; j != jn; j += dir) {
                int tile = board.getTile(i, j);
                //board.setTile(i, j, 0);
                // each tile in a row, if tile is empty, continue
                if (tile == 0) {
                    continue;
                }
                if (prevTile == 0) {
                    prevTile = tile;
                } else {
                    if (prevTile == tile) {
                        //if matched and unchanged, tile*2
                        int newTile = tile * 2;
                        board.setTile(i, merged, newTile);
                        merged += dir;
                        addScore += newTile;
                        prevTile = 0;
                    } else {
                        board.setTile(i, merged, prevTile);
                        merged += dir;
                        prevTile = tile;
                    }
                }
            }
            if (prevTile != 0) {
                board.setTile(i, merged, prevTile);
                merged += dir;
            }
            // merged is the number of remaing tiles
            //System.arraycopy(board[i], 0, board[i], boardSize - merged, merged);
            int delta = boardSize - merged;
            j0 = merged - 1;
            jn = -1;
            if (dir < 0) {
                j0 = merged + 1;
                jn = boardSize;
                delta = -j0;
            }
            for (int j = j0; j != jn; j -= dir) {
                board.setTile(i, j + delta, board.getTile(i, j));
            }
            for (int j = jn + delta; j != jn; j -= dir) {
                board.setTile(i, j, 0);
            }
        }
        boardSocre += addScore;
    }

    public void moveLeft() {
        move(-1, false);
    }
    public void moveUP() {
        move(-1, true);
    }
    public void moveDown() {
       move(1, true);
    }
    public void moveRight() {
        move(1, false);
        /*
        int addScore = 0;
        for (int i = 0; i < boardSize; i++) {
            // for each row
            Stack<Tile> stackTile = new Stack<Tile>();
            List<Tile> thisRow = board.get(i);
            for (int j = 0; j < boardSize; j++) {
                // each tile in a row, if tile is empty, continue
                if (thisRow.get(j).score == 0) {
                    continue;
                }
                // if tile is not empty & stack is empty, push tile to stack
                if (stackTile.empty()) {
                    stackTile.push(thisRow.get(j));
                } else {
                    // if stack is not empty, peek the top of stack.
                    Tile tempTile = stackTile.peek();
                    if (tempTile.score == thisRow.get(j).score) {
                        // if peek.score == tile.score & peek.ifChanged = false
                        if (tempTile.ifChanged == false) {
                            // merge and replace the top of stack
                            Tile popedTile = stackTile.pop();
                            popedTile.score = thisRow.get(j).score * 2;
                            popedTile.ifChanged = true;
                            stackTile.push(popedTile);
                            addScore += thisRow.get(j).score * 2;
                        } else {
                            // if peek.score == tile.score & peek.ifChanged = true
                            // simple add to top of stack
                            stackTile.push(thisRow.get(j));
                        }
                    } else {
                        stackTile.push(thisRow.get(j));
                    }
                }
            }
            // finish scan a row, refresh this row
            for (int j = boardSize - 1; j >= 0; j--) {
                if (!stackTile.empty()) {
                    Tile temp = stackTile.pop();
                    thisRow.get(j).score = temp.score;
                    thisRow.get(j).ifChanged = false;
                } else {
                    thisRow.get(j).score = 0;
                    thisRow.get(j).ifChanged = false;
                }
            }
        }
        boardSocre += addScore;
        */
    }
    /*
     * initialBoard
     * displayBoard
     * take input
     * make move
     * gameScoreUdpate
     * gameStatusCheck
     * generateNewTile
     */
}