/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim.units.defaults;

import devicesim.DirectedUnit;
import devicesim.InputTerminal;
import devicesim.OutputTerminal;
import devicesim.StateOutputTerminal;
import devicesim.Terminal;
import devicesim.Unit;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author erhannis
 */
public class SinkSysout extends BlankDirectedUnit {
  public SinkSysout() {
    outputs.add(new StateOutputTerminal(0.0, this));
    terminals.addAll(outputs);
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
    System.out.println(outputs.get(0).getValue());
  }
  
  @Override
  public HashSet<OutputTerminal> tick() {
    return collectChanged();
  }
}
