/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim;

/**
 * Is actually an OutputTerminal.  It's attached to the inside edge of an InputTerminal, though.
 * @author erhannis
 */
public class InternalInputTerminal extends OutputTerminal {
  private InputTerminal dual;
  private DirectedConnection connection = null;
  
  private boolean hasChanged = true;
  private double prevValue;
  
  public InternalInputTerminal(InputTerminal dual, DirectedUnit unit) {
    this.dual = dual;
    this.unit = unit;
    this.prevValue = dual.getValue();
  }

  @Override
  public void setValue(double value) {
    if (dual.getValue() != value) {
      hasChanged = true;
      dual.setValue(value);
    }
  }

  @Override
  public double getValue() {
    return dual.getValue();
  }

  @Override
  public boolean pullHasChanged() {
    if (hasChanged || (prevValue != dual.getValue())) {
      hasChanged = false;
      prevValue = dual.getValue();
      return true;
    }
    return false;
  }
  
  @Override
  public void setConnection(DirectedConnection connection) {
    this.connection = connection;
  }
  
  @Override
  public DirectedConnection getConnection() {
    return connection;
  }
}
