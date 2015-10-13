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
  private String name;

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

  @Override
  public String getName() {
    return name;
  }
  
  // Dunno why I'm all about getters and setters at the moment.  Must be all these interfaces.
  @Override
  public BlankDirectedUnit setName(String name) {
    this.name = name;
    return this; // For construction chains
  }
  
  // Most things won't usually need to implement this, so I'm providing a blank default.
  @Override
  public void doFinalState() {
  }
  
  // Same here
  @Override
  public boolean isFinal() {
    return false;
  }

  @Override
  public String toString() {
    String name = getName();
    if (name != null) {
      return name;
    } else {
      return super.toString();
    }
  }
}
