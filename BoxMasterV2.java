import java.util.ArrayList;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinTask;

/**
 * Created by lightray on 12/11/16.
 */

public class BoxMasterV2 extends CountedCompleter<MyLine> {

    State state;
    CountedCompleter<?> parent;
    int level;
    MyLine bestMove = null;

    public BoxMasterV2(CountedCompleter<?> parent, State state,int level){
        super(parent);
        this.parent = parent;
        this.state = state;
        this.level = level;
    }

    @Override
    public void compute() {
        ArrayList<MyLine> bestMoves = new ArrayList<>();
        if(level < 2) {

            State stateCopy = new State(state);
            State stateCopy2 = new State(state);
            this.addToPendingCount(1);
            BoxMasterV2 m = new BoxMasterV2(this, stateCopy, level+1);
            m.fork();
            this.tryComplete();


        } else {
            int bestScore = 0;
            //MyLine bestMove = null;
            for(MyLine move : state.movesLeft){
                State stateCopy = new State(state);
                stateCopy.makeMove(move);
                if(stateCopy.p2Score > bestScore){
                    bestScore = stateCopy.p2Score;
                    bestMove = move;
                }
            }
            this.complete(bestMove);
        }



    }

    @Override
    public void onCompletion(CountedCompleter<?> taskGiver){

    }


}
