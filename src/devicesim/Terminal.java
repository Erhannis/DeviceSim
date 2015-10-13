/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim;

import java.io.Serializable;

/**
 *
 * @author erhannis
 */
public interface Terminal extends Serializable {
  public void setValue(double value);
  public double getValue();
  public Unit getUnit();
  public Connection getConnection();
}
