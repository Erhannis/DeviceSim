/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim.units.defaults;

import devicesim.DirectedUnit;
import devicesim.InputTerminal;
import devicesim.OutputTerminal;
import devicesim.Terminal;
import java.util.ArrayList;

/**
 *
 * @author erhannis
 */
public abstract class BlankDirectedUnit implements DirectedUnit {
  private String name;

  protected ArrayList<Terminal> terminals = new ArrayList<Terminal>();
  protected ArrayList<InputTerminal> inputs = new ArrayList<InputTerminal>();
  protected ArrayList<OutputTerminal> outputs = new ArrayList<OutputTerminal>();
  
  //<editor-fold desc="View stuff">
  public double viewTop = 50;
  public double viewLeft = 60;
  public double viewWidth = 30;
  public double viewHeight = 20;
  public float viewFontSize = 10;
  
  @Override
  public double getViewTop() {
    return viewTop;
  }

  @Override
  public double getViewLeft() {
    return viewLeft;
  }

  @Override
  public double getViewWidth() {
    return viewWidth;
  }

  @Override
  public double getViewHeight() {
    return viewHeight;
  }

  @Override
  public float getViewFontSize() {
    return viewFontSize;
  }

  @Override
  public void setViewTop(double top) {
    this.viewTop = top;
  }

  @Override
  public void setViewLeft(double left) {
    this.viewLeft = left;
  }

  @Override
  public void setViewWidth(double width) {
    this.viewWidth = width;
  }

  @Override
  public void setViewHeight(double height) {
    this.viewHeight = height;
  }
  
  @Override
  public void setViewTopLeft(double top, double left) {
    this.viewTop = top;
    this.viewLeft = left;
  }
  
  @Override
  public void setViewDims(double width, double height) {
    this.viewWidth = width;
    this.viewHeight = height;
  }
  
  @Override
  public void setViewFontSize(float fontSize) {
    this.viewFontSize = fontSize;
  }
  //</editor-fold>
  
  @Override
  public ArrayList<Terminal> getTerminals() {
    //TODO Maybe ought not to return the actual object
    return terminals;
  }

  @Override
  public ArrayList<InputTerminal> getInputs() {
    return inputs;
  }

  @Override
  public ArrayList<OutputTerminal> getOutputs() {
    return outputs;
  }

  @Override
  public String getName() {
    return name;
  }
  
  // Dunno why I'm all about getters and setters at the moment.  Must be all these interfaces.
  @Override
  public BlankDirectedUnit setName(String name) {
    this.name = name;
    return this; // For construction chains
  }
  
  // Most things won't usually need to implement this, so I'm providing a blank default.
  @Override
  public void doFinalState() {
  }
  
  // Same here
  @Override
  public boolean isFinal() {
    return false;
  }

  @Override
  public String toString() {
    String name = getName();
    if (name != null) {
      return name;
    } else {
      return super.toString();
    }
  }
}
