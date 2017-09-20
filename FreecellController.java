package cs3500.hw03;


import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import cs3500.hw02.Card;
import cs3500.hw02.FreecellOperations;
import cs3500.hw02.PileType;

/**
 * Represents FreecellController.
 */
public class FreecellController implements IFreecellController<Card> {
  private Appendable app;
  private Readable read;

  /**
   * Constructs a new FreecellController.
   * NOTE: initialise
   * @param read will handle the reading of user input
   * @param app will handle the transmitting of output, based on user input
   * @throws IllegalStateException if inputs have not been initialized
   */
  public FreecellController(Readable read, Appendable app) {
    if (app == null || read == null) {
      throw new IllegalStateException("inputs have not been initialized.");
    }
    this.app = app;
    this.read = read;
  }

  @Override
  public void playGame(List<Card> deck, FreecellOperations<Card> model, int numCascades,
                       int numOpens, boolean shuffle) {
    if (deck == null || model == null) {
      throw new IllegalArgumentException("parameters have not been properly initialized");
    }
    try {
      model.startGame(deck, numCascades, numOpens, shuffle);
    } catch (IllegalArgumentException e) {
      printToApp(this.app, "Could not start game.");
      System.out.println(this.app);
    }
    Scanner sc = new Scanner(read);
    printToApp(this.app, model.getGameState());
    //System.out.println(this.app);
    String temp1;
    String temp2 = "";
    String temp3 = "";
    //gameplay
    while (!model.isGameOver() && sc.hasNext()) {
      temp1 = sc.next();
      if (sc.hasNext()) {
        temp2 = sc.next();
      }
      if (sc.hasNext()) {
        temp3 = sc.next();
      }
      String from = validMove(temp1, 1, "source");
      if (from.indexOf("quit") >= 0) {
        printToApp(this.app, "\n" + from);
        //System.out.println(this.app);
        break;
      } else if (from.indexOf("Invalid") >= 0) {
        printToApp(this.app, "\n" + from);
        //System.out.println(this.app);
      }

      String index = validMove(temp2, 2, "card index");
      if (index.indexOf("quit") >= 0) {
        printToApp(this.app, "\n" + index);
        //System.out.println(this.app);
        break;
      } else if (index.indexOf("Invalid") >= 0) {
        printToApp(this.app, "\n" + index);
        //System.out.println(this.app);
      }
      String to = validMove(temp3, 1, "destination");
      if (to.indexOf("quit") >= 0) {
        printToApp(this.app, "\n" + to);
        //System.out.println(this.app);
        break;
      } else if (to.indexOf("Invalid") >= 0) {
        printToApp(this.app, "\n" + to);
        //System.out.println(this.app);
      }

      try {
        makeMove(from, index, to, model);
        printToApp(this.app, "\n" + "\n" + model.getGameState());
        //System.out.println("\n" + this.app);
      } catch (IllegalArgumentException e) {
        printToApp(this.app, "\n" + "Invalid move. Try again." + " " + e.getMessage());
        System.out.println(this.app);
      }
    }
    if (model.isGameOver()) {
      printToApp(this.app, "\n" + "Game over.");
      System.out.println(this.app);
    }
  }

  /**
   * Makes the model in play game, execute the move.
   * @param from is the user inputted source pile
   * @param where is the card index in the form of the string
   * @param to is the user inputted destination pile
   * @param model is what needs to perform the move
   */
  private void makeMove(String from, String where, String to, FreecellOperations<Card> model) {
    String sourcePile = from.substring(0, 1);
    PileType p1 = whichPile(sourcePile);
    int sourcePileNumber = Integer.parseInt(from.substring(1));
    int cardIndex = Integer.parseInt(where);
    String destPile = to.substring(0, 1);
    PileType p2 = whichPile(destPile);
    int destPileNumber  = Integer.parseInt(to.substring(1));
    model.move(p1, sourcePileNumber - 1, cardIndex - 1, p2,
            destPileNumber - 1);
  }

  /**
   * Determines which PileType needs to be returned based on the input.
   * INVARIANT: Anything passed into this method will be either "F" "O" "C" because of
   * @param from is the string representation of the PileType required
   * @return the required PileType
   */
  private PileType whichPile(String from) {
    switch (from) {
      case "F": return PileType.FOUNDATION;
      case "O": return PileType.OPEN;
      case "C": return PileType.CASCADE;
      default: return null;
    }
  }

  /**
   * Appends string to given appendable object.
   * @param app is what is appending the given string
   * @param str given string to be appended
   */
  private void printToApp(Appendable app, String str) {
    try {
      app.append(str);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Determines the next string to be written to appendable object, based on the user input.
   * INVARIANT: move will only be 1 or 2 based on usage. Where 2 1 represents a "C1" move, and 2
   * represents card index.
   * Part will only be either "card index", "source", or "destination"
   * @param input is
   * @param move is either 1 or 2, move determinant
   * @param part is the part of the move identifier
   * @return the next string to be displayed
   */
  private String validMove(String input, int move, String part) {
    if (input.equals("")) {
      return input;
    }
    switch (move) {
      case 1:
        //very minor edit, allows for input strings larger than 2, allows for "C12", not just "C1"
        if (input.length() >= 2) {
          if (input.substring(0,1).equals("F") ||
                  input.substring(0,1).equals("C") ||
                  input.substring(0,1).equals("O") &&
                          isInteger(input.substring(1))) {
            return input;
          } else {
            if (input.indexOf("q") >= 0 || input.indexOf("Q") >= 0) {
              return "Game quit prematurely.";
            }
            return "Invalid " + part + " pile. Try again.";
          }
        } else {
          if (input.indexOf("Q") >= 0 || input.indexOf("q") >= 0) {
            return "Game quit prematurely.";
          }
          return "Invalid " + part + " pile. Try again.";
        }
      case 2:
        if (input.length() >= 1) {
          if (isInteger(input)) {
            return input;
          } else {
            if (input.equalsIgnoreCase("q")) {
              return "Game quit prematurely.";
            } else {
              return "Invalid " + part + ". Try again";
            }
          }
        } else {
          if (input.indexOf("Q") >= 0 || input.indexOf("q") >= 0) {
            return "Game quit prematurely.";
          }
          return "Invalid " + part + ". Try again";
        }
      default:
        return "";
    }
  }

  /**
   * Determines if the string represents an integer.
   * @param s is a string of length one
   * @return if the string represents an integer
   */
  private boolean isInteger(String s) {
    try {
      Integer.parseInt(s);
      return true;

    } catch (NumberFormatException e) {
      return false;
    }
  }
}