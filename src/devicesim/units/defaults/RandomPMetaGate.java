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
import java.util.HashSet;
import java.util.Random;

/**
 *
 * @author erhannis
 */
public class RandomPMetaGate extends RandomMetaGate {
  // I'd kinda like this to be an instance variable, but copying makes them synced....
  private static Random r = new Random();
  
  public RandomPMetaGate() {
    // Augh, weird.  RandomMetaGate's constructor is called.
    setName("PRAND");
  }

  @Override
  boolean getRand() {
    return r.nextBoolean();
  }
}
