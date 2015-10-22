/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim.units.defaults;

import devicesim.GenericDirectedConnection.GDC;
import devicesim.InputTerminal;
import devicesim.InternalInputTerminal;
import devicesim.InternalOutputTerminal;
import devicesim.OutputTerminal;
import devicesim.Terminal;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Ow, ok, this explanation is as much for my benefit as it is for yours.
 * So, the thing here is that we need a way for DirectedCompositeUnits to
 * exchange signals with the outside world.  The easiest way I see to do so is
 * to pretend the outside world is, itself, another unit.  This is that unit.
 * @author erhannis
 */
public class InternalMetaUnit extends BlankDirectedUnit {
  public InternalMetaUnit(ArrayList<InputTerminal> inputs, ArrayList<OutputTerminal> outputs) {
    for (int i = 0; i < inputs.size(); i++) {
      this.outputs.add(new InternalInputTerminal(inputs.get(i), this));
    }
    for (int i = 0; i < outputs.size(); i++) {
      this.inputs.add(new InternalOutputTerminal(outputs.get(i), this));
    }
    terminals.addAll(this.inputs);
    terminals.addAll(this.outputs);
    setName("InternalMetaUnit");
    setViewWidth(-400);
    setViewHeight(800 / 3.0);
    setViewTop(80);
    setViewLeft(450);
  }

  /**
   * For use during unit editing.
   */
  public void resizeTerminals(ArrayList<InputTerminal> newInputs, ArrayList<OutputTerminal> newOutputs) {
    if (outputs.size() > newInputs.size()) {
      while (outputs.size() > newInputs.size()) {
        OutputTerminal ot = outputs.get(outputs.size() - 1);
        if (ot.getConnection() != null) {
          ot.getConnection().severConnection();
        }
        Terminal t = outputs.get(outputs.size() - 1);
        outputs.remove(t);
        terminals.remove(t);
      }
    } else if (outputs.size() < newInputs.size()) {
      while (outputs.size() < newInputs.size()) {
        OutputTerminal t = new InternalInputTerminal(newInputs.get(outputs.size()), this);
        outputs.add(t);
        terminals.add(t);
      }
    }
    if (inputs.size() > newOutputs.size()) {
      while (inputs.size() > newOutputs.size()) {
        InputTerminal it = inputs.get(inputs.size() - 1);
        if (it.getConnection() != null) {
          it.getConnection().removeOutput(it);
        }
        Terminal t = inputs.get(inputs.size() - 1);
        inputs.remove(t);
        terminals.remove(t);
      }
    } else if (inputs.size() < newOutputs.size()) {
      while (inputs.size() < newOutputs.size()) {
        InputTerminal t = new InternalOutputTerminal(newOutputs.get(inputs.size()), this);
        inputs.add(t);
        terminals.add(t);
      }
    }
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
