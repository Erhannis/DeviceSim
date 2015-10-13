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
public interface Unit {
  public ArrayList<Terminal> getTerminals();
  public HashSet<? extends Terminal> tick();
}
