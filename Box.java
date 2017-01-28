import java.awt.*;
import java.util.ArrayList;

/**
 * Created by lightray on 12/10/16.
 */
public class Box {
    MyLine leftLine;
    MyLine rightLine;
    MyLine topLine;
    MyLine bottomLine;
    ArrayList<MyLine> lines;
    ArrayList<Coordinate> corners;
    Color color;

    enum State{
        UNFILLED,
        P1FILLED,
        P2FILLED
    }

    State state;

    public Box(MyLine l, MyLine r, MyLine t, MyLine b){
        leftLine = l;
        rightLine = r;
        topLine = t;
        bottomLine = b;
        state = State.UNFILLED;
        initialize();
    }

    private void initialize() {
        lines = new ArrayList<>();
        lines.add(leftLine);
        lines.add(rightLine);
        lines.add(topLine);
        lines.add(bottomLine);
        corners = new ArrayList<>();
        for(MyLine line : lines){
            corners.add(line.dot1);
            corners.add(line.dot2);
        }
    }

    public boolean containsLine(MyLine line){
        for(MyLine l : lines){
            if(l.dot1 == line.dot1 && l.dot2 == line.dot2){
                return true;
            }
        }
        return false;
    }

    public boolean setLine(MyLine line, Player player){
       // if(this.containsLine(line)){
        //System.out.println(line);
            boolean success;
            if(leftLine == line || (leftLine.dot1 == line.dot1 && leftLine.dot2 == line.dot2)){
                success = leftLine.pick(player);
            } else if(rightLine == line || (rightLine.dot1 == line.dot1 && rightLine.dot2 == line.dot2)){
                success =  rightLine.pick(player);
            } else if(topLine == line || (topLine.dot1 == line.dot1 && topLine.dot2 == line.dot2)){
                success =  topLine.pick(player);
            } else if(bottomLine == line || (bottomLine.dot1 == line.dot1 && bottomLine.dot2 == line.dot2)){
                success =  bottomLine.pick(player);
            } else {
                success =  false;
            }

            if(success){
                if(allLinesAreFilled()){
                    this.fillIn(player);
                }
            }
            return success;
//        } else {
//            return false;
//        }
    }

    private void fillIn(Player player) {
        if(player == Player.P1){
            state = State.P1FILLED;
        } else if(player == Player.P2){
            state = State.P2FILLED;
        }
    }

    private boolean allLinesAreFilled() {
        if(rightLine.state != MyLine.State.OPEN && leftLine.state != MyLine.State.OPEN
                && topLine.state != MyLine.State.OPEN && bottomLine.state != MyLine.State.OPEN){
            return true;
        } else {
            return false;
        }
    }

    public boolean hasAllLines(){
        return (rightLine != null && leftLine != null && topLine != null && bottomLine!=null);
    }
}
