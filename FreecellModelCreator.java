package cs3500.hw04;

import cs3500.hw02.FreecellModel;

/**
 * Factory class to create FreecellModel games based on GameType.
 */
public class FreecellModelCreator {

  /**
   * Enumerations to represent different gametypes.
   */
  public enum GameType {
    SINGLEMOVE, MULTIMOVE
  }

  /**
   * Returns specific FreecellModel object based on given gametype.
   * NOTE: will be either singlemove Freecell or multimove Freecell.
   * @param type is the game type
   * @return The required FreecellModel based on gameplay choice.
   */
  public static FreecellModel create(GameType type) {
    switch (type) {
      case MULTIMOVE: return new MFreecellModel();
      case SINGLEMOVE: return new FreecellModel();
      default: return null;
    }
  }

}
