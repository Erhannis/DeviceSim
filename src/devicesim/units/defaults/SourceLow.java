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

/**
 *
 * @author erhannis
 */
public class SourceLow extends BlankDirectedUnit {
  public SourceLow() {
    outputs.add(new StateOutputTerminal(0.0, this));
    terminals.addAll(outputs);
  }

  @Override
  public boolean isOrigin() {
    return true;
  }

  @Override
  public void resolve() {
  }
}
