/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 *
 * @author erhannis
 */
public class GenericDirectedConnection implements DirectedConnection {
  /**
   * Just a convenience class;
   */
  public static class GDC extends GenericDirectedConnection {
    private GDC(OutputTerminal input, InputTerminal... outputs) {
      super(input, outputs);
    }  
  }
  
  private OutputTerminal input;
  private HashSet<InputTerminal> outputs;
  private HashSet<Terminal> terminals;
  
  private GenericDirectedConnection(OutputTerminal input, InputTerminal... outputs) {
    this.input = input;
    this.outputs = new HashSet<InputTerminal>(Arrays.asList(outputs));
    terminals = new HashSet<Terminal>();
    terminals.add(this.input);
    terminals.addAll(this.outputs);
    this.input.setConnection(this);
    for (InputTerminal it : this.outputs) {
      it.setConnection(this);
    }
  }
  
  public static GenericDirectedConnection connect(OutputTerminal input, InputTerminal... outputs) {
    return new GenericDirectedConnection(input, outputs);
  }
  
  /**
   * //TODO Quite frankly, I'm not convinced this won't break stuff.
   * But I don't see anything obvious, so like, just don't use it after starting
   * to run your circuit, and it should be fine.  Maybe even otherwise.
   * @param input
   * @param outputs
   * @return 
   */
  public static GenericDirectedConnection addConnection(OutputTerminal input, InputTerminal... outputs) {
    DirectedConnection existing = input.getConnection();
    if (existing == null) {
      return new GenericDirectedConnection(input, outputs);
    } else if (!(existing instanceof GenericDirectedConnection)) {
      //TODO I think this should work, but I probably won't be testing it anytime soon.
      existing = new GenericDirectedConnection(input, existing.getOutputs().toArray(new InputTerminal[]{}));
    }
    HashSet<InputTerminal> its = existing.getOutputs();
    its.addAll(Arrays.asList(outputs));
    return (GenericDirectedConnection)existing;
  }
  
  @Override
  public OutputTerminal getInput() {
    return input;
  }

  /**
   * Please don't modify the list.  ...Even though I did, I guess.
   * @return 
   */
  @Override
  public HashSet<InputTerminal> getOutputs() {
    return outputs;
  }

  /**
   * Please don't modify the list.  ...Even though I did, I guess.
   * @return 
   */
  @Override
  public HashSet<Terminal> getTerminals() {
    return terminals;
  }
}
