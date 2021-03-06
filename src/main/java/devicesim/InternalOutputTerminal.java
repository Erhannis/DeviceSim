/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim;

/**
 * Is actually an InputTerminal.  It's attached to the inside edge of an OutputTerminal, though.
 * @author erhannis
 */
public class InternalOutputTerminal extends InputTerminal {
  private static final long serialVersionUID = 1792409368122463907L;
  private OutputTerminal dual;
  private DirectedConnection connection = null;
  
  public InternalOutputTerminal(OutputTerminal dual, DirectedUnit unit) {
    this.dual = dual;
    this.unit = unit;
  }

  @Override
  public void setValue(double value) {
    dual.setValue(value);
  }

  @Override
  public double getValue() {
    return dual.getValue();
  }

  @Override
  public String getName() {
    return dual.getName();
  }

  @Override
  public InputTerminal setName(String name) {
    dual.setName(name);
    return this;
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
