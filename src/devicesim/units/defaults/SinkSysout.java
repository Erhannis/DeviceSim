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
  private String name;
  
  public SinkSysout(String name) {
    this();
    this.name = name;
  }
  
  public SinkSysout() {
    inputs.add(new StateInputTerminal(0.0, this));
    terminals.addAll(inputs);
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
    if (name != null) {
      System.out.println(name + " " + inputs.get(0).getValue());
    } else {
      System.out.println(inputs.get(0).getValue());
    }
  }
  
  @Override
  public HashSet<OutputTerminal> tick() {
    return collectChanged();
  }
}
