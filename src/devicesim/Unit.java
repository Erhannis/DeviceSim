/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import mathnstuff.utils.QueueStream.QueueInputStream;
import mathnstuff.utils.QueueStream.QueueOutputStream;

/**
 *
 * @author erhannis
 */
public interface Unit extends Serializable {
  public ArrayList<Terminal> getTerminals();
  public HashSet<? extends Terminal> tick();
  
  public String getName();
  public Unit setName(String name);
  
  /**
   * Be careful!  Don't call this after you've hooked it up to anything external.
   * It serializes itself and reads a clone from the serialization, and will
   * therefore copy any objects this one has references to.
   * @return 
   */
  public default Unit copy() throws IOException, ClassNotFoundException {
    QueueOutputStream qos = new QueueOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(qos);
    QueueInputStream qis = qos.dual;
    ObjectInputStream ois = new ObjectInputStream(qis);
    
    oos.writeObject(this);
    oos.flush();
    oos.close();
    Unit result = (Unit)ois.readObject();
    ois.close();
    
    return result;
  }
  
  /**
   * Same warnings apply here as for copy().  For convenience, upon errors, this one
   * logs them and then returns null.
   * @return 
   */
  public default Unit copyLog() {
    try {
      return copy();
    } catch (IOException ex) {
      Logger.getLogger(Unit.class.getName()).log(Level.SEVERE, null, ex);
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(Unit.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }
}
