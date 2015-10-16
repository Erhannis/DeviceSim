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
public class SwitchMetaGate extends BlankDirectedUnit implements Runnable {
  private boolean on = false;
  
  public SwitchMetaGate() {
    inputs.add(new StateInputTerminal(0, this)); // High source
    inputs.add(new StateInputTerminal(0, this)); // Low source
    outputs.add(new StateOutputTerminal(0, this));
    terminals.addAll(inputs);
    terminals.addAll(outputs);
    setName("Switch");
  }

  public SwitchMetaGate(OutputTerminal high, OutputTerminal low) {
    this();
    GDC.addConnection(high, in(0));
    GDC.addConnection(low, in(1));
  }
  
  @Override
  public boolean isOrigin() {
    return false;
  }

  private void doUpdate() {
    if (on) {
      out(0).setValue(in(0).getValue());
    } else {
      out(0).setValue(in(1).getValue());
    }
  }
  
  @Override
  public HashSet<OutputTerminal> tick() {
    doUpdate();
    return collectChanged();
  }

  @Override
  public void run() {
    on = !on;
  }
}
