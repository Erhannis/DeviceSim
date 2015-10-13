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
public interface InputTerminal extends Terminal {
  @Override
  public DirectedConnection getConnection();
  public void setConnection(DirectedConnection connection);
  @Override
  public DirectedUnit getUnit();
}
