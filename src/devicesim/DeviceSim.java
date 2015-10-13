/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim;

import java.io.IOException;

/**
 *
 * @author erhannis
 */
public class DeviceSim {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) throws ClassNotFoundException, IOException {
    DeviceEngine engine = new DeviceEngine();
    engine.testRun3();
  }
}
