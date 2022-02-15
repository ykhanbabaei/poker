package com.workwise.assignment.poker;

import com.workwise.assignment.poker.model.Card;
import com.workwise.assignment.poker.model.CardValue;
import com.workwise.assignment.poker.model.WinType;

import java.util.*;
import java.util.stream.Collectors;

import static com.workwise.assignment.poker.model.WinType.*;

public class Poker {

    private final List<Card> hand1;
    private final List<Card> hand2;

    private int winner = 0;// 1 or -1
    private WinType winType = NONE;

    public Poker(List<Card> hand1, List<Card> hand2) {
        if(hand1.size() != 5 || hand2.size() != 5){
            throw new IllegalStateException("expected 5 cards for each hand");
        }
        this.hand1 = new ArrayList<>(hand1);
        this.hand2 = new ArrayList<>(hand2);
    }

    /**
     *
     * @return more than zero means hand1 won, less than zero hand 2 won and zero tie
     */
    public int getWinner() {
        return winner;
    }

    public Poker run() {
        hand1.sort((h1, h2) -> compareCardValue(h1.getValue(), h2.getValue()));
        hand2.sort((h1, h2) -> compareCardValue(h1.getValue(), h2.getValue()));

        try{
            int compare;

            compare = compareStraightFlush();
            if(compare != 0){
                throw new WinnerException(compare, STRAIGHT_FLUSH);
            }
            compare = compareFourOfaKind();
            if(compare != 0){
                throw new WinnerException(compare, FOUR_OF_A_KIND);
            }
            compare = compareFullHouse();
            if(compare != 0){
                throw new WinnerException(compare, FULL_HOUSE);
            }
            compare = compareFlush();
            if(compare != 0){
                throw new WinnerException(compare, FLUSH);
            }
            compare = compareStraight();
            if(compare != 0){
                throw new WinnerException(compare, STRAIGHT);
            }
            compare = compareThreeOfaKind();
            if(compare != 0){
                throw new WinnerException(compare, THREE_OF_A_KIND);
            }
            compare = compareTwoOfaKind();
            if(compare != 0){
                throw new WinnerException(compare, TWO_OF_A_KIND);
            }
            compare = compareHighCard();
            if(compare != 0){
                throw new WinnerException(compare, HIGH_CARD);
            }
        }catch(WinnerException winnerException){
            winner = winnerException.getWinner();
            winType = winnerException.getType();
        }
        return this;
    }

    private int compareTwoOfaKind() {
        Map<CardValue, Long> groupByCardValue1 = hand1.stream().map(Card::getValue).collect(
                Collectors.groupingBy(cardValue -> cardValue, Collectors.counting()));
        Map<CardValue, Long> groupByCardValue2 = hand2.stream().map(Card::getValue).collect(
                Collectors.groupingBy(cardValue -> cardValue, Collectors.counting()));
        boolean hasTwoPair1 = groupByCardValue1.size() == 3;
        boolean hasTwoPair2 = groupByCardValue2.size() == 3;
        if(hasTwoPair1 && !hasTwoPair2){
            return 1;
        }
        else if(!hasTwoPair1 && hasTwoPair2){
            return -1;
        }
        else if(hasTwoPair1 && hasTwoPair2){
            return compareTwoCouples(groupByCardValue1, groupByCardValue2);
        }
        boolean hasOnePair1 = groupByCardValue1.size() == 4;
        boolean hasOnePair2 = groupByCardValue2.size() == 4;
        if(hasOnePair1 && !hasOnePair2){
            return 1;
        }
        else if(!hasOnePair1 && hasOnePair2){
            return -1;
        }
        else if(hasOnePair1 && hasOnePair2){
            CardValue value1 = groupByCardValue1.entrySet().stream().filter(entry -> entry.getValue() == 2).map(Map.Entry::getKey).findFirst().get();
            CardValue value2 = groupByCardValue2.entrySet().stream().filter(entry -> entry.getValue() == 2).map(Map.Entry::getKey).findFirst().get();
            return Integer.compare(value1.ordinal(), value2.ordinal());
        }
        return 0;
    }

    private int compareTwoCouples(Map<CardValue, Long> groupByCardValue1, Map<CardValue, Long> groupByCardValue2) {
        List<CardValue> twoPair1 = groupByCardValue1.entrySet().stream().filter(entry -> entry.getValue() == 2).map(Map.Entry::getKey).collect(Collectors.toList());
        List<CardValue> twoPair2 = groupByCardValue2.entrySet().stream().filter(entry -> entry.getValue() == 2).map(Map.Entry::getKey).collect(Collectors.toList());
        twoPair1.sort(Comparator.comparingInt(Enum::ordinal));
        twoPair2.sort(Comparator.comparingInt(Enum::ordinal));
        if(twoPair1.get(0) != twoPair2.get(0)){
            return Integer.compare(twoPair1.get(0).ordinal(), twoPair2.get(0).ordinal());
        }
        if(twoPair1.get(1) != twoPair2.get(1)){
            return Integer.compare(twoPair1.get(1).ordinal(), twoPair2.get(1).ordinal());
        }
        Optional<CardValue> value1 = groupByCardValue1.entrySet().stream().filter(entry -> entry.getValue() == 1).map(Map.Entry::getKey).findFirst();
        Optional<CardValue> value2 = groupByCardValue2.entrySet().stream().filter(entry -> entry.getValue() == 1).map(Map.Entry::getKey).findFirst();
        return Integer.compare(value1.get().ordinal(), value2.get().ordinal());
    }

