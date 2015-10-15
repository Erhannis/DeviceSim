/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim;

/**
 *
 * @author erhannis
 */
public class StateOutputTerminal extends OutputTerminal {
  private double value;
  private DirectedConnection connection = null;
  
  private boolean hasChanged = true;
  
  public StateOutputTerminal(double value, DirectedUnit unit) {
    this.value = value;
    this.unit = unit;
  }

  @Override
  public void setValue(double value) {
    if (this.value != value) {
      hasChanged = true;
      this.value = value;
    }
  }
  
  @Override
  public boolean pullHasChanged() {
    if (hasChanged) {
      hasChanged = false;
      return true;
    }
    return false;
  }

  @Override
  public double getValue() {
    return value;
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
