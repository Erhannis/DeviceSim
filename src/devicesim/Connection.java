/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author erhannis
 */
public interface Connection extends Serializable {
  public ArrayList<Terminal> getTerminals();
}
