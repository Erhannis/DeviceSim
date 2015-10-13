/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim;

import java.util.ArrayList;
import java.util.Arrays;

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
  private ArrayList<InputTerminal> outputs;
  private ArrayList<Terminal> terminals;
  
  private GenericDirectedConnection(OutputTerminal input, InputTerminal... outputs) {
    this.input = input;
    this.outputs = new ArrayList<InputTerminal>(Arrays.asList(outputs));
    terminals = new ArrayList<Terminal>();
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
  
  @Override
  public OutputTerminal getInput() {
    return input;
  }

  /**
   * Please don't modify the list.
   * @return 
   */
  @Override
  public ArrayList<InputTerminal> getOutputs() {
    return outputs;
  }

  /**
   * Please don't modify the list.
   * @return 
   */
  @Override
  public ArrayList<Terminal> getTerminals() {
    return terminals;
  }
}
