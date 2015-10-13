/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim.units.defaults;

import devicesim.OutputTerminal;
import devicesim.StateInputTerminal;
import java.util.HashSet;

/**
 *
 * @author erhannis
 */
public class SinkSysout extends BlankDirectedUnit {
  public SinkSysout() {
    inputs.add(new StateInputTerminal(0.0, this));
    terminals.addAll(inputs);
  }

  @Override
  public boolean isOrigin() {
    return false;
  }

  @Override
  public boolean isFinal() {
    return true;
  }

  @Override
  public void doFinalState() {
    System.out.println(inputs.get(0).getValue());
  }
  
  @Override
  public HashSet<OutputTerminal> tick() {
    return collectChanged();
  }
}
