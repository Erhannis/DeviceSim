/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim.units.defaults;

import devicesim.GenericDirectedConnection.GDC;
import devicesim.OutputTerminal;
import devicesim.StateInputTerminal;
import devicesim.StateOutputTerminal;
import java.util.HashSet;
import mathnstuff.MeMath;

/**
 *
 * @author erhannis
 */
public class NotGate extends BlankDirectedUnit {
  public NotGate() {
    inputs.add(new StateInputTerminal(0, this)); // High source
    inputs.add(new StateInputTerminal(0, this)); // Low source
    inputs.add(new StateInputTerminal(0, this));
    outputs.add(new StateOutputTerminal(0, this));
    terminals.addAll(inputs);
    terminals.addAll(outputs);
    setName("NOT");
  }

  public NotGate(OutputTerminal high, OutputTerminal low, OutputTerminal a) {
    this();
    GDC.addConnection(high, in(0));
    GDC.addConnection(low, in(1));
    GDC.addConnection(a, in(2));
  }
  
  @Override
  public boolean isOrigin() {
    return false;
  }

  private void doUpdate() {
    if (!MeMath.nearerFirst(inputs.get(0).getValue(), inputs.get(1).getValue(), inputs.get(2).getValue())) {
      outputs.get(0).setValue(inputs.get(0).getValue());
    } else {
      outputs.get(0).setValue(inputs.get(1).getValue());
    }
  }
  
  @Override
  public HashSet<OutputTerminal> tick() {
    doUpdate();
    return collectChanged();
  }
}
