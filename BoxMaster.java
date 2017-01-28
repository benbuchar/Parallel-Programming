import java.util.ArrayList;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by lightray on 12/10/16.
 */
public class BoxMaster extends RecursiveTask<ArrayList<MyLine>> {
    State state;
    int depth;

    public BoxMaster(State state, int depth){
        this.depth = depth;
        State stateCopy = state;
        this.state = stateCopy;
    }

    /**
     * The BoxMaster's recursive decomposition algorithm.
     * The BoxMaster looks N-moves ahead in the state of the game
     * and returns a list of the moves it believes are best for each
     * turn.
     *
     * N is defined by the depth field in the BoxMaster constructor.
     *
     * For each available move that can be made, the BoxMaster copies the given
     * state of the game and makes the move on the copy. It then creates a
     * BoxMaster child who is given the new state.
     *
     * In parallel, each BoxMaster will make moves on their states and make children
     * who are given the new states. When a child receives a state with only 1 move
     * left in the game, or the child's depth is specified as 1, it will sequentially
     * determine the best move it can make on the given state.
     *
     * Upon joining from all children, a BoxMaster will choose which ArrayList of
     * moves it has received is the best move list, and return that back up to its parent.
     *
     * @return an ArrayList containing the moves which the BoxMaster and its children
     *  believe will result in the best score for Player 2 if executed in order.
     *  Number of turns evaluated is equal to the depth specified.
     */

    @Override
    protected ArrayList<MyLine> compute() {
        ArrayList<MyLine> bestMoveTrail = new ArrayList<>();

        if(depth > 1 && state.movesLeft.size() > 1){
            //recursive algorithm
            ArrayList<ForkJoinTask<ArrayList<MyLine>>> tasks = new ArrayList<>();
            /**
             * for every move left in this turn, copy the state,
             * make the move, fork a child and give it the new state.
             */
            for(MyLine move : state.movesLeft){
                State s = new State(state);
                s.makeMove(move);
                ForkJoinTask<ArrayList<MyLine>> child = new BoxMaster(s,depth-1).fork();
                tasks.add(child);
            }

            /**
             * Iterate through the list of children in reverse
             * and join to receive the ArrayList containing
             * their determined best moves based on the state
             * they were given.
             */
            ArrayList<ArrayList<MyLine>> bestMoveLists = new ArrayList<>();
            for(int i = tasks.size()-1; i>=0; i--){
                ArrayList<MyLine> moves = tasks.get(i).join();
                bestMoveLists.add(moves);
            }


            /**
             * Prepends the move it made to the state before giving it to its child
             * into the list the child returned.
             */
            for(MyLine movesAtThisLevel : state.movesLeft){
                ArrayList<MyLine> list = bestMoveLists.get(state.movesLeft.indexOf(movesAtThisLevel));
                list.add(0,movesAtThisLevel);
            }

            /**
             * Chooses the best of the move trails given by its children.
             */
            int bestScore = 0;
            int lowestP1Score = 1000;
            int count = 0;
            for(ArrayList<MyLine> bestMoveList : bestMoveLists){
                State s = new State(state);
                for(MyLine move : bestMoveList) {
                    s.makeMove(move);
                }
                if(s.p2Score > bestScore){
                    count++;
                    bestMoveTrail = bestMoveList;
                    bestScore = s.p2Score;
                } else if(s.p2Score == bestScore){
                    if(s.p1Score <= lowestP1Score){
                        count++;
                        bestMoveTrail = bestMoveList;
                        lowestP1Score = s.p1Score;
                    }
                }
            }

            /**
             * If all moves were considered equally good, the BoxMaster chooses
             * a random one.
             */
            if(count == state.movesLeft.size()){
                ThreadLocalRandom r = ThreadLocalRandom.current();
                bestMoveTrail = bestMoveLists.get(r.nextInt(bestMoveLists.size()));
            }

            return bestMoveTrail;

        } else {
            //base case
            return determineBestMoveSeq();
        }

    }

    /**
     * Sequentially determines the best move available
     * to make on the state and returns it as an element
     * in a new ArrayList.
     * @return a new ArrayList containing the best move to make
     * on the state.
     */

    private ArrayList<MyLine> determineBestMoveSeq() {
        MyLine bestMove = null;
        ArrayList<MyLine> list = new ArrayList<>();
        int bestScore = 0;
        int lowestP1Score = 1000;
        int count = 0;
        /**
         * For each move left to make on the state, the BoxMaster creates
         * a copy of the state and makes the move on it. It then looks at
         * the scores of each new state and saves the one which resulted
         * in the highest score for Player 2.
         */
        for(MyLine move : state.movesLeft){
            State s = new State(state);
            s.makeMove(move);
            if(s.p2Score > bestScore){
                count++;
                bestMove = move;
                bestScore = s.p2Score;
            } else if(s.p2Score == bestScore){
                if(s.p1Score <= lowestP1Score){
                    count++;
                    lowestP1Score = s.p1Score;
                    bestMove = move;
                }
            }
        }

        /**
         * If all moves resulted in the same score for Player 2, a random
         * move is chosen.
         */
        if(count == state.movesLeft.size()){
            //all moves were considered the same;
            ThreadLocalRandom r = ThreadLocalRandom.current();
            bestMove = state.movesLeft.get(r.nextInt(state.movesLeft.size()));
        }

        list.add(bestMove);
        return list;

    }

}
