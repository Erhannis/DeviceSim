/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim.units.defaults;

import devicesim.OutputTerminal;
import devicesim.StateOutputTerminal;
import java.util.HashSet;

/**
 *
 * @author erhannis
 */
public class SourceHigh extends BlankDirectedUnit {
  public SourceHigh() {
    outputs.add(new StateOutputTerminal(1.0, this));
    terminals.addAll(outputs);
    setName("HIGH");
  }

  @Override
  public boolean isOrigin() {
    return true;
  }

  @Override
  public HashSet<OutputTerminal> tick() {
    return collectChanged();
  }
}
