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
public interface DirectedUnit extends Unit {
  public ArrayList<InputTerminal> getInputs();
  public default InputTerminal in(int i) {
    return getInputs().get(i);
  }
  public ArrayList<OutputTerminal> getOutputs();
  public default OutputTerminal out(int i) {
    return getOutputs().get(i);
  };
  public boolean isOrigin();
  public boolean isFinal();
  
  //TODO May be barking up the wrong tree.
  //TODO Also, might return list of connections changed, for (very effective?) optimizations.
  
  /**
   * It's of note that "tick", for a DirectedUnit, should be idempotent, given the same set of inputs.
   * Should work like "resolve".
   * @return 
   */
  @Override
  public HashSet<OutputTerminal> tick();
  public void doFinalState();
  default HashSet<OutputTerminal> collectChanged() {
    //TODO I'm not sure how to accomplish restarts.
    HashSet<OutputTerminal> changed = new HashSet<OutputTerminal>();
    for (OutputTerminal o : getOutputs()) {
      if (o.pullHasChanged()) {
        changed.add(o);
      }
    }
    return changed;
  }
}
