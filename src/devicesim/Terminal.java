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
  
  //<editor-fold desc="Optional view stuff">
  public default double getViewX() {
    return calcViewX();
  }

  public default double getViewY() {
    return calcViewY();
  }
  
  public default double getViewSocketRadius() {
    return calcViewSocketRadius();
  }
  
  public default double calcViewX() {
    return getUnit().getViewLeft();
  }

  public default double calcViewY() {
    return getUnit().getViewTop();
  }

  public default double calcViewSocketRadius() {
    Unit u = getUnit();
    return 0.5 * 0.4 * (u.getViewHeight() / u.getTerminals().size());
  }
  
  public default void recalcView() {
    calcViewX();
    calcViewY();
    calcViewSocketRadius();
  }
  //</editor-fold>
}
