/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim;

import devicesim.units.defaults.AndGate;
import devicesim.units.defaults.SourceHigh;
import devicesim.units.defaults.SourceLow;
import java.util.ArrayList;
import devicesim.units.defaults.DirectedCompositeUnit;
import devicesim.units.defaults.SinkSysout;
import java.io.IOException;

/**
 * Running list of conventions to follow.
 * Connect things by creating a new GenericDirectedConnection.
 * Wrap every instantiation of a DirectedUnit subclass in `checkOrigin`.
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
  
  public void testRun() throws ClassNotFoundException, IOException {
    DirectedCompositeUnit dcu = new DirectedCompositeUnit();
    dcu.initTest();
    Unit dcuCopy = dcu.copy();
    unitTypes.add(dcuCopy);
    dcu.testRun();
    dcu.changeTest();
    dcu.testRun();
    dcu = (DirectedCompositeUnit)dcuCopy.copy();
    dcu.testRun();
  }
}
