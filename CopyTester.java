import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by lightray on 12/12/16.
 */
public class CopyTester {

    static MyLine[][] lines = new MyLine[2][2];
    static ArrayList<Coordinate> dotCoords = new ArrayList<>();
    static ArrayList<MyLine> edgeLines = new ArrayList<>();
    static ArrayList<MyLine> allLines = new ArrayList<>();
    static ArrayList<Box> boxes = new ArrayList<>();
    static State state;

    public static void main(String[] args){
        state = initializeState();
        state.myToString();
        //State s = new State(state);

        State oldState = new State(state);
        do{
            oldState.makeMove(oldState.movesLeft.get(0));
            oldState.myToString();
            oldState = new State(oldState);
        } while(!oldState.movesLeft.isEmpty());

//        oldState.makeMove(oldState.movesLeft.get(0));
//        oldState.myToString();
//
//        State newState = new State(oldState);
//        newState.makeMove(newState.movesLeft.get(0));
//        newState.myToString();
//
//        State newState2 = new State(newState);
//        newState2.makeMove(newState2.movesLeft.get(0));
//        newState2.myToString();


    }

    private static State initializeState() {
        State state;
        double borderWidth = 40d;
        double spaceBetweenX = Math.ceil((500 - 2 * (borderWidth + 5)) / (lines.length));
        double spaceBetweenY = Math.ceil((500 - 2 * (borderWidth + 5)) / (lines[0].length));
        //g.setColor(Color.BLACK);
        int maxX = 0;
        int maxY = 0;
        int prevY = 40;
        for (int i = 0; i <= lines.length; i++) {
            int prevX = 40;
            for (int j = 0; j <= lines[0].length; j++) {
                //g.fillRect(prevX, prevY, 5, 5);
                dotCoords.add(new Coordinate(prevX, prevY));
                if (prevX > maxX) {
                    maxX = prevX;
                }
                prevX = prevX + (int) spaceBetweenX;

            }
            if (prevY > maxY) {
                maxY = prevY;
            }
            prevY = prevY + (int) spaceBetweenY;

        }
        System.out.printf("Maxes: %d,%d\n", maxX, maxY);
        MyLine[][] rowLines = new MyLine[2 + 1][2];
        int rowNum = 0;
        int posInRow = 0;
        for (Coordinate dot : dotCoords) {
            if (dot.x < maxX) {
                MyLine l = new MyLine(dot, dotCoords.get(dotCoords.indexOf(dot) + 1));
                allLines.add(l);
                if (rowNum == 0 || rowNum == rowLines.length - 1) {
                    edgeLines.add(l);
                }
                rowLines[rowNum][posInRow] = l;
                posInRow++;
            } else {
                rowNum++;
                posInRow = 0;
            }
        }

        MyLine[][] colLines = new MyLine[2+1][2];
        int colNum = 0;
        int posInCol = 0;
//            for(Coordinate dot : dotCoords){
//                if(dot.y < maxY){
//                    System.out.println("Dot that's giving me problems is at: " + dot.x + " " + dot.y);
//                    colLines[colNum][posInCol] =  new MyLine(dot,dotCoords.get(dotCoords.indexOf(dot) + (SIZE+1)) );
//                    posInCol++;
//                } else {
//                    colNum++;
//                    posInCol = 0;
//                }
//            }

        for(int i = 0; i < rowLines.length-1; i++){
            for(int j = 0; j < rowLines[0].length; j++){
                MyLine l = new MyLine(rowLines[i][j].dot1,rowLines[i+1][j].dot1);
                colLines[j][i] = l;
                allLines.add(l);
                if(j == 0){
                    edgeLines.add(l);
                }
                if(j == rowLines[0].length-1){
                    l = new MyLine(rowLines[i][j].dot2,rowLines[i+1][j].dot2);
                    colLines[j+1][i] = l;
                    allLines.add(l);
                    edgeLines.add(l);
                }
            }
        }
        Random r = new Random();
        ArrayList<Box> boxes = new ArrayList<>();
        for(int i = 0; i < rowLines.length-1; i++){
            for(int j = 0; j < rowLines[0].length; j++){
                System.out.printf("%d %d: ",i,j);
                System.out.println("--- " + colLines[j][i]+" "+ colLines[j+1][i]+" "+rowLines[i][j]+" "+ rowLines[i+1][j]+" ---");
                Box b = new Box(colLines[j][i], colLines[j+1][i], rowLines[i][j], rowLines[i+1][j]);
                b.color = new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
                boxes.add(b);
            }

        }

        state = new State(boxes,allLines);
        state.edgeLines = edgeLines;
        return state;
    }
}
