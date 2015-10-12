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
public interface DirectedUnit extends Unit {
  public ArrayList<InputTerminal> getInputs();
  public ArrayList<OutputTerminal> getOutputs();
  public boolean isOrigin();
  
  //TODO May be barking up the wrong tree.
  public void resolve();
}
