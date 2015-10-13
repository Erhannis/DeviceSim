/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim;

import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author erhannis
 */
public interface DirectedConnection extends Connection {
  /**
   * Note that this is the input TO THE CONNECTION, so it is, itself, an output.
   * @return 
   */
  public OutputTerminal getInput();

  /**
   * Note that these are the outputs FROM THE CONNECTION, so they are, themselves, outputs.
   * @return 
   */
  public HashSet<InputTerminal> getOutputs();
}