    private int compareThreeOfaKind() {
        Integer value1 = findHitsOfaKind(hand1, 3).map(Enum::ordinal).orElse(-1);
        Integer value2 = findHitsOfaKind(hand2, 3).map(Enum::ordinal).orElse(-1);
        return value1.compareTo(value2);
    }

    private int compareStraight() {
        boolean isStraight1 = isStraight(hand1);
        boolean isStraight2 = isStraight(hand2);
        if(!isStraight1 && !isStraight2){
            return 0;
        }
        if(isStraight1 && !isStraight2){
            return 1;
        }
        if(!isStraight1 && isStraight2){
            return -1;
        }
        return Integer.compare(hand1.get(0).getValue().ordinal(), hand2.get(0).getValue().ordinal());
    }

    private int compareFlush() {
        boolean isFlush1 = isFlush(hand1);
        boolean isFlush2 = isFlush(hand2);
        if(!isFlush1 && !isFlush2){
            return 0;
        }
        if(isFlush1 && !isFlush2){
            return 1;
        }
        if(!isFlush1 && isFlush2){
            return -1;
        }
        return compareHighCard();
    }

    private int compareHighCard() {
        for(int i = 4; i >= 0; i--){
            int compare = Integer.compare(hand1.get(i).getValue().ordinal(), hand2.get(i).getValue().ordinal());
            if(compare != 0){
                return compare;
            }
        }
        return 0;
    }

    private int compareFullHouse() {
        Integer value1 = findHitsOfaKind(hand1, 3).map(Enum::ordinal).orElse(-1);
        Integer value2 = findHitsOfaKind(hand2, 3).map(Enum::ordinal).orElse(-1);

        boolean fullHouse1 = value1 != -1 && hand1.stream().map(Card::getValue).filter(cardValue -> cardValue.ordinal() != value1).distinct().count() == 1;
        boolean fullHouse2 = value2 != -1 && hand2.stream().map(Card::getValue).filter(cardValue -> cardValue.ordinal() != value2).distinct().count() == 1;
        if(!fullHouse1 && !fullHouse2){
            return 0;
        }
        if(fullHouse1 && !fullHouse2){
            return 1;
        }
        if(!fullHouse1 && fullHouse2){
            return -1;
        }
        return value1.compareTo(value2);
    }

    private int compareStraightFlush() {
        boolean isStraightFlush1 = isFlush(hand1) && isStraight(hand1);
        boolean isStraightFlush2 = isFlush(hand2) && isStraight(hand2);
        if(!isStraightFlush1 && !isStraightFlush2){
            return 0;
        }
        if(isStraightFlush1 && !isStraightFlush2){
            return 1;
        }
        if(!isStraightFlush1 && isStraightFlush2){
            return -1;
        }
        return Integer.compare(hand1.get(0).getValue().ordinal(), hand2.get(0).getValue().ordinal());
    }

    private int compareFourOfaKind() {
        Integer value1 = findFourOfaKind(hand1).map(Enum::ordinal).orElse(-1);
        Integer value2 = findFourOfaKind(hand2).map(Enum::ordinal).orElse(-1);
        return value1.compareTo(value2);
    }

    private static Optional<CardValue> findFourOfaKind(List<Card> hand) {
        return findHitsOfaKind(hand, 4);
    }

    private static Optional<CardValue> findHitsOfaKind(List<Card> hand, int hits) {
        Map<CardValue, Long> groupByCardValue = hand.stream().map(Card::getValue).collect(
                Collectors.groupingBy(cardValue -> cardValue, Collectors.counting()));
        for (Map.Entry<CardValue, Long> entry : groupByCardValue.entrySet()) {
            if(entry.getValue() == hits){
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    private int compareCardValue(CardValue value1, CardValue value2) {
        return Integer.compare(value1.ordinal(), value2.ordinal());
    }

    private boolean isStraight(List<Card> hand){
        //hand is already asc sorted
        int expected = hand.get(0).getValue().ordinal();
        for (Card card: hand) {
            if(card.getValue().ordinal() != expected++){
                return false;
            }
        }
        return true;
    }

    private boolean isFlush(List<Card> hand){
        return hand.stream().map(Card::getSuit).distinct().count() == 1;
    }

    @Override
    public String toString() {
        return "Poker{" +
                "winner=" + winner  +
                ", winType=" + winType +
                '}';
    }

    public String formattedWinner(){
        return formatWinner();
    }

    private String formatWinner() {
        if(winner==0){
            return "Tie!";
        }
        return winner > 0 ? "Hand 1 has won!" : "Hand 2 has won!" ;
    }

    public WinType getType() {
        return winType;
    }
}
