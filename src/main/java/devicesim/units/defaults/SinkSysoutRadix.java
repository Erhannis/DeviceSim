/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim.units.defaults;

import devicesim.GenericDirectedConnection.GDC;
import devicesim.OutputTerminal;
import devicesim.StateInputTerminal;
import com.erhannis.mathnstuff.MeMath;
import com.erhannis.mathnstuff.MeUtils;
import java.math.BigInteger;
import java.util.HashSet;

/**
 *
 * @author erhannis
 */
public class SinkSysoutRadix extends BlankDirectedUnit {
  private int radix = 2;
  private boolean signed = false;
  
  public SinkSysoutRadix(int radix, boolean signed, int inputCount) {
    inputs.add(new StateInputTerminal(0, this)); // High source
    inputs.add(new StateInputTerminal(0, this)); // Low source
    // Haha, this seems sketchy
    for (int i = 0; i < inputCount; i++) {
      inputs.add(new StateInputTerminal(0, this));
    }
    terminals.addAll(inputs);
    terminals.addAll(outputs);
    this.radix = radix;
    this.signed = signed;
    setName("SysoutR" + radix + (signed ? "S" : "U") + "_" + inputCount);
  }

  public SinkSysoutRadix(OutputTerminal high, OutputTerminal low, OutputTerminal... inputs) {
    this(2, false, inputs.length);
    GDC.addConnection(high, in(0));
    GDC.addConnection(low, in(1));
    for (int i = 0; i < inputs.length; i++) {
      GDC.addConnection(inputs[i], in(2+i));
    }
  }
  
  public SinkSysoutRadix() {
    this(2, false, 1);
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
    if (signed && bi.testBit(inputs.size() - 3)) {
      for (int i = 0; i < inputs.size() - 2; i++) {
        bi = bi.flipBit(i);
      }
      bi = bi.add(BigInteger.ONE);
      bi = bi.negate();
    }
    String num = bi.toString(radix);
    if (radix == 2 && num.length() < (inputs.size() - 2)) {
      sb.append(MeUtils.multiplyString("0", (inputs.size() - 2) - num.length()));
    }
    sb.append(num);
    System.out.println(sb.toString());
  }

  @Override
  public HashSet<OutputTerminal> tick() {
    return collectChanged();
  }
}
