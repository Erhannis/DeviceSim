/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim.units.defaults;

import devicesim.OutputTerminal;
import devicesim.StateOutputTerminal;
import java.util.HashSet;

/**
 *
 * @author erhannis
 */
public class StateSource extends BlankDirectedUnit {
  public StateSource(double value) {
    outputs.add(new StateOutputTerminal(value, this));
    terminals.addAll(outputs);
  }
  
  public double getValue() {
    return outputs.get(0).getValue();
  }
  
  public void setValue(double value) {
    outputs.get(0).setValue(value);
  }

  @Override
  public boolean isOrigin() {
    return true;
  }

  @Override
  public HashSet<OutputTerminal> tick() {
    return collectChanged();
  }
}
