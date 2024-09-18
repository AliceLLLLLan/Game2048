import utils.Board;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Board board = new Board(8);
        // in a loop of check game status
        
        //take input 
        Scanner myObj = new Scanner(System.in);
        String inputMovement;
        String direction;
          
        
        while (true) {
            System.out.println("Enter a movement: (W-up, S-down, A-left, D-right)"); 
            inputMovement = myObj.nextLine(); 
            direction = board.transferInput(inputMovement);
            System.out.println("Entered movement is: " + direction);
            board.moveDirection(direction);
        }
        
    }
}
