/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim.units.defaults;

import devicesim.StateInputTerminal;
import devicesim.StateOutputTerminal;

/**
 *
 * @author erhannis
 */
public class AndGate extends BlankDirectedUnit {
  public AndGate() {
    inputs.add(new StateInputTerminal(0, this)); // High source
    inputs.add(new StateInputTerminal(0, this)); // Low source
    inputs.add(new StateInputTerminal(0, this));
    inputs.add(new StateInputTerminal(0, this));
    outputs.add(new StateOutputTerminal(0, this));
    terminals.addAll(inputs);
    terminals.addAll(outputs);
  }

  @Override
  public boolean isOrigin() {
    return false;
  }

  @Override
  public void resolve() {
    
  }
}
