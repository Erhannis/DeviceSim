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

/**
 *
 * @author erhannis
 */
public class ClockMetaGate extends BlankDirectedUnit {
  private boolean on = false;
  public long cycleLength = 1;
  private long countdown = 1;
  
  public ClockMetaGate(long cycleLength, long countdown) {
    inputs.add(new StateInputTerminal(0, this)); // High source
    inputs.add(new StateInputTerminal(0, this)); // Low source
    outputs.add(new StateOutputTerminal(0, this));
    terminals.addAll(inputs);
    terminals.addAll(outputs);
    this.cycleLength = cycleLength;
    this.countdown = countdown;
    setName("CLOCK" + cycleLength);
  }

  public ClockMetaGate(OutputTerminal high, OutputTerminal low) {
    this(1, 1);
    GDC.addConnection(high, in(0));
    GDC.addConnection(low, in(1));
  }
  
  public ClockMetaGate() {
    this(1, 1);
  }
  
  @Override
  public boolean isOrigin() {
    return true;
  }

  @Override
  public boolean isFinal() {
    return true;
  }

  @Override
  public void doFinalState() {
    countdown--;
    if (countdown <= 0) {
      on = !on;
      countdown = cycleLength;
    }
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
}
