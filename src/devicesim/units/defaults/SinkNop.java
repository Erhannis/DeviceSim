/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim.units.defaults;

import devicesim.GenericDirectedConnection.GDC;
import devicesim.OutputTerminal;
import devicesim.StateInputTerminal;
import java.util.HashSet;
import mathnstuff.MeMath;

/**
 *
 * @author erhannis
 */
public class SinkNop extends BlankDirectedUnit {
  public SinkNop() {
    inputs.add(new StateInputTerminal(0, this));
    terminals.addAll(inputs);
    terminals.addAll(outputs);
    setName("NOP");
  }

  public SinkNop(OutputTerminal a) {
    this();
    GDC.addConnection(a, in(0));
  }
  
  @Override
  public boolean isOrigin() {
    return false;
  }

  @Override
  public HashSet<OutputTerminal> tick() {
    return collectChanged();
  }
}
