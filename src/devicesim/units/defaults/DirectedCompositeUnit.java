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
import devicesim.StateInputTerminal;
import devicesim.StateOutputTerminal;
import devicesim.Terminal;
import devicesim.Unit;
import java.io.IOException;
import java.util.HashSet;

/**
 *
 * @author erhannis
 */
public class DirectedCompositeUnit extends BlankDirectedUnit {
  private HashSet<DirectedUnit> origins = new HashSet<DirectedUnit>();
  private HashSet<DirectedUnit> manuals = new HashSet<DirectedUnit>();
  private HashSet<DirectedUnit> finals = new HashSet<DirectedUnit>();
  public HashSet<DirectedUnit> allUnits = new HashSet<DirectedUnit>();

  public InternalMetaUnit internalMetaUnit;
  
  public DirectedCompositeUnit(int inputCount, int outputCount) {
    for (int i = 0; i < inputCount; i++) {
      inputs.add(new StateInputTerminal(0.0, this));
    }
    for (int i = 0; i < outputCount; i++) {
      outputs.add(new StateOutputTerminal(0.0, this));
    }
    terminals.addAll(inputs);
    terminals.addAll(outputs);
    internalMetaUnit = addUnit(new InternalMetaUnit(inputs, outputs));
  }

  public DirectedCompositeUnit(int inputCount, int outputCount, OutputTerminal... inputs) {
    this(inputCount, outputCount);
    setInputs(inputs);
  }
  
  // Construction chains
  public DirectedCompositeUnit setInputs(OutputTerminal... inputs) {
    for (int i = 0; i < inputs.length; i++) {
      GDC.addConnection(inputs[i], in(i));
    }
    return this;
  }

  public DirectedCompositeUnit setOutputs(InputTerminal... outputs) {
    for (int i = 0; i < outputs.length; i++) {
      GDC.addConnection(out(i), outputs[i]);
    }
    return this;
  }

  // End construction chains
  @Override
  public DirectedCompositeUnit copy() throws IOException, ClassNotFoundException {
    return (DirectedCompositeUnit)super.copy(); //To change body of generated methods, choose Tools | Templates.
  }
  
  public InputTerminal iout(int i) {
    return internalMetaUnit.in(i);
  }

  public OutputTerminal iin(int i) {
    return internalMetaUnit.out(i);
  }
  
  public void resizeTerminals(int inputCount, int outputCount) {
    if (outputs.size() > outputCount) {
      while (outputs.size() > outputCount) {
        OutputTerminal ot = outputs.get(outputs.size() - 1);
        if (ot.getConnection() != null) {
          ot.getConnection().severConnection();
        }
        Terminal t = outputs.get(outputs.size() - 1);
        outputs.remove(t);
        terminals.remove(t);
      }
    } else if (outputs.size() < outputCount) {
      while (outputs.size() < outputCount) {
        OutputTerminal t = new StateOutputTerminal(0.0, this);
        outputs.add(t);
        terminals.add(t);
      }
    }
    if (inputs.size() > inputCount) {
      while (inputs.size() > inputCount) {
        InputTerminal it = inputs.get(inputs.size() - 1);
        if (it.getConnection() != null) {
          it.getConnection().removeOutput(it);
        }
        Terminal t = inputs.get(inputs.size() - 1);
        inputs.remove(t);
        terminals.remove(t);
      }
    } else if (inputs.size() < inputCount) {
      while (inputs.size() < inputCount) {
        InputTerminal t = new StateInputTerminal(0.0, this);
        inputs.add(t);
        terminals.add(t);
      }
    }
    internalMetaUnit.resizeTerminals(inputs, outputs);
  }

  @Override
  public void recalcView() {
    super.recalcView(); //To change body of generated methods, choose Tools | Templates.
    internalMetaUnit.recalcView();
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
    allUnits.add(unit);
    return checkOriginFinal(unit);
  }
  
  public void removeUnit(DirectedUnit unit) {
    if (unit == internalMetaUnit) {
      return;
    }
    for (InputTerminal it : unit.getInputs()) {
      it.breakConnection();
    }
    for (OutputTerminal ot : unit.getOutputs()) {
      ot.breakConnection();
    }
    allUnits.remove(unit);
    finals.remove(unit);
    origins.remove(unit);
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

  /**
   * Adds a unit to the list of units to be manually checked at the start of the next tick.
   * Good for switches and other things which are not sources, per se, but may change state without
   * prompting from inputs.
   * ...Maybe I should just make them sources.
   * @param du 
   */
  public void addManualCheck(DirectedUnit du) {
    manuals.add(du);
  }
  
  @Override
  public HashSet<OutputTerminal> tick() {
    HashSet<DirectedUnit> queued = new HashSet<DirectedUnit>();
    queued.addAll(origins);
    queued.addAll(manuals);
    manuals.clear();
    
    while (!queued.isEmpty()) {
      HashSet<DirectedUnit> nextQueued = new HashSet<DirectedUnit>();
      for (DirectedUnit u : queued) {
        HashSet<OutputTerminal> changed = u.tick();
        for (OutputTerminal ot : changed) {
          double value = ot.getValue();
          for (InputTerminal it : ot.getConnection().getOutputs()) {
            it.setValue(value);
            //TODO Optimization: have passive flag, which makes their sets not trigger updates down the line.
            //         Probably good for passive source.
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
  
  public HashSet<Unit> collectDownstreamUnits() {
    //TODO Technically misses the ones that aren't origins and aren't downstream of origins.
    HashSet<Unit> results = new HashSet<Unit>();
    HashSet<DirectedUnit> queued = new HashSet<DirectedUnit>();
    queued.addAll(origins);
    
    while (!queued.isEmpty()) {
      HashSet<DirectedUnit> nextQueued = new HashSet<DirectedUnit>();
      for (DirectedUnit du : queued) {
        for (OutputTerminal ot : du.getOutputs()) {
          if (ot.getConnection() == null) continue;
          for (InputTerminal it : ot.getConnection().getOutputs()) {
            if (!results.contains(it.getUnit())) {
              nextQueued.add(it.getUnit());
              results.add(it.getUnit());
            }
          }
        }
      }
      HashSet<DirectedUnit> bucket = queued;
      queued = nextQueued;
      nextQueued = bucket;
    }
    
    return results;
  }
}
