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
import devicesim.units.defaults.SinkNop;
import devicesim.units.defaults.SinkSysout;
import devicesim.units.defaults.SinkSysoutBinary;
import devicesim.units.defaults.SourceHigh;
import devicesim.units.defaults.SourceLow;
import devicesim.units.defaults.StateSource;
import devicesim.units.defaults.SwitchMetaGate;
import devicesim.units.defaults.XorGate;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

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

  public static Unit createAdd2() {
    DirectedCompositeUnit add = new DirectedCompositeUnit(4, 2);
    add.setName("ADD2");

    AndGate ag = add.addUnit(new AndGate(add.iin(0), add.iin(1), add.iin(2), add.iin(3)));
    XorGate xg = add.addUnit(new XorGate(add.iin(0), add.iin(1), add.iin(2), add.iin(3)));
    GDC.addConnection(ag.out(0), add.iout(0));
    GDC.addConnection(xg.out(0), add.iout(1));
    
    try {
      return add.copy();
    } catch (IOException ex) {
      Logger.getLogger(DeviceEngine.class.getName()).log(Level.SEVERE, null, ex);
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(DeviceEngine.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }
  
  public static final Unit[] UNIT_ARCHETYPES = {
    new SourceHigh(),
    new SourceLow(),
    new AndGate(),
    new OrGate(),
    new XorGate(),
    new NandGate(),
    new NotGate(),
    new SinkNop(),
    createAdd2(),
    new SinkSysout(),
    new SinkSysoutBinary(8),
    new SwitchMetaGate()
  };
  
  public void init() {
    for (Unit archetype : UNIT_ARCHETYPES) {
      unitTypes.add(archetype);
    }
  }
  
  
  public static final int FILE_VERSION = 1;

  public static void saveObjectToFile(Object o, File f) {
    ObjectOutputStream oos = null;
    try {
      oos = new ObjectOutputStream(new FileOutputStream(f));
      switch (FILE_VERSION) {
        case 1:
          oos.writeInt(FILE_VERSION);
          oos.writeObject(o);
          break;
      }
      oos.flush();
    } catch (IOException ex) {
      Logger.getLogger(DeviceEngine.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      try {
        oos.close();
      } catch (IOException ex) {
        Logger.getLogger(DeviceEngine.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }
  
  public static Object readFile(File f) {
    Object result = null;
    ObjectInputStream ois = null;
    try {
      ois = new ObjectInputStream(new FileInputStream(f));
      int fileVersion = ois.readInt();
      // I don't actually know how to deal with opening old versions with class differences.
      switch (fileVersion) {
        case 1:
          result = ois.readObject();
          break;
        default:
          System.err.println("Invalid version: " + fileVersion);
          break;
      }
    } catch (FileNotFoundException ex) {
      Logger.getLogger(DeviceEngine.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(DeviceEngine.class.getName()).log(Level.SEVERE, null, ex);
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(DeviceEngine.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      try {
        ois.close();
      } catch (IOException ex) {
        Logger.getLogger(DeviceEngine.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return result;
  }
  
  public static void validateUnit(Unit unit) throws Exception {
    //TODO Rechecking, say, the source connection repeatedly could be slow.  Cache that, too.
    HashSet<Unit> toCheck = new HashSet<Unit>();
    HashSet<Unit> checked = new HashSet<Unit>();
    toCheck.add(unit);
    while (!toCheck.isEmpty()) {
      Unit u = toCheck.iterator().next();
      for (Terminal t : u.getTerminals()) {
        if (t.getConnection() != null) {
          for (Terminal t2 : t.getConnection().getTerminals()) {
            if (!checked.contains(t2.getUnit())) {
              toCheck.add(t2.getUnit());
            }
          }
        } else {
          throw new Exception("disconnected terminal");
        }
      }
      toCheck.remove(u);
      checked.add(u);
    }
    if (unit.getTerminals().size() > 0) {
      // You can't actually run a non-self-contained unit
      throw new Exception("has external terminals");
    }
  }
  
  public static void validateUnit(DirectedCompositeUnit dcu) throws Exception {
    for (DirectedUnit du : dcu.allUnits) {
      for (Terminal t : du.getTerminals()) {
        if (t.getConnection() == null) {
          throw new Exception("disconnected terminal");
        }
      }
    }
    if (dcu.getTerminals().size() > 0) {
      // You can't actually run a non-self-contained unit
      throw new Exception("has external terminals");
    }
  }
  
  public static HashSet<Terminal> findDisconnectedTerminals(Unit unit) {
    //TODO Rechecking, say, the source connection repeatedly could be slow.  Cache that, too.
    HashSet<Unit> toCheck = new HashSet<Unit>();
    HashSet<Unit> checked = new HashSet<Unit>();
    HashSet<Terminal> disconnected = new HashSet<Terminal>();
    toCheck.add(unit);
    while (!toCheck.isEmpty()) {
      Unit u = toCheck.iterator().next();
      for (Terminal t : u.getTerminals()) {
        if (t.getConnection() != null) {
          for (Terminal t2 : t.getConnection().getTerminals()) {
            if (!checked.contains(t2.getUnit())) {
              toCheck.add(t2.getUnit());
            }
          }
        } else {
          disconnected.add(t);
        }
      }
      toCheck.remove(u);
      checked.add(u);
    }
    return disconnected;
  }
  
  public static HashSet<Terminal> findDisconnectedTerminals(DirectedCompositeUnit dcu) {
    HashSet<Terminal> disconnected = new HashSet<Terminal>();
    for (DirectedUnit du : dcu.allUnits) {
      for (Terminal t : du.getTerminals()) {
        if (t.getConnection() == null) {
          disconnected.add(t);
        }
      }
    }
    return disconnected;
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

  /**
   * Testing a two-bit adder, and SinkSysoutBinary.
   * @throws IOException
   * @throws ClassNotFoundException 
   */
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
  
  /**
   * Testing the internalMetaUnit feature.
   * @throws IOException
   * @throws ClassNotFoundException 
   */
  public void testRun4() throws IOException, ClassNotFoundException {
    DirectedCompositeUnit add = new DirectedCompositeUnit(4, 2);
    add.setName("ADD2");

    AndGate ag = add.addUnit(new AndGate(add.iin(0), add.iin(1), add.iin(2), add.iin(3)));
    XorGate xg = add.addUnit(new XorGate(add.iin(0), add.iin(1), add.iin(2), add.iin(3)));
    GDC.addConnection(ag.out(0), add.iout(0));
    GDC.addConnection(xg.out(0), add.iout(1));
    
    unitTypes.add(add.copy());
    
    DirectedCompositeUnit shell = new DirectedCompositeUnit(0, 0);

    SourceHigh high = shell.addUnit(new SourceHigh());
    OutputTerminal highT = high.out(0);
    SourceLow low = shell.addUnit(new SourceLow());
    OutputTerminal lowT = low.out(0);

    StateSource a = shell.addUnit(new StateSource(1.0));
    OutputTerminal aT = a.out(0);
    StateSource b = shell.addUnit(new StateSource(1.0));
    OutputTerminal bT = b.out(0);
    
    shell.addUnit(add);
    add.setInputs(highT, lowT, aT, bT);
    shell.addUnit(new SinkSysoutBinary(highT, lowT, add.out(0), add.out(1)).setName("a+b"));
    
    for (double av = 0.0; av <= 1.0; av++) {
      for (double bv = 0.0; bv <= 1.0; bv++) {
        a.setValue(av);
        b.setValue(bv);
        
        System.out.println(a.getValue() + " " + b.getValue());
        shell.tick();
        shell.doFinalState();
      }
    }
  }
}
