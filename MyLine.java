/**
 * Created by lightray on 12/10/16.
 */
public class MyLine {

    Coordinate dot1;
    Coordinate dot2;
    enum State{
        OPEN,
        P1,
        P2
    }
    State state;
    int timesPicked;

    public MyLine(Coordinate dot1, Coordinate dot2){
        this.dot1 = dot1;
        this.dot2 = dot2;
        this.state = State.OPEN;
        timesPicked=0;
    }

    public boolean pick(Player player){
        timesPicked++;
        if(timesPicked < 3){
            if(player == Player.P1){
                state = State.P1;
            } else if(player == Player.P2){
                state = State.P2;
            } else {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }
}
