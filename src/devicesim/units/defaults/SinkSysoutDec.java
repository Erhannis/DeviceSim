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
import java.math.BigInteger;
import java.util.HashSet;
import mathnstuff.MeMath;

/**
 *
 * @author erhannis
 */
public class SinkSysoutDec extends BlankDirectedUnit {
  public SinkSysoutDec(int inputCount) {
    inputs.add(new StateInputTerminal(0, this)); // High source
    inputs.add(new StateInputTerminal(0, this)); // Low source
    // Haha, this seems sketchy
    for (int i = 0; i < inputCount; i++) {
      inputs.add(new StateInputTerminal(0, this));
    }
    terminals.addAll(inputs);
    terminals.addAll(outputs);
    setName("SysoutDec" + inputCount);
  }

  public SinkSysoutDec(OutputTerminal high, OutputTerminal low, OutputTerminal... inputs) {
    this(inputs.length);
    GDC.addConnection(high, in(0));
    GDC.addConnection(low, in(1));
    for (int i = 0; i < inputs.length; i++) {
      GDC.addConnection(inputs[i], in(2+i));
    }
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
    if (getName() != null) {
      sb.append(getName() + " ");
    }
    double high = inputs.get(0).getValue();
    double low = inputs.get(1).getValue();
    BigInteger bi = BigInteger.valueOf(0);
    for (int i = 2; i < inputs.size(); i++) {
      bi = bi.shiftLeft(1);
      if (MeMath.nearerFirst(high, low, in(i).getValue())) {
        bi = bi.add(BigInteger.ONE);
      } else {
      }
    }
    sb.append(bi.toString(10));
    System.out.println(sb.toString());
  }

  @Override
  public HashSet<OutputTerminal> tick() {
    return collectChanged();
  }
}
