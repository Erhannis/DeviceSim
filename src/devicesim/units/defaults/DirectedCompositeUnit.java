/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim.units.defaults;

import devicesim.DirectedUnit;
import devicesim.GenericDirectedConnection.GDC;
import devicesim.InputTerminal;
import devicesim.OutputTerminal;
import devicesim.StateOutputTerminal;
import java.util.HashSet;

/**
 *
 * @author erhannis
 */
public class DirectedCompositeUnit extends BlankDirectedUnit {
  private HashSet<DirectedUnit> origins = new HashSet<DirectedUnit>();
  private HashSet<DirectedUnit> queued = new HashSet<DirectedUnit>();
  private HashSet<DirectedUnit> finals = new HashSet<DirectedUnit>();

  public DirectedCompositeUnit() {
    outputs.add(new StateOutputTerminal(1.0, this));
    terminals.addAll(inputs);
    terminals.addAll(outputs);
  }

  // Ooh, cool, that syntax worked.
  private <T extends DirectedUnit> T checkOriginFinal(T unit) {
    if (unit.isOrigin()) {
      origins.add(unit);
    }
    if (unit.isFinal()) {
      finals.add(unit);
    }
    return unit;
  }
  
  /**
   * This is effectively just checkOriginFinal, but named differently for peace
   * of mind.  See, a DCU doesn't actually track its children.  It stores its
   * sources, (and its finals), and the connections from the sources keep track
   * of the children.
   * This may change in the future.
   * @param <T>
   * @param unit
   * @return 
   */
  public <T extends DirectedUnit> T addUnit(T unit) {
    return checkOriginFinal(unit);
  }
  
  private StateSource ssA;
  private StateSource ssB;
  
  public void initTest() {
    AndGate ag = checkOriginFinal(new AndGate());
    GDC.connect(checkOriginFinal(new SourceHigh()).out(0), ag.in(0));
    GDC.connect(checkOriginFinal(new SourceLow()).out(0), ag.in(1));
    StateSource a = checkOriginFinal(new StateSource(1.0));
    StateSource b = checkOriginFinal(new StateSource(1.0));
    ssA = a;
    ssB = b;
    GDC.connect(a.out(0), ag.in(2));
    GDC.connect(b.out(0), ag.in(3));
    GDC.connect(ag.out(0), checkOriginFinal(new SinkSysout()).in(0));
  }

  public void testRun() {
    tick();
    doFinalState();
  }
  
  public void changeTest() {
    ssA.setValue(0.0);
  }
  
  @Override
  public boolean isOrigin() {
    return false;
  }

  /**
   * Note that because of child finals, you must call checkOriginFinal on
   * DirectedCompositeUnits AFTER adding all their children.
   * @return 
   */
  @Override
  public boolean isFinal() {
    return !finals.isEmpty();
  }

  @Override
  public void doFinalState() {
    for (DirectedUnit du : finals) {
      du.doFinalState();
    }
  }

  @Override
  public HashSet<OutputTerminal> tick() {
    queued.clear();
    queued.addAll(origins);
    
    while (!queued.isEmpty()) {
      HashSet<DirectedUnit> nextQueued = new HashSet<DirectedUnit>();
      for (DirectedUnit u : queued) {
        HashSet<OutputTerminal> changed = u.tick();
        for (OutputTerminal ot : changed) {
          double value = ot.getValue();
          for (InputTerminal it : ot.getConnection().getOutputs()) {
            it.setValue(value);
            nextQueued.add(it.getUnit());
          }
        }
      }
      HashSet<DirectedUnit> bucket = queued;
      queued = nextQueued;
      nextQueued = bucket;
    }
    
    return collectChanged();
  }
}
