/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim;

/**
 * Hmm, identical to StateOutputTerminal.  Do they need to be separate?
 * @author erhannis
 */
public class StateInputTerminal implements InputTerminal {
  private double value;
  private DirectedUnit unit;
  private DirectedConnection connection = null;
  
  public StateInputTerminal(double value, DirectedUnit unit) {
    this.value = value;
    this.unit = unit;
  }

  @Override
  public void setValue(double value) {
    this.value = value;
  }

  @Override
  public double getValue() {
    return value;
  }

  @Override
  public DirectedUnit getUnit() {
    return unit;
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
