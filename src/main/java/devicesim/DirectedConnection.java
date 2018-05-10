/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim;

import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author erhannis
 */
public interface DirectedConnection extends Connection {
  /**
   * Note that this is the input TO THE CONNECTION, so it is, itself, an output.
   * @return 
   */
  public OutputTerminal getInput();

  /**
   * Note that these are the outputs FROM THE CONNECTION, so they are, themselves, outputs.
   * @return 
   */
  public HashSet<InputTerminal> getOutputs();
  
  /**
   * Probably a bad idea to try this while the circuit is running.  Might glitch "hasChanged"s, maybe.
   */
  public default void severConnection() {
    getInput().setConnection(null);
    for (InputTerminal t : getOutputs()) {
      t.setConnection(null);
    }
  }
  
  /**
   * This probably isn't GUARANTEED to work, if getOutputs or getTerminals returns a copy or something.
   * @param output 
   */
  public default void removeOutput(InputTerminal output) {
    getOutputs().remove(output);
    getTerminals().remove(output);
    output.setConnection(null);
  }
  
  /**
   * This probably isn't GUARANTEED to work, if getOutputs or getTerminals returns a copy or something.
   * @param output 
   */
  public default void replaceOutput(InputTerminal outA, InputTerminal outB) {
    getOutputs().remove(outA);
    getTerminals().remove(outA);
    outA.breakConnection();
    getOutputs().add(outB);
    getTerminals().add(outB);
    outB.breakConnection();
    outB.setConnection(this);
  }

  /**
   * This probably isn't GUARANTEED to work, if getOutputs or getTerminals returns a copy or something.
   * @param output 
   */
  public void replaceInput(OutputTerminal newIn);
}
