/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim;

import java.io.File;
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
    //DeviceEngine.readFile(new File("/Users/MEwer/NetBeansProjects/DeviceSim/saves/mem8_2.crc"));
    FrameMain main = new FrameMain();
    main.setVisible(true);
  }
}
