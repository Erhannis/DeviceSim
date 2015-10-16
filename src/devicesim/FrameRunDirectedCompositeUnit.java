/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim;

import com.sun.glass.events.KeyEvent;
import devicesim.units.defaults.DirectedCompositeUnit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import mathnstuff.MeMath;

/**
 *
 * @author erhannis
 */
public class FrameRunDirectedCompositeUnit extends javax.swing.JFrame {
  private DirectedCompositeUnit unit;
  private DirectedCompositeUnit unitPseudoArchetype;
  private DefaultListModel<Unit> unitTypes;
  
  private PanelDisplay pd;
  
  /**
   * Creates new form FrameEditUnit
   */
  public FrameRunDirectedCompositeUnit(DefaultListModel<Unit> unitTypes, DirectedCompositeUnit unit, DirectedCompositeUnit unitPseudoArchetype) {
    this.unitTypes = unitTypes; // Maybe unneeded
    this.unit = unit;
    this.unitPseudoArchetype = unitPseudoArchetype;
    initComponents();
    
    this.setTitle(unit.getName() + " run");
    
    
    radioInteract.setMnemonic(KeyEvent.VK_I);
    radioProbe.setMnemonic(KeyEvent.VK_P);
    
    pd = new PanelDisplay();
    pd.units = unit.allUnits;
    jSplitPane1.setLeftComponent(pd);
    
    pd.addMouseListener(new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent e) {
        Point2D m = pd.ati.transform(new Point2D.Double(e.getX(), e.getY()), null);
        if (radioInteract.isSelected()) {
          double closestDist2 = Double.POSITIVE_INFINITY;
          Unit closest = null;
          for (Unit u : pd.units) {
            double dist2 = m.distanceSq(u.getViewLeft(), u.getViewTop());
            if (dist2 < closestDist2 && dist2 <= MeMath.sqr((u.getViewHeight() + u.getViewHeight()) / 2.0) && u != unit.internalMetaUnit) {
              // We want to be at least PRETTY close, and not delete the internalMetaUnit.
              closest = u;
              closestDist2 = dist2;
            }
          }
          if (closest != null) {
            if (closest instanceof Runnable && closest instanceof DirectedUnit) {
              ((Runnable)closest).run();
              //TODO Again, cheating
              unit.addManualCheck((DirectedUnit)closest);
              if (cbAutorun.isSelected()) {
                doRun();
              } else {
                doRepaint();
              }
            }
          }
        } else if (radioProbe.isSelected()) {
        }
      }

      public boolean hadFocus = false;
      
      @Override
      public void mousePressed(MouseEvent e) {
        Point2D m = pd.ati.transform(new Point2D.Double(e.getX(), e.getY()), null);
        if (radioInteract.isSelected()) {
        } else if (radioProbe.isSelected()) {
        }
      }

      @Override
      public void mouseReleased(MouseEvent e) {
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
        if (radioInteract.isSelected()) {
        } else if (radioProbe.isSelected()) {
        }
      }

      @Override
      public void mouseMoved(MouseEvent e) {
      }
    });
    pd.addMouseWheelListener(new MouseWheelListener() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        if (radioInteract.isSelected()) {
        } else if (radioProbe.isSelected()) {
        }
      }
    });
  }
  
  private void doRepaint() {
    if (!pd.skipRender) {
      pd.repaint();
    }
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
    btnSaveUnitState = new javax.swing.JButton();
    btnRun = new javax.swing.JButton();
    btnRedraw = new javax.swing.JButton();
    jPanel4 = new javax.swing.JPanel();
    radioInteract = new javax.swing.JRadioButton();
    radioProbe = new javax.swing.JRadioButton();
    cbAutorun = new javax.swing.JCheckBox();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
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

    btnSaveUnitState.setText("Save State");
    btnSaveUnitState.setToolTipText("Technically speaking, saves a unit.  It'll be in the displayed state, though.  Yeah, yeah, yeah.");
    btnSaveUnitState.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnSaveUnitStateActionPerformed(evt);
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
            .addComponent(btnSaveUnitState)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 109, Short.MAX_VALUE)
            .addComponent(btnRun))
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addComponent(btnRedraw)
            .addGap(0, 0, Short.MAX_VALUE)))
        .addContainerGap())
    );
    jPanel3Layout.setVerticalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap(369, Short.MAX_VALUE)
        .addComponent(btnRedraw)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(btnSaveUnitState)
          .addComponent(btnRun))
        .addContainerGap())
    );

    jTabbedPane1.addTab("Props", jPanel3);

    groupTools.add(radioInteract);
    radioInteract.setSelected(true);
    radioInteract.setText("(I)nteract");
    radioInteract.setToolTipText("Toggle switches, etc.");

    groupTools.add(radioProbe);
    radioProbe.setText("(P)robe");
    radioProbe.setToolTipText("Nothing, atm");

    cbAutorun.setSelected(true);
    cbAutorun.setText("Autorun");
    cbAutorun.setToolTipText("Hits \"run\" after you interact with anything");

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
      jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel4Layout.createSequentialGroup()
            .addComponent(radioInteract)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(cbAutorun))
          .addComponent(radioProbe))
        .addContainerGap(69, Short.MAX_VALUE))
    );
    jPanel4Layout.setVerticalGroup(
      jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(radioInteract)
          .addComponent(cbAutorun))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(radioProbe)
        .addContainerGap(385, Short.MAX_VALUE))
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

  public JFileChooser fileChooser = new JFileChooser();
  
  private void btnSaveUnitStateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveUnitStateActionPerformed
    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      DeviceEngine.saveObjectToFile(unit, fileChooser.getSelectedFile());
    }
  }//GEN-LAST:event_btnSaveUnitStateActionPerformed

  private void doRun() {
    unit.tick();
    unit.doFinalState();
    doRepaint();
  }
  
  private void btnRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRunActionPerformed
    doRun();
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
      java.util.logging.Logger.getLogger(FrameRunDirectedCompositeUnit.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      java.util.logging.Logger.getLogger(FrameRunDirectedCompositeUnit.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      java.util.logging.Logger.getLogger(FrameRunDirectedCompositeUnit.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger.getLogger(FrameRunDirectedCompositeUnit.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
        //</editor-fold>

    /* Create and display the form */
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        new FrameRunDirectedCompositeUnit(null, null, null).setVisible(true);
      }
    });
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton btnRedraw;
  private javax.swing.JButton btnRun;
  private javax.swing.JButton btnSaveUnitState;
  private javax.swing.JCheckBox cbAutorun;
  private javax.swing.ButtonGroup groupTools;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JPanel jPanel4;
  private javax.swing.JSplitPane jSplitPane1;
  private javax.swing.JTabbedPane jTabbedPane1;
  private javax.swing.JRadioButton radioInteract;
  private javax.swing.JRadioButton radioProbe;
  // End of variables declaration//GEN-END:variables
}
