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
import devicesim.Unit;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashSet;

/**
 *
 * @author erhannis
 */
public abstract class RandomMetaGate extends BlankDirectedUnit {
  boolean on = false;
  
  public RandomMetaGate() {
    inputs.add(new StateInputTerminal(0, this)); // High source
    inputs.add(new StateInputTerminal(0, this)); // Low source
    outputs.add(new StateOutputTerminal(0, this));
    terminals.addAll(inputs);
    terminals.addAll(outputs);
    on = getRand();
    setName("RAND");
  }

  public RandomMetaGate(OutputTerminal high, OutputTerminal low) {
    this();
    GDC.addConnection(high, in(0));
    GDC.addConnection(low, in(1));
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
    on = getRand();
  }

  abstract boolean getRand();
  
  void doUpdate() {
    if (on) {
      out(0).setValue(in(0).getValue());
    } else {
      out(0).setValue(in(1).getValue());
    }
  }

  @Override
  public Unit copy() throws IOException, ClassNotFoundException {
    //TODO Don't want RANDs to start all 0, but this will interfere with state saving.  As will the current RNG variable, I suppose.
    RandomMetaGate copy = (RandomMetaGate)super.copy(); //To change body of generated methods, choose Tools | Templates.
    copy.on = copy.getRand();
    return copy;
  }
  
  @Override
  public HashSet<OutputTerminal> tick() {
    doUpdate();
    return collectChanged();
  }
}
