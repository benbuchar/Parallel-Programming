
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by lightray on 12/5/16.
 */

public class UI extends JFrame implements ActionListener, MouseListener {

    private static int XDIM = 540;
    private static int YDIM = 540;


    private int[][] lines;
    ArrayList<Coordinate> dotCoords = new ArrayList<>();
    ArrayList<MyLine> allLines = new ArrayList<>();
    ArrayList<MyLine> edgeLines = new ArrayList<>();
    Coordinate nearestDot;
    Coordinate nextNearestDot;
    volatile State state;
    int SIZE = 5;
    int numberOfBoxesFilled;


    JPanel gamePanel;

    public UI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        lines = new int[SIZE][SIZE];

        gamePanel = new GamePanel();
        gamePanel.setFocusable(true);
        gamePanel.addMouseListener(this);

        add(gamePanel, BorderLayout.CENTER);
        numberOfBoxesFilled = 0;

        setSize(XDIM, YDIM);
        pack();
        setResizable(false);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {

    }

    public static void createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        UI kingdom = new UI();

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }



    private void submitMove(Coordinate click) {
        findNearestDot(click);
        MyLine choice = findLine(nearestDot,nextNearestDot);

        if(state.movesLeft.contains(choice)) {
            state.makeMove(choice);
        }


        /**
         * After the user makes a move, the UI
         * calls the makeMove() method in parallel,
         * which calls the BoxMaster's recursive decomposition
         * algorithm.
         */
        SwingUtilities.invokeLater(()-> {
            while(state.whoseTurn == Player.P2 && state.movesLeft.size() > 0){
                if(state.movesLeft.size() > 0) {
                    makeMove();
                }
            }
        });

        if(state.movesLeft.size() == 0){
            endGame();
        }

        System.out.println("The scores are now: " + state.p1Score + " " + state.p2Score);
    }

    private void endGame() {
        JOptionPane.showMessageDialog(throneRoom,"Player 1: " + state.p1Score + "\nPlayer 2: " + state.p2Score,"Game Over",JOptionPane.PLAIN_MESSAGE);
        System.exit(0);
    }

    private void makeMove() {
        /**
         * Creates a BoxMaster who finds the list of
         * moves to take in order, and takes the first move.
         */

            BoxMaster master = new BoxMaster(state,2);
            MyLine choice = master.compute().get(0);

            state.makeMove(choice);



    }

    private MyLine findLine(Coordinate nearestDot, Coordinate nextNearestDot) {
        MyLine returnLine = null;
        for(MyLine line : allLines){
            if((line.dot1 == nearestDot && line.dot2 == nextNearestDot)
                    || (line.dot2 == nearestDot && line.dot1 == nextNearestDot)){
                returnLine = line;
            }
        }
        return returnLine;
    }

    private void findNearestDot(Coordinate click) {
        Coordinate nearest = dotCoords.get(0);
        Vector nearestVect = new Vector(click.x - nearest.x, click.y - nearest.y);
        Coordinate nextNearest = dotCoords.get(0);
        Vector nextNearestVect = new Vector(click.x - nextNearest.x, click.y - nextNearest.y);
        for(Coordinate dot : dotCoords){
            Vector distanceVect = new Vector(click.x - dot.x, click.y - dot.y);
            if(distanceVect.getMagnitude() < nearestVect.getMagnitude()){
                nearest = dot;
                nearestVect = distanceVect;
            }
        }

        for(Coordinate dot : dotCoords){
            if(dot != nearest){
                Vector distanceVect = new Vector(click.x - dot.x, click.y - dot.y);
                if(distanceVect.getMagnitude() < nextNearestVect.getMagnitude() || nextNearest == nearest){
                    nextNearest = dot;
                    nextNearestVect = distanceVect;
                }
            }
        }

        nearestDot = nearest;
        nextNearestDot = nextNearest;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        submitMove(new Coordinate(e.getX(),e.getY()));
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    class GamePanel extends JPanel {

        public GamePanel(){

            initializeState();

            Thread painter = new Thread(()-> {
                for(;;){
                        gamePanel.repaint();


                }

            });
            painter.start();

        }

        private void initializeState() {
            double borderWidth = 40d;
            double spaceBetweenX = Math.ceil((XDIM - 2 * (borderWidth + 5)) / (lines.length));
            double spaceBetweenY = Math.ceil((YDIM - 2 * (borderWidth + 5)) / (lines[0].length));
            int maxX=0;
            int maxY=0;
            int prevY = 40;
            for (int i = 0; i <= lines.length; i++) {
                int prevX = 40;
                for (int j = 0; j <= lines[0].length; j++) {
                    dotCoords.add(new Coordinate(prevX,prevY));
                    if(prevX > maxX){
                        maxX = prevX;
                    }
                    prevX = prevX + (int) spaceBetweenX;

                }
                if(prevY > maxY){
                    maxY = prevY;
                }
                prevY = prevY + (int) spaceBetweenY;

            }
            MyLine[][] rowLines = new MyLine[SIZE+1][SIZE];
            int rowNum = 0;
            int posInRow = 0;
            for(Coordinate dot : dotCoords){
                if(dot.x < maxX){
                    MyLine l = new MyLine(dot,dotCoords.get(dotCoords.indexOf(dot)+1));
                    allLines.add(l);
                    if(rowNum == 0 || rowNum == rowLines.length-1){
                        edgeLines.add(l);
                    }
                    rowLines[rowNum][posInRow] = l;
                    posInRow++;
                } else {
                    rowNum++;
                    posInRow = 0;
                }
            }

            MyLine[][] colLines = new MyLine[SIZE+1][SIZE];


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
                    Box b = new Box(colLines[j][i], colLines[j+1][i], rowLines[i][j], rowLines[i+1][j]);
                    b.color = new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
                    boxes.add(b);
                }

            }

            state = new State(boxes,allLines);
            state.edgeLines = edgeLines;

        }


        public Dimension getPreferredSize() {
            return new Dimension(XDIM, YDIM);
        }



        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);


            if(state != null){
                for(Box b : state.boxes){
                    g.setColor(b.color);
                    g.setColor(Color.BLACK);
                    g.drawLine(b.bottomLine.dot1.x, b.bottomLine.dot2.y + 3, b.bottomLine.dot1.x, b.bottomLine.dot2.y + 3);
                }
            }

            for(Coordinate coord : dotCoords){
                g.fillRect(coord.x, coord.y, 5, 5);
            }

            if(state != null){
                for(Box b : state.boxes){
                    if(b.state == Box.State.P1FILLED){
                        g.setColor(Color.GREEN);
                        int lw = b.topLine.dot2.x - b.topLine.dot1.x;
                        g.fillRect(b.topLine.dot1.x+3, b.topLine.dot1.y+3,lw,lw);
                    } else if(b.state == Box.State.P2FILLED){
                        g.setColor(Color.RED);
                        int lw = b.topLine.dot2.x - b.topLine.dot1.x;
                        g.fillRect(b.topLine.dot1.x+3, b.topLine.dot1.y+3,lw,lw);
                    }
                    for(MyLine line : b.lines){
                        if(line.state == MyLine.State.P1 || line.state == MyLine.State.P2){
                            g.setColor(Color.BLACK);
                            g.fillRect(line.dot1.x,line.dot1.y, 7,7);
                            g.fillRect(line.dot2.x,line.dot2.y,7,7);
                            g.drawLine(line.dot1.x+3,line.dot1.y+3,line.dot2.x+3,line.dot2.y+3);
                        }
                    }

                }
            }



        }

    }


}



