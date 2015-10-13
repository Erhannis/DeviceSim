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
public class SinkSysoutBinary extends BlankDirectedUnit {
  private String name;
  
  public SinkSysoutBinary(String name, int inputCount) {
    inputs.add(new StateInputTerminal(0, this)); // High source
    inputs.add(new StateInputTerminal(0, this)); // Low source
    // Haha, this seems sketchy
    for (int i = 0; i < inputCount; i++) {
      inputs.add(new StateInputTerminal(0, this));
    }
    terminals.addAll(inputs);
    terminals.addAll(outputs);
    this.name = name;
  }

  public SinkSysoutBinary(String name, OutputTerminal high, OutputTerminal low, OutputTerminal... inputs) {
    this(name, inputs.length);
    GDC.addConnection(high, in(0));
    GDC.addConnection(low, in(1));
    // Haha, this seems sketchy
    for (int i = 0; i < inputs.length; i++) {
      GDC.addConnection(inputs[i], in(2+i));
    }
  }

  public String getName() {
    return name;
  }
  
  // Dunno why I'm all about getters and setters at the moment.  Must be all these interfaces.
  public void setName(String name) {
    this.name = name;
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
    StringBuilder sb = new StringBuilder();
    if (name != null) {
      sb.append(name + " ");
    }
    double high = inputs.get(0).getValue();
    double low = inputs.get(1).getValue();
    for (int i = 2; i < inputs.size(); i++) {
      if (MeMath.nearerFirst(high, low, in(i).getValue())) {
        sb.append("1");
      } else {
        sb.append("0");
      }
    }
    System.out.println(sb.toString());
  }

  @Override
  public HashSet<OutputTerminal> tick() {
    return collectChanged();
  }
}
