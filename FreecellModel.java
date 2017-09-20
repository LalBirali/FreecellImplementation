package cs3500.hw02;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * represents the Model for the game of FreeCell with all operations implemented.
 */
public class FreecellModel implements FreecellOperations<Card> {

  /**
   * NOTE: Edit I made to the access of these fields. To avoid code duplication, I made each of
   * these fields protected so a sub class can access these fields directly. This makes checking
   * for valid builds possible.
   */
  protected ArrayList<ArrayList<Card>> cascadePiles;
  protected ArrayList<ArrayList<Card>> openPiles;
  protected ArrayList<ArrayList<Card>> foundationPiles;
  protected int numOpenPiles;
  protected int numCascadePiles;
  protected boolean hasGameStarted;

  /**
   * Constructs a new FreecellModel. Game has not started upon creation.
   */
  public FreecellModel() {
    this.hasGameStarted = false;
    cascadePiles = new ArrayList<>();
    openPiles = new ArrayList<>();
    foundationPiles = new ArrayList<>(4);
    for (int i = 0; i < 4; i++) {
      ArrayList<Card> pile = new ArrayList<>(13);
      foundationPiles.add(pile);
    }
  }

  @Override
  public List<Card> getDeck() {
    List<Card> deck = new ArrayList<>(52);
    ArrayList<String> allSuits = new ArrayList<>(Arrays.asList("hearts", "clubs",
            "spades", "diamonds"));
    ArrayList<Integer> allValues = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6,
            7, 8, 9, 10, 11, 12, 13));
    for (int i = 0; i < allSuits.size(); i++) {
      String suit = allSuits.get(i);
      for (int j = 0; j < allValues.size(); j++) {
        deck.add(new Card(allValues.get(j), suit));
      }
    }
    return deck;
  }

  /**
   * Shuffles the given deck.
   * @param deck is a valid Deck of Cards
   */
  private void shuffle(List<Card> deck) {
    Random rand = new Random();
    for (int i = 0; i < deck.size(); i++) {
      int indexSwap = rand.nextInt(deck.size());
      Card temp = deck.get(i);
      deck.set(i, deck.get(indexSwap));
      deck.set(indexSwap, temp);
    }
  }

  /**
   * Determines if given deck of Cards is valid.
   * @param deck is the given Deck of cards to be determined if it's valid
   */
  private void validDeck(List<Card> deck) {
    List<Card> toCompare = this.getDeck();
    if (!(deck.size() == toCompare.size() && deck.containsAll(toCompare))) {
      throw new IllegalArgumentException("invalid deck provided");
    }
  }

  @Override
  public void startGame(List<Card> deck, int numCascadePiles, int numOpenPiles,
                        boolean shuffle) throws IllegalArgumentException {
    validDeck(deck);
    validGameStart(numCascadePiles, numOpenPiles);
    this.numCascadePiles = numCascadePiles;
    this.numOpenPiles = numOpenPiles;
    this.hasGameStarted = true;
    if (shuffle) {
      this.shuffle(deck);
    }
    this.openPiles = this.dealOpen();
    this.cascadePiles = this.dealCascade(deck, this.numCascadePiles);
  }

  /**
   * Deals out all the cards into the respective Cascade piles.
   * @param deck is a valid Deck of cards
   * @param numCascadePiles is a valid number of cascadePiles
   * @return the correctly dealt cascade pile
   */
  private ArrayList<ArrayList<Card>> dealCascade(List<Card> deck, int numCascadePiles) {
    ArrayList<ArrayList<Card>> cascades = new ArrayList<>();
    for (int i = 0; i < numCascadePiles; i++) {
      ArrayList<Card> pile = new ArrayList<>();
      for (int j = i; j < deck.size(); j += numCascadePiles) {
        pile.add(deck.get(j));
      }
      cascades.add(pile);
    }

    return cascades;
  }

  /**
   * initializes Open pile.
   * @return what Open piles need to be initialized to, empty
   */
  private ArrayList<ArrayList<Card>> dealOpen() {
    ArrayList<ArrayList<Card>> open = new ArrayList<>();
    for (int i = 0; i < this.numOpenPiles; i++) {
      ArrayList<Card> pile = new ArrayList<>(1);
      open.add(pile);
    }

    return open;
  }

  /**
   * Will try to ensure there is a valid number of Cascade and Open piles.
   * @param numCascadePiles the user's chosen number of cascade piles
   * @param numOpenPiles the user's chosen number of open piles
   */
  private void validGameStart(int numCascadePiles, int numOpenPiles) {
    if (numCascadePiles < 4 || numOpenPiles < 1) {
      throw new IllegalArgumentException("chosen number of piles is invalid");
    }
  }

  @Override
  public void move(PileType source, int pileNumber, int cardIndex, PileType destination,
                   int destPileNumber) throws IllegalArgumentException {
    if (!this.hasGameStarted) {
      throw new IllegalStateException("game has not started yet");
    }
    if (pileNumber < 0 || destPileNumber < 0 || cardIndex < 0) {
      throw new IllegalArgumentException("invalid pile number");
    }
    switch (source) {
      case OPEN: fromOpenPile(source, pileNumber, cardIndex, destination, destPileNumber);
        break;
      case CASCADE: fromCascadePile(source, pileNumber, cardIndex, destination, destPileNumber);
        break;
      case FOUNDATION: throw new IllegalArgumentException("cannot move a card " +
              "from a foundation pile");
      default: break;
    }
  }

  /**
   * Helper to determine validity of move from an Open Pile.
   * @param source is the PileType from which a card is to be moved
   * @param pileNumber is the chosen pile
   * @param cardIndex is index of chosen card
   * @param destination is destination PileType
   * @param destPileNumber destination pileNumber
   */
  private void fromOpenPile(PileType source, int pileNumber, int cardIndex,
                            PileType destination, int destPileNumber) {
    if (pileNumber > this.numOpenPiles) {
      throw new IllegalArgumentException("pile doesn't exist.");
    }
    ArrayList<Card> pile = this.openPiles.get(pileNumber);
    if (pile.isEmpty()) {
      throw new IllegalArgumentException("open pile source is empty");
    }
    if (cardIndex != 0) {
      throw new IllegalArgumentException("cannot execute multimove");
    }
    Card c = pile.get(0);
    determineDestination(source, pileNumber, c, destination, destPileNumber);

  }

  /**
   * Helper to determine validity of move from a Cascade Pile.
   * @param source is the PileType from which a card is to be moved
   * @param pileNumber is the chosen pile
   * @param cardIndex is index of chosen card
   * @param destination is destination PileType
   * @param destPileNumber destination pileNumber
   */
  private void fromCascadePile(PileType source, int pileNumber, int cardIndex,
                               PileType destination, int destPileNumber) {
    if (pileNumber >= this.numCascadePiles) {
      throw new IllegalArgumentException("pile doesn't exit");
    }
    ArrayList<Card> pile = this.cascadePiles.get(pileNumber);
    if (cardIndex >= pile.size() || cardIndex < pile.size() - 1) {
      throw new IllegalArgumentException("cannot execute multimove");
    }
    Card c = pile.get(pile.size() - 1);
    determineDestination(source, pileNumber, c, destination, destPileNumber);

  }

  /**
   * Determines which type pile is the destination to execute the move.
   * @param source is the source pile.
   * @param pileNumber is the source pile number.
   * @param c is the Card to be moved.
   * @param destination is the destination Pile provided.
   * @param destPileNumber is the destination pile number.
   */
  private void determineDestination(PileType source, int pileNumber, Card c,
                                    PileType destination, int destPileNumber) {
    switch (destination) {
      case OPEN: toOpenPile(source, pileNumber, c, destPileNumber);
        break;
      case CASCADE: toCascadePile(source, pileNumber, c, destPileNumber);
        break;
      case FOUNDATION: toFoundationPile(source, pileNumber, c, destPileNumber);
        break;
      default: break;
    }

  }

  /**
   * Destination is OpenPile.
   * @param source is PileType of source of Card
   * @param pileNumber is the pile which the card was removed from
   * @param c is the one that wants to be moved
   * @param destPileNumber is the pile where the card is being moved to
   */
  private void toOpenPile(PileType source, int pileNumber, Card c, int destPileNumber) {
    switch (source) {
      case CASCADE: {
        if (destPileNumber >= this.numOpenPiles) {
          throw new IllegalArgumentException("invalid destination pile");
        } else {
          if (this.openPiles.get(destPileNumber).isEmpty()) {
            this.openPiles.get(destPileNumber).add(c);
            this.cascadePiles.get(pileNumber).remove(c);
          } else {
            throw new IllegalArgumentException("invalid move.");
          }
        }
      }
      break;
      case OPEN: {
        if (destPileNumber >= this.numOpenPiles) {
          throw new IllegalArgumentException("invalid destination pile");
        } else {
          if (this.openPiles.get(destPileNumber).isEmpty()) {
            this.openPiles.get(destPileNumber).add(c);
            this.openPiles.get(pileNumber).remove(c);
          } else {
            throw new IllegalArgumentException("invalid move.");
          }
        }
      }
      break;
      default: break;
    }

  }



  /**
   * Destination is CascadePile.
   * NOTE: CHANGE MADE. see comment below.
   * @param source is PileType of source of Card
   * @param pileNumber is the pile which the card was removed from
   * @param c is the one that wants to be moved
   * @param destPileNumber is the pile where the card is being moved to
   */
  private void toCascadePile(PileType source, int pileNumber, Card c, int destPileNumber) {
    switch (source) {
      case CASCADE: {
        if (destPileNumber >= this.numCascadePiles) {
          throw new IllegalArgumentException("invalid destination pile");
        } else {
          ArrayList<Card> pile = this.cascadePiles.get(destPileNumber);
          //this allows for the addition of a single card to an empty cascade pile. This was not
          //supported earlier.
          if (pile.isEmpty()) {
            pile.add(c);
            this.cascadePiles.get(pileNumber).remove(c);
          }
          Card last = pile.get(pile.size() - 1);
          if (last.getValue() - c.getValue() == 1) {
            pile.add(c);
            this.cascadePiles.get(pileNumber).remove(c);
          } else {
            throw new IllegalArgumentException("may be multimove, still invalid");
          }
        }
      }
      break;
      case OPEN: {
        if (destPileNumber >= this.numCascadePiles) {
          throw new IllegalArgumentException("invalid destination pile");
        } else {
          ArrayList<Card> pile = this.cascadePiles.get(destPileNumber);
          Card last = pile.get(pile.size());
          if (pile.isEmpty()) {
            pile.add(c);
            this.cascadePiles.get(pileNumber).remove(c);
          }
          if (last.getValue() - c.getValue() == 1) {
            pile.add(c);
            this.openPiles.get(pileNumber).remove(c);
          } else {
            throw new IllegalArgumentException("invalid move.");
          }
        }
      }
      break;
      default: break;
    }
  }

  /**
   * Destination is FoundationPile.
   * @param source is PileType of source of Card
   * @param pileNumber is the pile which the card was removed from
   * @param c is the one that wants to be moved
   * @param destPileNumber is the pile where the card is being moved to
   */
  private void toFoundationPile(PileType source, int pileNumber, Card c, int destPileNumber) {
    switch (source) {
      case CASCADE: {
        if (destPileNumber > 4) {
          throw new IllegalArgumentException("invalid destination pile");
        } else {
          if (this.foundationPiles.get(destPileNumber).isEmpty()) {
            if (c.getValue() == 1) {
              this.foundationPiles.get(destPileNumber).add(c);
              this.cascadePiles.get(pileNumber).remove(c);
            } else {
              throw new IllegalArgumentException("must be ace");
            }
          } else {
            ArrayList<Card> pile = this.foundationPiles.get(destPileNumber);
            Card temp = pile.get(pile.size() - 1);
            if (c.getValue() - temp.getValue() == 1 && c.getSuit().equals(temp.getSuit())) {
              this.foundationPiles.get(destPileNumber).add(c);
              this.cascadePiles.get(pileNumber).remove(c);
            } else {
              throw new IllegalArgumentException("may be multimove, still invalid");
            }
          }
        }
      }
      break;
      case OPEN: {
        if (destPileNumber > 4) {
          throw new IllegalArgumentException("invalid destination pile");
        } else {
          if (this.foundationPiles.get(destPileNumber).isEmpty()) {
            if (c.getValue() == 1) {
              this.foundationPiles.get(destPileNumber).add(c);
              this.openPiles.get(pileNumber).remove(c);
            } else {
              throw new IllegalArgumentException("must be ace");
            }
          } else {
            ArrayList<Card> pile = this.foundationPiles.get(destPileNumber);
            Card temp = pile.get(pile.size() - 1);
            if (c.getValue() - temp.getValue() == 1 && c.getSuit().equals(temp.getSuit())) {
              this.foundationPiles.get(destPileNumber).add(c);
              this.openPiles.get(pileNumber).remove(c);
            } else {
              throw new IllegalArgumentException("invalid move");
            }
          }
        }
      }
      break;
      default: break;
    }
  }

  /**
   * Checks to see if a singular foundation pile is correctly sorted.
   * @param foundationPile is the given foundation pile
   * @return is foundation pile is sorted
   */
  private boolean isEachPileSorted(List<Card> foundationPile) {
    if (foundationPile.size() != 13) {
      return false;
    }
    for (int i = 0; i < 12; i++) {
      Card first = foundationPile.get(i);
      Card second = foundationPile.get(i + 1);
      if (!(first.getSuit().equals(second.getSuit())
              && first.getValue() + 1 == second.getValue())) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean isGameOver() {
    if (!hasGameStarted) {
      return false;
    }
    for (ArrayList<Card> pile : this.foundationPiles) {
      if (!isEachPileSorted(pile)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns string of displayed pile based on given identifier.
   * @param str is the pile identifier
   * @param pile is pile of cards to be accessed
   * @return state of given pile based on identifier
   */
  private String stateOfPile(String str, ArrayList<ArrayList<Card>> pile) {
    String s = "";
    int count = 1;
    for (int i = 0; i < pile.size(); i++) {
      ArrayList<Card>  temp = pile.get(i);
      if (temp.isEmpty()) {
        s += str + count + ":";
      } else {
        s += str + count + ": ";
      }
      for (int j = 0; j < temp.size(); j++) {
        Card c = temp.get(j);
        if (j == temp.size() - 1) {
          s += c;
        } else {
          s += c + ", ";
        }
      }
      if (str.equals("C") && i == pile.size() - 1) {
        continue;
      }
      count += 1;
      s += "\n";
    }

    return s;
  }

  @Override
  public String getGameState() {
    if (hasGameStarted) {
      return stateOfPile("F", this.foundationPiles) +
              stateOfPile("O", this.openPiles) +
              stateOfPile("C", this.cascadePiles);
    }
    return "";
  }


}

