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
public abstract class InputTerminal implements Terminal {
  private static final long serialVersionUID = 3135384821311998240L;
  private String name;
  DirectedUnit unit;
  
  @Override
  public abstract DirectedConnection getConnection();
  public abstract void setConnection(DirectedConnection connection);

  public void breakConnection() {
    DirectedConnection c = getConnection();
    if (c != null) {
      c.removeOutput(this);
    }
    setConnection(null);
  }
  
  @Override
  public DirectedUnit getUnit() {
    return unit;
  }

  @Override
  public String getName() {
    return name;
  }
  
  @Override
  public InputTerminal setName(String name) {
    this.name = name;
    return this; // For construction chains
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
    viewX = getUnit().getViewLeft();
    return viewX;
  }

  @Override
  public double calcViewY() {
    DirectedUnit u = getUnit();
    ArrayList<InputTerminal> inputs = u.getInputs();
    viewY = u.getViewTop() + (((inputs.indexOf(this) + 0.5) / inputs.size()) * u.getViewHeight());
    return viewY;
  }
  
  @Override
  public double calcViewSocketRadius() {
    DirectedUnit u = getUnit();
    viewSocketRadius = 0.5 * 0.4 * (u.getViewHeight() / u.getInputs().size());
    return viewSocketRadius;
  }
  //</editor-fold>
}
