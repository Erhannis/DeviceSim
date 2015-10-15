/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim;

import com.sun.glass.events.KeyEvent;
import devicesim.GenericDirectedConnection.GDC;
import devicesim.units.defaults.DirectedCompositeUnit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author erhannis
 */
public class FrameEditDirectedCompositeUnit extends javax.swing.JFrame {
  private DirectedCompositeUnit unit;
  private DirectedCompositeUnit unitToReplace;
  private DefaultListModel<Unit> unitTypes;
  private boolean changed = false;
  private boolean loading = false;
  
  private PanelDisplay pd;
  
  /**
   * Creates new form FrameEditUnit
   */
  public FrameEditDirectedCompositeUnit(DefaultListModel<Unit> unitTypes, DirectedCompositeUnit unit, DirectedCompositeUnit unitToReplace) {
    this.unitTypes = unitTypes;
    this.unit = unit;
    this.unitToReplace = unitToReplace;
    loading = true;
    initComponents();
    loading = false;
    
    load();

    radioMove.setMnemonic(KeyEvent.VK_M);
    radioConnect.setMnemonic(KeyEvent.VK_C);
    radioDisconnect.setMnemonic(KeyEvent.VK_D);
    
    pd = new PanelDisplay();
    pd.units = unit.collectDownstreamUnits();
    jSplitPane1.setLeftComponent(pd);
    
    pd.addMouseListener(new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent e) {
        Point2D m = pd.ati.transform(new Point2D.Double(e.getX(), e.getY()), null);
        if (radioMove.isSelected()) {
        } else if (radioConnect.isSelected()) {
          outer:
          for (Unit u : pd.units) {
            for (Terminal t : u.getTerminals()) {
              double dist = m.distance(t.getViewX(), t.getViewY());
              if (dist < t.getViewSocketRadius()) {
                if (pd.selectedTerminal == null) {
                  pd.selectedTerminal = t;
                  break outer;
                } else {
                  if (pd.selectedTerminal instanceof InputTerminal && t instanceof OutputTerminal) {
                    GDC.addConnection(((OutputTerminal)t), ((InputTerminal)pd.selectedTerminal));
                  } else if (pd.selectedTerminal instanceof OutputTerminal && t instanceof InputTerminal) {
                    GDC.addConnection(((OutputTerminal)pd.selectedTerminal), ((InputTerminal)t));
                  } else {
                  }
                  pd.selectedTerminal = null;
                }
              }
            }
          }
          changed = true;
          doRepaint();
        } else if (radioDisconnect.isSelected()) {
          outer:
          for (Unit u : pd.units) {
            for (Terminal t : u.getTerminals()) {
              double dist = m.distance(t.getViewX(), t.getViewY());
              if (dist < t.getViewSocketRadius()) {
                //TODO Yeah, the following is slightly cheating.
                if (t instanceof InputTerminal) {
                  ((InputTerminal)t).getConnection().removeOutput(((InputTerminal)t));
                } else if (t instanceof OutputTerminal) {
                  ((OutputTerminal)t).getConnection().severConnection();
                } else {
                  //TODO Dunno.
                }
              }
            }
          }
          changed = true;
          doRepaint();
        }
      }

      public boolean hadFocus = false;
      
      @Override
      public void mousePressed(MouseEvent e) {
        Point2D m = pd.ati.transform(new Point2D.Double(e.getX(), e.getY()), null);
        if (radioMove.isSelected()) {
          double closestDist2 = Double.POSITIVE_INFINITY;
          Unit closest = null;
          for (Unit u : pd.units) {
            double dist2 = m.distanceSq(u.getViewLeft(), u.getViewTop());
            if (dist2 < closestDist2) {
              closest = u;
              closestDist2 = dist2;
            }
          }
          pd.selectedUnit = closest;
          doRepaint();
        } else if (radioConnect.isSelected()) {
        } else if (radioDisconnect.isSelected()) {
        }
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        pd.selectedUnit = null;
        doRepaint();
      }

      @Override
      public void mouseEntered(MouseEvent e) {
      }

      @Override
      public void mouseExited(MouseEvent e) {
      }
    });
    pd.addMouseMotionListener(new MouseMotionListener() {
      @Override
      public void mouseDragged(MouseEvent e) {
        if (pd.selectedUnit != null) {
          Point2D m = pd.ati.transform(new Point2D.Double(e.getX(), e.getY()), null);
          pd.selectedUnit.setViewLeft(m.getX());
          pd.selectedUnit.setViewTop(m.getY());
          pd.selectedUnit.recalcView();
          changed = true;
          doRepaint();
        }
      }

      @Override
      public void mouseMoved(MouseEvent e) {
      }
    });
    pd.addMouseWheelListener(new MouseWheelListener() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        if (pd.selectedUnit != null) {
          double scale = Math.pow(1.1, e.getPreciseWheelRotation());
          pd.selectedUnit.setViewWidth(pd.selectedUnit.getViewWidth() * scale);
          pd.selectedUnit.setViewHeight(pd.selectedUnit.getViewHeight() * scale);
          pd.selectedUnit.setViewFontSize((float)(pd.selectedUnit.getViewFontSize() * scale));
          pd.selectedUnit.recalcView();
          changed = true;
          doRepaint();
        }
      }
    });
    
    textUnitName.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void changedUpdate(DocumentEvent e) {
        save();
      }
      @Override
      public void removeUpdate(DocumentEvent e) {
        save();
      }
      @Override
      public void insertUpdate(DocumentEvent e) {
        save();
      }

      public void save() {
        if (!loading) {
          unit.setName(textUnitName.getText());
          changed = true;
        }
      }
    });
    
    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent ev) {
        if (changed) {
          switch (JOptionPane.showConfirmDialog(FrameEditDirectedCompositeUnit.this, "Save before closing?")) {
            case JOptionPane.YES_OPTION:
              try {
                saveUnit();
              } catch (IOException ex) {
                Logger.getLogger(FrameEditDirectedCompositeUnit.class.getName()).log(Level.SEVERE, null, ex);
              } catch (ClassNotFoundException ex) {
                Logger.getLogger(FrameEditDirectedCompositeUnit.class.getName()).log(Level.SEVERE, null, ex);
              }
              FrameEditDirectedCompositeUnit.this.dispose();
              break;
            case JOptionPane.NO_OPTION:
              FrameEditDirectedCompositeUnit.this.dispose();
              break;
            case JOptionPane.CANCEL_OPTION:
              break;
          }
        } else {
          FrameEditDirectedCompositeUnit.this.dispose();
        }
      }
    });
  }

  private void doRepaint() {
    if (!pd.skipRender) {
      pd.repaint();
    }
  }
  private void load() {
    loading = true;
    textUnitName.setText(unit.getName());
    spinInputs.setValue(unit.getInputs().size());
    spinOutputs.setValue(unit.getOutputs().size());
    loading = false;
  }
  
  private void saveUnit() throws IOException, ClassNotFoundException {
    if (unitToReplace != null) {
      int idx = unitTypes.indexOf(unitToReplace);
      DirectedCompositeUnit copy = (DirectedCompositeUnit)unit.copy();
      unitTypes.set(idx, copy);
      unitToReplace = copy;
    }
    changed = false;
  }
  
  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    groupTools = new javax.swing.ButtonGroup();
    jSplitPane1 = new javax.swing.JSplitPane();
    jPanel1 = new javax.swing.JPanel();
    jTabbedPane1 = new javax.swing.JTabbedPane();
    jPanel3 = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    textUnitName = new javax.swing.JTextField();
    btnSaveUnit = new javax.swing.JButton();
    spinInputs = new javax.swing.JSpinner();
    jLabel2 = new javax.swing.JLabel();
    jLabel3 = new javax.swing.JLabel();
    spinOutputs = new javax.swing.JSpinner();
    btnRun = new javax.swing.JButton();
    btnRedraw = new javax.swing.JButton();
    jPanel4 = new javax.swing.JPanel();
    radioMove = new javax.swing.JRadioButton();
    radioConnect = new javax.swing.JRadioButton();
    radioDisconnect = new javax.swing.JRadioButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent evt) {
        formWindowClosing(evt);
      }
    });

    jSplitPane1.setDividerLocation(500);
    jSplitPane1.setResizeWeight(1.0);

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 500, Short.MAX_VALUE)
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 487, Short.MAX_VALUE)
    );

    jSplitPane1.setLeftComponent(jPanel1);

    jLabel1.setText("Name");

    textUnitName.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        textUnitNameActionPerformed(evt);
      }
    });
    textUnitName.addInputMethodListener(new java.awt.event.InputMethodListener() {
      public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
        textUnitNameInputMethodTextChanged(evt);
      }
      public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
      }
    });
    textUnitName.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
      public void propertyChange(java.beans.PropertyChangeEvent evt) {
        textUnitNamePropertyChange(evt);
      }
    });
    textUnitName.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
      public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
        textUnitNameVetoableChange(evt);
      }
    });

    btnSaveUnit.setText("Save");
    btnSaveUnit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnSaveUnitActionPerformed(evt);
      }
    });

    spinInputs.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
    spinInputs.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        spinInputsStateChanged(evt);
      }
    });

    jLabel2.setText("Inputs");

    jLabel3.setText("Outputs");

    spinOutputs.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
    spinOutputs.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        spinOutputsStateChanged(evt);
      }
    });

    btnRun.setText("Run");
    btnRun.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnRunActionPerformed(evt);
      }
    });

    btnRedraw.setText("Redraw");
    btnRedraw.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnRedrawActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addComponent(jLabel1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(textUnitName))
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addComponent(btnSaveUnit)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnRun))
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(btnRedraw)
              .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addComponent(jLabel2)
                  .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addComponent(spinOutputs, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addComponent(spinInputs, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addGap(0, 46, Short.MAX_VALUE)))
        .addContainerGap())
    );
    jPanel3Layout.setVerticalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(textUnitName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel1))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(spinInputs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel2))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(spinOutputs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel3))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 237, Short.MAX_VALUE)
        .addComponent(btnRedraw)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(btnSaveUnit)
          .addComponent(btnRun))
        .addContainerGap())
    );

    jTabbedPane1.addTab("Props", jPanel3);

    groupTools.add(radioMove);
    radioMove.setSelected(true);
    radioMove.setText("(M)ove");

    groupTools.add(radioConnect);
    radioConnect.setText("(C)onnect");

    groupTools.add(radioDisconnect);
    radioDisconnect.setText("(D)isconnect");

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
      jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(radioMove)
          .addComponent(radioConnect)
          .addComponent(radioDisconnect))
        .addContainerGap(132, Short.MAX_VALUE))
    );
    jPanel4Layout.setVerticalGroup(
      jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(radioMove)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(radioConnect)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(radioDisconnect)
        .addContainerGap(354, Short.MAX_VALUE))
    );

    jTabbedPane1.addTab("Tools", jPanel4);

    jSplitPane1.setRightComponent(jTabbedPane1);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jSplitPane1)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jSplitPane1)
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
  }//GEN-LAST:event_formWindowClosing

  private void textUnitNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textUnitNameActionPerformed
  }//GEN-LAST:event_textUnitNameActionPerformed

  private void textUnitNamePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_textUnitNamePropertyChange
  }//GEN-LAST:event_textUnitNamePropertyChange

  private void btnSaveUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveUnitActionPerformed
    try {
      saveUnit();
    } catch (IOException ex) {
      Logger.getLogger(FrameEditDirectedCompositeUnit.class.getName()).log(Level.SEVERE, null, ex);
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(FrameEditDirectedCompositeUnit.class.getName()).log(Level.SEVERE, null, ex);
    }
  }//GEN-LAST:event_btnSaveUnitActionPerformed

  private void textUnitNameInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_textUnitNameInputMethodTextChanged
  }//GEN-LAST:event_textUnitNameInputMethodTextChanged

  private void textUnitNameVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_textUnitNameVetoableChange
  }//GEN-LAST:event_textUnitNameVetoableChange

  private void spinInputsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinInputsStateChanged
    if (!loading) {
      unit.resizeTerminals((Integer)spinInputs.getValue(), (Integer)spinOutputs.getValue());
      changed = true;
      doRepaint();
    }
  }//GEN-LAST:event_spinInputsStateChanged

  private void spinOutputsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinOutputsStateChanged
    if (!loading) {
      unit.resizeTerminals((Integer)spinInputs.getValue(), (Integer)spinOutputs.getValue());
      changed = true;
      doRepaint();
    }
  }//GEN-LAST:event_spinOutputsStateChanged

  private void btnRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRunActionPerformed
    //TODO Do
  }//GEN-LAST:event_btnRunActionPerformed

  private void btnRedrawActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRedrawActionPerformed
    doRepaint();
  }//GEN-LAST:event_btnRedrawActionPerformed

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
     * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
     */
    try {
      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (ClassNotFoundException ex) {
      java.util.logging.Logger.getLogger(FrameEditDirectedCompositeUnit.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      java.util.logging.Logger.getLogger(FrameEditDirectedCompositeUnit.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      java.util.logging.Logger.getLogger(FrameEditDirectedCompositeUnit.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger.getLogger(FrameEditDirectedCompositeUnit.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
        //</editor-fold>

    /* Create and display the form */
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        new FrameEditDirectedCompositeUnit(null, null, null).setVisible(true);
      }
    });
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton btnRedraw;
  private javax.swing.JButton btnRun;
  private javax.swing.JButton btnSaveUnit;
  private javax.swing.ButtonGroup groupTools;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JPanel jPanel4;
  private javax.swing.JSplitPane jSplitPane1;
  private javax.swing.JTabbedPane jTabbedPane1;
  private javax.swing.JRadioButton radioConnect;
  private javax.swing.JRadioButton radioDisconnect;
  private javax.swing.JRadioButton radioMove;
  private javax.swing.JSpinner spinInputs;
  private javax.swing.JSpinner spinOutputs;
  private javax.swing.JTextField textUnitName;
  // End of variables declaration//GEN-END:variables
}
