package cs3500.hw02;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a single standard playing card.
 */
public class Card {

  //CHANGE. ADDED color field, to help determine alternating builds. It is an Enum type CardColor.
  private final CardColor color;
  private final int value;
  private final String suit;
  private static final ArrayList<String> allSuits = new ArrayList<>(Arrays.asList("hearts",
          "clubs", "spades", "diamonds"));


  /**
   * Constructs a Standard Playing Card object
   * Value: can only take values from 1-13. Ace represents low card (1). King is highest card (13).
   * Suit: can only be one of four Strings: "hearts", "clubs", "spades", and "diamonds"
   * @param val is the integer representation of card value
   * @param s is the card's suit
   * @throws IllegalArgumentException when card created isn't a standard playing card
   */
  public Card(int val, String s) {
    if (correctSuit(s) && val >= 1 && val <= 13) {
      this.value = val;
      this.suit = s;
      if (this.suit.equalsIgnoreCase("hearts") ||
              this.suit.equalsIgnoreCase("diamonds")) {
        color = CardColor.RED;
      } else {
        color = CardColor.BLACK;
      }
    } else {
      throw new IllegalArgumentException("invalid Card");
    }
  }

  /**
   * Makes sure this card will be initalized with a valid suit.
   * @param suit is the string passed into Card constuctor
   * @return if the suit is valid
   */
  private boolean correctSuit(String suit) {
    if (suit == null) {
      return false;
    }
    for (int i = 0; i < allSuits.size(); i++) {
      if (suit.equals(allSuits.get(i))) {
        return true;
      }
    }
    return false;
  }

  /**
   * Getter for this Card's value.
   * @return this card's Value
   */
  public int getValue() {
    return this.value;
  }

  /**
   * Getter for this Card's suit.
   * @return this card's Suit
   */
  public String getSuit() {
    return this.suit;
  }


  /**
   * getter for this card's color.
   * @return card's color.
   */
  public CardColor getColor() {
    return color;
  }

  /**
   * Decodes 1 back to "A", 11 back to "J", etc.
   * Returns true Card value
   * @return actual value of this Card
   */
  private String realValue() {
    switch (this.value) {
      case 1: return "A";
      case 11: return "J";
      case 12: return "Q";
      case 13: return "K";
      default: return String.valueOf(this.value);
    }
  }

  /**
   * Returns this card's suit symbol as a string.
   * @return suit symbol
   */
  private String suitSymbol() {
    switch (this.suit) {
      case "hearts": return "♥";
      case "diamonds": return "♦";
      case "clubs": return "♣";
      case "spades": return "♠";
      default: return "";
    }
  }

  /**
   * Card equality.
   * @param other is passed in object for equality testing
   * @return if the passed in object is equal to this card
   */
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof Card) {
      Card card = (Card) other;
      return card.getSuit().equals(this.suit) && card.getValue() == this.getValue();
    }
    return false;
  }

  public int hashCode() {
    return this.value * 13;
  }

  /**
   * String representation of this Card.
   * @return card value followed by this card's suit's symbol
   */
  public String toString() {
    return this.realValue() + this.suitSymbol();
  }

}
