package cs3500.hw04;

import java.util.ArrayList;


import cs3500.hw02.Card;
import cs3500.hw02.FreecellModel;
import cs3500.hw02.PileType;

/**
 * Representation of FreecellModel game with moves that involve builds.
 */
public class MFreecellModel extends FreecellModel {

  /**
   * Constructor for MFreecellModel. Creates a multimove version of a Freecell game.
   * NOTE: if you want to play using builds, use this constructor.
   */
  MFreecellModel() {

    super();
  }


  @Override
  public void move(PileType source, int pileNumber, int cardIndex, PileType destination,
                   int destPileNumber) throws IllegalArgumentException {
    try {
      super.move(source, pileNumber, cardIndex, destination, destPileNumber);
    } catch (IllegalArgumentException e) {
      if (e.getMessage().contains("multimove")) {
        this.multiMove(source, pileNumber, cardIndex, destination, destPileNumber);
      } else {
        throw new IllegalArgumentException(e.getMessage());
      }
    }
  }

  /**
   * Determines max number of cards can be moved based on intermediate piles open.
   * Number empty Open Piles = N.
   * Number empty Cascade Piles = K.
   * Calculates (N+1) * 2^K
   * @return max cards that can be moved
   */
  private int maxCardMove() {
    return (numEmptyPiles(PileType.OPEN) + 1) * (int)Math.pow(2, numEmptyPiles(PileType.CASCADE));
  }

  /**
   * Execution of multimove.
   * @param source is the source pile
   * @param pileNumber is the pile the number of source piles
   * @param cardIndex is start card index to execute multimove
   * @param destination is the destination pile
   * @param destPileNumber is the pile number of the destination piles.
   */
  private void multiMove(PileType source, int pileNumber, int cardIndex, PileType destination,
                         int destPileNumber) {

    if (source == PileType.CASCADE) {
      ArrayList<Card> from = cascadePiles.get(pileNumber);
      if ((from.size() - cardIndex) > maxCardMove()) {
        throw new IllegalArgumentException("cannot move this build.");
      }
      if (from.size() - cardIndex == 1) {
        throw new IllegalArgumentException("is a single move");
      }
      if (validBuild(from, cardIndex, destination, destPileNumber)) {
        switch (destination) {
          case CASCADE: {
            ArrayList<Card> to = cascadePiles.get(destPileNumber);
            addAndRemove(from, cardIndex, to);
            break;
          }
          case FOUNDATION: {
            throw new IllegalArgumentException("cannot move a build to a foundation pile");
          }
          default:
            return;
        }
      } else {
        throw new IllegalArgumentException("not a valid build");
      }
    } else {
      throw new IllegalArgumentException("cannot do a multimove from a non-cascade pile");
    }
  }


  /**
   * Determines how many empty piles of a given pile type exist.
   * NOTE: the way this method is used (in conjunction with the max Cards), there will never be
   * checking for the number of foundation piles, hence the sentinel value of -1.
   * @param pileType is the pile type to be checked
   * @return number of empty piles of given pile type
   */
  private int numEmptyPiles(PileType pileType) {
    int count = 0;
    switch (pileType) {
      case OPEN: {
        for (int i = 0; i < openPiles.size(); i++) {
          if (openPiles.get(i).isEmpty()) {
            count += 1;
          }
        }
        return count;
      }
      case CASCADE: {
        for (int i = 0; i < cascadePiles.size(); i++) {
          if (cascadePiles.get(i).isEmpty()) {
            count += 1;
          }
        }
        return count;
      }
      case FOUNDATION: {
        return -1;
      }
      default: return -1;
    }
  }


  /**
   * Checks to see if the move is valid based on the card arrangements in the pile, and
   * the given destination pile.
   * @param list is the source pile
   * @param cardIndex is the start card index for movement
   * @param destination is the destination pile type
   * @param destPileNumber is the pile number for the destination pile type
   * @return if this a valid build
   */
  private boolean validBuild(ArrayList<Card> list, int cardIndex, PileType destination,
                             int destPileNumber) {

    switch (destination) {
      case CASCADE: {
        if (destPileNumber >= numCascadePiles) {
          throw new IllegalArgumentException("card doesn't exist.");
        }
        Card c = list.get(cardIndex);
        ArrayList<Card> pile = cascadePiles.get(destPileNumber);
        if (pile.isEmpty()) {
          return isBuild(list, cardIndex);
        } else {
          Card last = pile.get(pile.size() - 1);
          if (c.getColor() != last.getColor()
                  && last.getValue() - 1 == c.getValue()) {
            return isBuild(list, cardIndex);
          } else {
            return false;
          }
        }
      }
      case OPEN: {
        throw new IllegalArgumentException("cannot move a build into an open pile directly.");
      }
      case FOUNDATION:
        throw new IllegalArgumentException("cannot move a build into a foundation pile " +
                this.getGameState());
      default:
        return false;
    }

  }

  /**
   * Checks if the cards in pile after cardIndex are arranged in alternating colors and descending
   * point values.
   * @param list is the source pile.
   * @param cardIndex start index
   * @return whether or not cards attempting to be moved is build
   */
  private boolean isBuild(ArrayList<Card> list, int cardIndex) {

    for (int i = cardIndex; i < list.size() - 1; i ++) {
      Card first = list.get(i);
      Card second = list.get(i + 1);
      if (!((first.getColor() != second.getColor())
              && first.getValue() - 1 == second.getValue())) {
        return false;
      }
    }
    return true;
  }

  /**
   * Transfers cards from first pile to the second.
   * @param from is the source pile
   * @param cardIndex is start index for tranferring
   * @param to is the destination pile
   */
  private void addAndRemove(ArrayList<Card> from, int cardIndex, ArrayList<Card> to) {
    for (int i = cardIndex; i < from.size(); i++) {
      Card c = from.get(i);
      to.add(c);
      from.remove(c);
      i -= 1;
    }
  }

}