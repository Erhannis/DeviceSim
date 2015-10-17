/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim.units.defaults;

import devicesim.GenericDirectedConnection.GDC;
import devicesim.OutputTerminal;
import devicesim.StateInputTerminal;
import devicesim.StateOutputTerminal;
import java.security.SecureRandom;
import java.util.HashSet;

/**
 *
 * @author erhannis
 */
public class RandomSMetaGate extends RandomMetaGate {
  // I'd kinda like this to be an instance variable, but copying makes them synced....
  private static SecureRandom r = new SecureRandom();
  
  public RandomSMetaGate() {
    // Augh, weird.  RandomMetaGate's constructor is called.
    setName("SRAND");
  }

  @Override
  boolean getRand() {
    return r.nextBoolean();
  }
}
