import com.workwise.assignment.poker.model.Card;
import com.workwise.assignment.poker.Poker;
import com.workwise.assignment.poker.model.WinType;
import org.junit.Test;

import java.util.List;

import static com.workwise.assignment.poker.model.CardSuit.*;
import static com.workwise.assignment.poker.model.CardValue.*;
import static org.junit.Assert.assertEquals;

public class PokerTest {

    @Test
    public void testWinner(){
        Poker poker;
        poker = new Poker(
                List.of(new Card(V2, H), new Card(V3, D), new Card(V5, S), new Card(V9, C), new Card(K, D)),
                List.of(new Card(V2, C), new Card(V3, H), new Card(V4, S), new Card(V8, C), new Card(A, H))).run();
        assertEquals(poker.formattedWinner(), "Hand 2 has won!");

        poker = new Poker(
                List.of(new Card(V2, H), new Card(V4, S), new Card(V4, C), new Card(V2, D), new Card(V4, H)),
                List.of(new Card(V2, S), new Card(V8, S), new Card(A, S), new Card(Q, S), new Card(V3, S))).run();
        assertEquals(poker.formattedWinner(), "Hand 1 has won!");

        poker = new Poker(
                List.of(new Card(V2, H), new Card(V3, D), new Card(V5, S), new Card(V9, C), new Card(K, D)),
                List.of(new Card(V2, D), new Card(V3, H), new Card(V5, C), new Card(V9, S), new Card(K, H))).run();
        assertEquals(poker.formattedWinner(), "Tie!");
    }

    @Test
    public void testWinType(){
        Poker poker;
        poker = new Poker(
                List.of(new Card(V2, H), new Card(V3, D), new Card(V5, S), new Card(V9, C), new Card(K, D)),
                List.of(new Card(V2, C), new Card(V3, H), new Card(V4, S), new Card(V8, C), new Card(A, H))).run();
        assertEquals(poker.getType(), WinType.HIGH_CARD);

        poker = new Poker(
                List.of(new Card(V2, H), new Card(V4, S), new Card(V4, C), new Card(V2, D), new Card(V4, H)),
                List.of(new Card(V2, S), new Card(V8, S), new Card(A, S), new Card(Q, S), new Card(V3, S))).run();
        assertEquals(poker.getType(), WinType.FULL_HOUSE);

        poker = new Poker(
                List.of(new Card(V2, H), new Card(V3, D), new Card(V5, S), new Card(V9, C), new Card(K, D)),
                List.of(new Card(V2, D), new Card(V3, H), new Card(V5, C), new Card(V9, S), new Card(K, H))).run();
        assertEquals(poker.getType(), WinType.NONE);
    }

}
