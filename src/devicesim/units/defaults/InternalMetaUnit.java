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
    terminals.addAll(inputs);
    terminals.addAll(outputs);
    setName("InternalMetaUnit");
    setViewWidth(-300);
    setViewHeight(200);
  }

  /**
   * For use during unit editing.
   */
  public void resizeTerminals(ArrayList<InputTerminal> newInputs, ArrayList<OutputTerminal> newOutputs) {
    if (outputs.size() > newInputs.size()) {
      while (outputs.size() > newInputs.size()) {
        OutputTerminal ot = outputs.get(outputs.size() - 1);
        ot.getConnection().severConnection();
        outputs.remove(outputs.size() - 1);
      }
    } else if (outputs.size() < newInputs.size()) {
      while (outputs.size() < newInputs.size()) {
        outputs.add(new InternalInputTerminal(newInputs.get(outputs.size()), this));
      }
    }
    if (inputs.size() > newOutputs.size()) {
      while (inputs.size() > newOutputs.size()) {
        InputTerminal it = inputs.get(inputs.size() - 1);
        it.getConnection().removeOutput(it);
        inputs.remove(inputs.size() - 1);
      }
    } else if (inputs.size() < newOutputs.size()) {
      while (inputs.size() < newOutputs.size()) {
        inputs.add(new InternalOutputTerminal(newOutputs.get(inputs.size()), this));
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
