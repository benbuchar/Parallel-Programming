import java.util.ArrayList;

/**
 * Created by lightray on 12/10/16.
 */
public class State {
    ArrayList<Box> boxes;
    ArrayList<MyLine> allLines;
    ArrayList<MyLine> movesLeft;
    ArrayList<MyLine> edgeLines;
    Player whoseTurn;
    int numberOfMovesLeft;
    int p1Score;
    int p2Score;


    public State(ArrayList<Box> boxes, ArrayList<MyLine> allLines){
        this.boxes = boxes;
        this.allLines = allLines;
        movesLeft = new ArrayList<>();
        whoseTurn = Player.P1;
        numberOfMovesLeft = allLines.size();
        updateNumberOfMovesLeft();
        p1Score = 0;
        p2Score = 0;
    }

    public State(State state){
        this.boxes = new ArrayList<>();
        this.edgeLines = new ArrayList<>();
        this.allLines = new ArrayList<>();
        this.movesLeft = new ArrayList<>();
        for(Box b : state.boxes){
            MyLine newLeft = new MyLine(b.leftLine.dot1, b.leftLine.dot2);
            newLeft.state = b.leftLine.state;
            MyLine newRight = new MyLine(b.rightLine.dot1, b.rightLine.dot2);
            newRight.state = b.rightLine.state;
            MyLine newTop = new MyLine(b.topLine.dot1, b.topLine.dot2);
            newTop.state = b.topLine.state;
            MyLine newBottom = new MyLine(b.bottomLine.dot1, b.bottomLine.dot2);
            newBottom.state = b.bottomLine.state;
            allLines.add(newLeft);
            allLines.add(newRight);
            allLines.add(newTop);
            allLines.add(newBottom);
            Box newBox = new Box(newLeft, newRight, newTop, newBottom);
            newBox.state = b.state;
            this.boxes.add(newBox);
        }
        if(state.whoseTurn == Player.P1){
            whoseTurn = Player.P1;
        } else {
            whoseTurn = Player.P2;
        }
        //this.updateNumberOfMovesLeft();
        for(MyLine line : allLines){
            for(MyLine edgeLine : state.edgeLines){
                if(edgeLine.dot1 == line.dot1 && edgeLine.dot2 == line.dot2){
                    this.edgeLines.add(line);
                }
            }
        }

        for(Box b : boxes){
            for(Box otherBox : boxes){
                for(MyLine bLine : b.lines){
                    for(MyLine otherBoxLine : otherBox.lines){
                        if(bLine.dot1 == otherBoxLine.dot1 && bLine.dot2 == otherBoxLine.dot2){
                            bLine = otherBoxLine;
                        }
                    }
                }
            }
        }
        p1Score = 0;
        p2Score = 0;




        this.updateScore();

    }

    public void endTurn(){
        if(whoseTurn == Player.P1){
            whoseTurn = Player.P2;
        } else {
            whoseTurn = Player.P1;
        }
    }

    public void updateNumberOfMovesLeft(){
        movesLeft.clear();
        int numberOfOpenLines = 0;
        for(MyLine lines : allLines){
           // if(!movesLeft.contains(lines)) {
                if (lines.state == MyLine.State.OPEN) {
                    boolean duplicateLine = false;
                    for(MyLine otherLines : movesLeft){
                        if(lines.dot1 == otherLines.dot1 && lines.dot2 == otherLines.dot2){
                            duplicateLine = true;
                        }
                    }
                    if(!duplicateLine) {
                        numberOfOpenLines++;
                        movesLeft.add(lines);
                    }
                }
           // }
        }

        numberOfMovesLeft = numberOfOpenLines;
    }

    public void updateScore(){
        int p1s = 0, p2s = 0;
        for(Box b : boxes){
            if(b.state == Box.State.P1FILLED){
                p1s++;
            } else if(b.state == Box.State.P2FILLED){
                p2s++;
            }
        }

        if(p1s > p1Score){
            p1Score = p1s;
        }

        if(p2s > p2Score){
            p2Score = p2s;
        }

        updateNumberOfMovesLeft();
    }

    public ArrayList<MyLine> movesLeft(){
        return movesLeft;
    }

    public void makeMove(MyLine choice){
        boolean setTwice =false;
        int p1s = p1Score, p2s = p2Score;
        for(Box b: boxes){
            if(edgeLines.contains(choice)){
                b.setLine(choice,whoseTurn);
                setTwice = true;
            }
            b.setLine(choice, whoseTurn);
            updateScore();
        }
        //System.out.println(setTwice);

        if(p1s == p1Score && p2s == p2Score){
            //System.out.println("ending turn");
            this.endTurn();
        }

    }

    public void myToString(){
        System.out.println("The scores are : " + p1Score + " " + p2Score);
        System.out.println("There are " + movesLeft.size() + " moves left. They are: ");
        System.out.println(movesLeft.toString());


    }
}
