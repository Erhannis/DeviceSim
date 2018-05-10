/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * I think the only places these should be stored semipermanently are on the
 * terminals themselves, so they can sever themselves properly if needed.
 * @author erhannis
 */
public interface Connection extends Serializable {
  public HashSet<Terminal> getTerminals();
}
