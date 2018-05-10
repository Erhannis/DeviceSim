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
import com.erhannis.mathnstuff.MeMath;
import java.util.HashSet;

/**
 *
 * @author erhannis
 */
public class OrGate extends BlankDirectedUnit {
  public OrGate() {
    inputs.add(new StateInputTerminal(0, this)); // High source
    inputs.add(new StateInputTerminal(0, this)); // Low source
    inputs.add(new StateInputTerminal(0, this));
    inputs.add(new StateInputTerminal(0, this));
    outputs.add(new StateOutputTerminal(0, this));
    terminals.addAll(inputs);
    terminals.addAll(outputs);
    setName("OR");
  }

  public OrGate(OutputTerminal high, OutputTerminal low, OutputTerminal a, OutputTerminal b) {
    this();
    GDC.addConnection(high, in(0));
    GDC.addConnection(low, in(1));
    GDC.addConnection(a, in(2));
    GDC.addConnection(b, in(3));
  }
  
  @Override
  public boolean isOrigin() {
    return false;
  }

  private void doUpdate() {
    if (MeMath.nearerFirst(inputs.get(0).getValue(), inputs.get(1).getValue(), inputs.get(2).getValue())
     || MeMath.nearerFirst(inputs.get(0).getValue(), inputs.get(1).getValue(), inputs.get(3).getValue())) {
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
