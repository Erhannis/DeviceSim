/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim;

import devicesim.GenericDirectedConnection.GDC;
import devicesim.units.defaults.AndGate;
import devicesim.units.defaults.DirectedCompositeUnit;
import devicesim.units.defaults.NandGate;
import devicesim.units.defaults.NotGate;
import devicesim.units.defaults.OrGate;
import devicesim.units.defaults.SinkSysout;
import devicesim.units.defaults.SinkSysoutBinary;
import devicesim.units.defaults.SourceHigh;
import devicesim.units.defaults.SourceLow;
import devicesim.units.defaults.StateSource;
import devicesim.units.defaults.XorGate;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Running list of conventions to follow.  See my collection of test methods for examples.
 * Connect things via GDC.connect or GDC.addConnection.
 * Make sure to use (DirectedCompositeUnit).addUnit on all units you want to be in the given unit, so it can check them for origin/final properties.
 * @author erhannis
 */
public class DeviceEngine {
  public static final int MODE_DIRECTED = 0;
  public static final int MODE_ITERATIVE = 1;

  /**
   * //TODO
   * I'm not sure how much of the engine I've actually written to be compatible with other modes.
   * Actually, now I'm not sure what this mode will even do, at all.
   */
  public int mode = MODE_DIRECTED;
  
  public ArrayList<Unit> unitTypes = new ArrayList<Unit>();
  
  public DeviceEngine() {
    init();
  }
  
  public void init() {
    unitTypes.add(new SourceHigh());
    unitTypes.add(new SourceLow());
    unitTypes.add(new AndGate());
    unitTypes.add(new SinkSysout());
  }
  
  /**
   * Test of AND gate, and my copy() function.
   * @throws ClassNotFoundException
   * @throws IOException 
   */
  public void testRun() throws ClassNotFoundException, IOException {
    DirectedCompositeUnit dcu = new DirectedCompositeUnit(0, 0);

    AndGate ag = dcu.addUnit(new AndGate());
    GDC.connect(dcu.addUnit(new SourceHigh()).out(0), ag.in(0));
    GDC.connect(dcu.addUnit(new SourceLow()).out(0), ag.in(1));
    StateSource a = dcu.addUnit(new StateSource(1.0));
    StateSource b = dcu.addUnit(new StateSource(1.0));
    GDC.connect(a.out(0), ag.in(2));
    GDC.connect(b.out(0), ag.in(3));
    GDC.connect(ag.out(0), dcu.addUnit(new SinkSysout()).in(0));
    
    Unit dcuCopy = dcu.copy();
    unitTypes.add(dcuCopy);
    dcu.tick();
    dcu.doFinalState();
    
    a.setValue(0.0);
    
    dcu.tick();
    dcu.doFinalState();
    dcu = (DirectedCompositeUnit)dcuCopy.copy();
    dcu.tick();
    dcu.doFinalState();
  }
  
  /**
   * Testing other gate types.
   * @throws IOException
   * @throws ClassNotFoundException 
   */
  public void testRun2ElectricBoogaloo() throws IOException, ClassNotFoundException {
    DirectedCompositeUnit dcu = new DirectedCompositeUnit(0, 0);

    SourceHigh high = dcu.addUnit(new SourceHigh());
    SourceLow low = dcu.addUnit(new SourceLow());
    
    AndGate ag = dcu.addUnit(new AndGate());
    GDC.addConnection(high.out(0), ag.in(0));
    GDC.addConnection(low.out(0), ag.in(1));
    OrGate og = dcu.addUnit(new OrGate());
    GDC.addConnection(high.out(0), og.in(0));
    GDC.addConnection(low.out(0), og.in(1));
    XorGate xg = dcu.addUnit(new XorGate());
    GDC.addConnection(high.out(0), xg.in(0));
    GDC.addConnection(low.out(0), xg.in(1));
    NandGate nandg = dcu.addUnit(new NandGate());
    GDC.addConnection(high.out(0), nandg.in(0));
    GDC.addConnection(low.out(0), nandg.in(1));
    NotGate ng = dcu.addUnit(new NotGate());
    GDC.addConnection(high.out(0), ng.in(0));
    GDC.addConnection(low.out(0), ng.in(1));

    StateSource a = dcu.addUnit(new StateSource(1.0));
    StateSource b = dcu.addUnit(new StateSource(1.0));
    GDC.addConnection(a.out(0), ag.in(2));
    GDC.addConnection(b.out(0), ag.in(3));
    GDC.addConnection(ag.out(0), dcu.addUnit(new SinkSysout().setName("AND ")).in(0));
    GDC.addConnection(a.out(0), og.in(2));
    GDC.addConnection(b.out(0), og.in(3));
    GDC.addConnection(og.out(0), dcu.addUnit(new SinkSysout().setName("OR  ")).in(0));
    GDC.addConnection(a.out(0), xg.in(2));
    GDC.addConnection(b.out(0), xg.in(3));
    GDC.addConnection(xg.out(0), dcu.addUnit(new SinkSysout().setName("XOR ")).in(0));
    GDC.addConnection(a.out(0), nandg.in(2));
    GDC.addConnection(b.out(0), nandg.in(3));
    GDC.addConnection(nandg.out(0), dcu.addUnit(new SinkSysout().setName("NAND")).in(0));
    GDC.addConnection(a.out(0), ng.in(2));
    GDC.addConnection(ng.out(0), dcu.addUnit(new SinkSysout().setName("NOT ")).in(0));
    
    Unit dcuCopy = dcu.copy();
    unitTypes.add(dcuCopy);
    for (double av = 0.0; av <= 1.0; av++) {
      for (double bv = 0.0; bv <= 1.0; bv++) {
        a.setValue(av);
        b.setValue(bv);
        
        System.out.println(a.getValue() + " " + b.getValue());
        dcu.tick();
        dcu.doFinalState();
      }
    }
  }

  public void testRun3() throws IOException, ClassNotFoundException {
    DirectedCompositeUnit dcu = new DirectedCompositeUnit(0, 0);

    SourceHigh high = dcu.addUnit(new SourceHigh());
    OutputTerminal highT = high.out(0);
    SourceLow low = dcu.addUnit(new SourceLow());
    OutputTerminal lowT = low.out(0);

    StateSource a = dcu.addUnit(new StateSource(1.0));
    OutputTerminal aT = a.out(0);
    StateSource b = dcu.addUnit(new StateSource(1.0));
    OutputTerminal bT = b.out(0);
    
    AndGate ag = dcu.addUnit(new AndGate(highT, lowT, aT, bT));
    XorGate xg = dcu.addUnit(new XorGate(highT, lowT, aT, bT));

    dcu.addUnit(new SinkSysoutBinary(highT, lowT, ag.out(0), xg.out(0)).setName("a+b"));
    
    Unit dcuCopy = dcu.copy();
    unitTypes.add(dcuCopy);
    for (double av = 0.0; av <= 1.0; av++) {
      for (double bv = 0.0; bv <= 1.0; bv++) {
        a.setValue(av);
        b.setValue(bv);
        
        System.out.println(a.getValue() + " " + b.getValue());
        dcu.tick();
        dcu.doFinalState();
      }
    }
  }
}
