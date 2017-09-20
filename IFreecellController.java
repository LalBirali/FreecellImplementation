package cs3500.hw03;

import java.util.List;

import cs3500.hw02.FreecellOperations;

/**
 * Interface of the Freecell controller. It is parameterized over the type of Card you create.
 */
public interface IFreecellController<K> {

  /**
   * Sets stage for the game, and starts the game based on the contraints of the instance of
   * a FreecellOperations given. Gameplay is then initiated based on the Readable object provided.
   * @param deck is a deck of Cards
   * @param model is the type of Freecell game
   * @param numCascades number of cascade piles to start
   * @param numOpens number of open piles to start
   * @param shuffle is whether or not the deck needs to be shuffled
   * @throws IllegalArgumentException if controller has not been properly initialized
   */
  void playGame(List<K> deck, FreecellOperations<K> model, int numCascades,
                int numOpens, boolean shuffle) throws IllegalArgumentException;
}
