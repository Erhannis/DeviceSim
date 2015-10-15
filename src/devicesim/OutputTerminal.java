/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim;

import java.util.ArrayList;

/**
 *
 * @author erhannis
 */
public abstract class OutputTerminal implements Terminal {
  DirectedUnit unit;
  
  @Override
  public abstract DirectedConnection getConnection();
  public abstract void setConnection(DirectedConnection connection);
  public abstract boolean pullHasChanged();

  public void breakConnection() {
    DirectedConnection c = getConnection();
    if (c != null) {
      c.severConnection();
    }
  }
  
  @Override
  public DirectedUnit getUnit() {
    return unit;
  }
  
  //<editor-fold desc="Optional view stuff">
  // Calculating these repeatedly could get slow.
  //     Good thing I just figured out a nice caching mechanism!
  public double viewX = Double.NaN;
  public double viewY = Double.NaN;
  public double viewSocketRadius = Double.NaN;
  
  @Override
  public double getViewX() {
    if (Double.isNaN(viewX)) {
      return calcViewX();
    }
    return viewX;
  }

  @Override
  public double getViewY() {
    if (Double.isNaN(viewY)) {
      return calcViewY();
    }
    return viewY;
  }

  @Override
  public double getViewSocketRadius() {
    if (Double.isNaN(viewSocketRadius)) {
      return calcViewSocketRadius();
    }
    return viewSocketRadius;
  }
  
  @Override
  public double calcViewX() {
    Unit u = getUnit();
    viewX = u.getViewLeft() + u.getViewWidth();
    return viewX;
  }

  @Override
  public double calcViewY() {
    DirectedUnit u = getUnit();
    ArrayList<OutputTerminal> outputs = u.getOutputs();
    viewY = u.getViewTop() + (((outputs.indexOf(this) + 0.5) / outputs.size()) * u.getViewHeight());
    return viewY;
  }
  
  @Override
  public double calcViewSocketRadius() {
    DirectedUnit u = getUnit();
    viewSocketRadius = 0.5 * 0.4 * (u.getViewHeight() / u.getOutputs().size());
    return viewSocketRadius;
  }
  //</editor-fold>
}
