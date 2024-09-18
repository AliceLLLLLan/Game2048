/*
start position, end position, new tile
    => if 
*/

/*
window.onload = function() {
    main();
}
*/

function main() {
    let boardSize = 4;
    let board = new Board(boardSize);
    let table = createTable(boardSize);

    board.generateNewTile();
    board.generateNewTile();
    board.displayBoard(table);

    window.addEventListener("keydown", function(event) {
        let direction;
        if (event.code == "ArrowDown") {
            direction = 'down';
        } else if (event.code == "ArrowUp") {
            direction = 'up';
        } else if (event.code == "ArrowLeft") {
            direction = 'left';
        } else if (event.code == "ArrowRight") {
            direction = 'right';
        } else {
            return;
        }
        console.log("move to " + direction);
        board.moveDirection(direction);
        for (let i = 0; i < board.boardsize; i++) {
            for (let j = 0; j < board.boardsize; j++) {
                table[i][j].innerHTML = board.board[i][j] > 0 ? board.board[i][j] : '';
            }
        }
        event.preventDefault();
    }, true);
}

function createTable(size) {
    let table = [];
    let cells = document.getElementById("container");
    cells.classList = "grid-container";
    for (let r=0; r<size; r++) {
        let row_table = [];
        for (let c=0; c<size; c++) {
            let div = document.createElement('div');
            div.setAttribute("id", r+"-"+c);
            cells.appendChild(div);
            row_table.push(div);
        }
        table.push(row_table);
    }
    console.log("hello!");
    console.log(cells);
    return table;
}

function changeValue(cells, row, col, newValue) {
   cells[row][col].innerHTML = newValue;
}

class Board {
    constructor(size) {
        this.boardsize = size;
        this.boardScore = 0;
        this.initialBoard();
    }

    initialBoard() {
        // create an n*n size board
        console.log("Initialize a board with size: " + this.boardsize);
        let x = new Array(this.boardsize);
        for (let i = 0; i < this.boardsize; i++) {
            x[i] = new Array(this.boardsize);
            x[i].fill(0);
        }
        this.board = x;
    }

    generateNewTile() {
        while(true) {
            let randomRow = Math.floor(Math.random() * this.boardsize);
            let randomCol = Math.floor(Math.random() * this.boardsize);
            if (this.board[randomRow][randomCol] == 0) {
                if (Math.random() > 0.7) {
                    this.board[randomRow][randomCol] = 4;
                } else {
                    this.board[randomRow][randomCol] = 2;
                }
                console.log("generate a new tile at: (" + randomRow + ", " + randomCol + ").");
                let div = document.getElementById(randomRow+"-"+randomCol);
                div.setAttribute("style", "background:red");
                let tile = document.createElement('div');
                tile.innerHTML = this.board[randomRow][randomCol];
                div.appendChild(tile);
                break;
            }
        }
    }

    logDisplayBoard() {
        //conect to front web display
        console.log("display current score: " + this.boardScore); 
        console.log("display current board: ");
        for (let i = 0; i < this.boardsize; i++) {
            let combinedString = "|";
            for (let j = 0; j< this.boardsize; j++) {
                if (this.board[i][j] == 0) {
                    combinedString = combinedString + " |";
                } else {
                    combinedString = combinedString + this.board[i][j].toString() + "|";
                }
            }
            console.log(combinedString);
            console.log("---------------------------");
        } 
    }

    displayBoard(cells) {
        for (let i = 0; i < this.boardsize; i++) {
            for (let j = 0; j < this.boardsize; j++) {
                cells[i][j].innerHTML = this.board[i][j] > 0 ? this.board[i][j] : '';
            }
        }
    }

    recoverBoardBackground() {
        for (let i = 0; i < this.boardsize; i++) {
            for (let j = 0; j < this.boardsize; j++) {
                let div = document.getElementById(i+"-"+j);
                div.setAttribute("style", "background:#EFCB68");
            }
        }
    }

    moveDirection(direction) {
        if (direction == 'up') {
            this.moveUP();
        } else if (direction =="down") {
            this.moveDown();
        } else if (direction =="left") {
            this.moveLeft();
        } else if (direction =="right") {
            this.moveRight();
        }
        this.recoverBoardBackground();
        this.logDisplayBoard();
        this.generateNewTile();
        this.logDisplayBoard();
    }

    move(dir, transposed) {
        let board = new NormalBoardView(this.board);
        if (transposed) {
            board = new TransposedBoardView(this.board);
        }
        let addScore = 0;
        for (let i = 0; i < this.boardsize; i++) {
            let prevTile = 0;
            let j0 = 0;
            let jn = this.boardsize;
            let merged = 0;
            if (dir < 0) {
                j0 = this.boardsize -1;
                jn = -1;
                merged = this.boardsize - 1;
            }

            for (let j = j0; j != jn; j += dir) {
                let tile = board.getTile(i,j);
                if (tile == 0) {
                    continue;
                }
                //orign_pos=i,j
                if (prevTile == 0) {
                    prevTile = tile;
                } else {
                    if (prevTile == tile) {
                        let newTile = tile * 2;
                        board.setTile(i,merged, newTile);
                        addScore += newTile;
                        prevTile = 0;
                    } else {
                        board.setTile(i, merged, prevTile);
                        prevTile = tile;
                    }
                    //new_pos = new pos [i, merged]
                    merged += dir;
                }
            }
            if (prevTile != 0) {
                board.setTile(i, merged, prevTile);
                //new_pos = new pos [i, merged], origin-pos=[i,jn-1]
                merged += dir;
            }
            let delta = this.boardsize - merged;
            j0 = merged -1;
            jn = -1;
            if (dir <0) {
                j0 = merged +1;
                jn = this.boardsize;
                delta = -j0;
            }
            for (let j = j0; j != jn; j -= dir) {
                board.setTile(i, j+delta, board.getTile(i,j));
            }
            for (let j = jn+delta; j!= jn; j -= dir) {
                board.setTile(i, j, 0);
            }
        }
        this.boardScore += addScore;
    }

    moveLeft() {
        this.move(-1, false);
    }

    moveUP() {
        this.move(-1, true);
    }

    moveDown() {
        this.move(1, true);
    }

    moveRight() {
        this.move(1, false);
    }
}


class NormalBoardView {
    constructor(board) {
        this.board = board;
    }

    getTile(i, j) {
        return this.board[i][j];
    }

    setTile(i, j, v) {
        this.board[i][j] = v;
    }
}

class TransposedBoardView {
    constructor(board) {
        this.board = board;
    }

    getTile(i, j) {
        return this.board[j][i];
    }

    setTile(i, j, v) {
        this.board[j][i] = v;
    }
}