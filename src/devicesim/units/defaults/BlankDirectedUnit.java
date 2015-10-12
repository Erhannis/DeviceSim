/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim.units.defaults;

import devicesim.DirectedUnit;
import devicesim.InputTerminal;
import devicesim.OutputTerminal;
import devicesim.Terminal;
import java.util.ArrayList;

/**
 *
 * @author erhannis
 */
public abstract class BlankDirectedUnit implements DirectedUnit {
  protected ArrayList<Terminal> terminals = new ArrayList<Terminal>();
  protected ArrayList<InputTerminal> inputs = new ArrayList<InputTerminal>();
  protected ArrayList<OutputTerminal> outputs = new ArrayList<OutputTerminal>();
  
  @Override
  public ArrayList<Terminal> getTerminals() {
    //TODO Maybe ought not to return the actual object
    return terminals;
  }

  @Override
  public ArrayList<InputTerminal> getInputs() {
    return inputs;
  }

  @Override
  public ArrayList<OutputTerminal> getOutputs() {
    return outputs;
  }
}
