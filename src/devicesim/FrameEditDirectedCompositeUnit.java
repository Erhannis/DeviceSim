/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim;

import com.sun.glass.events.KeyEvent;
import devicesim.GenericDirectedConnection.GDC;
import devicesim.units.defaults.DirectedCompositeUnit;
import devicesim.units.defaults.SourceHigh;
import devicesim.units.defaults.SourceLow;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import mathnstuff.MeMath;
import mathnstuff.MeUtils;

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

    listUnitTypes.setModel(unitTypes);
    
    radioMove.setMnemonic(KeyEvent.VK_M);
    radioConnect.setMnemonic(KeyEvent.VK_C);
    radioDisconnect.setMnemonic(KeyEvent.VK_D);
    radioRemove.setMnemonic(KeyEvent.VK_R);
    radioReplace.setMnemonic(KeyEvent.VK_E);
    radioPlace.setMnemonic(KeyEvent.VK_P);
    
    pd = new PanelDisplay();
    pd.units = unit.allUnits;
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
                if (pd.selectedTerminals.isEmpty() || pd.selectedTerminals.size() > 1) {
                  pd.selectedTerminals.clear();
                  pd.selectedTerminals.add(t);
                } else {
                  Terminal st = pd.selectedTerminals.iterator().next();
                  if (st instanceof InputTerminal && t instanceof OutputTerminal) {
                    GDC.addConnection(((OutputTerminal)t), ((InputTerminal)st));
                  } else if (st instanceof OutputTerminal && t instanceof InputTerminal) {
                    GDC.addConnection(((OutputTerminal)st), ((InputTerminal)t));
                  } else {
                  }
                  if (!e.isShiftDown()) {
                    pd.selectedTerminals.clear();
                  }
                }
                break outer;
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
                  if (((InputTerminal)t).getConnection() != null) {
                    ((InputTerminal)t).getConnection().removeOutput(((InputTerminal)t));
                  }
                } else if (t instanceof OutputTerminal) {
                  if (((OutputTerminal)t).getConnection() != null) {
                    ((OutputTerminal)t).getConnection().severConnection();
                  }
                } else {
                  //TODO Dunno.
                }
              }
            }
          }
          changed = true;
          doRepaint();
        } else if (radioRemove.isSelected()) {
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
            if (closest instanceof DirectedUnit) {
              //TODO Again, cheating
              unit.removeUnit((DirectedUnit)closest);
            }
          }
          pd.selectedTerminals.clear();
          pd.selectedUnits.clear();
          doRepaint();
        } else if (radioReplace.isSelected()) {
          double closestDist2 = Double.POSITIVE_INFINITY;
          Unit closest = null;
          for (Unit u : pd.units) {
            double dist2 = m.distanceSq(u.getViewLeft(), u.getViewTop());
            if (dist2 < closestDist2 && dist2 <= MeMath.sqr((u.getViewHeight() + u.getViewHeight()) / 2.0) && u != unit.internalMetaUnit) {
              // We want to be at least PRETTY close, and not replace the internalMetaUnit.
              closest = u;
              closestDist2 = dist2;
            }
          }
          if (closest != null) {
            if (closest instanceof DirectedUnit) {
              //TODO Again, cheating
              
              // Copied from radioPlace block
              Unit newUnitArchetype = (Unit)listUnitTypes.getSelectedValue();
              if (newUnitArchetype == null) {
                return;
              } else if (newUnitArchetype == unitToReplace) {
                JOptionPane.showMessageDialog(FrameEditDirectedCompositeUnit.this, "This is...a bad idea.  And not yet implemented.  I may do so later, if I'm feeling dangerous.");
                return;
              } else if (!(newUnitArchetype instanceof DirectedUnit)) {
                JOptionPane.showMessageDialog(FrameEditDirectedCompositeUnit.this, "Sorry, not a DirectedUnit.");
              }
              DirectedUnit newUnit = null;
              try {
                newUnit = (DirectedUnit)newUnitArchetype.copy();
              } catch (IOException ex) {
                Logger.getLogger(FrameEditDirectedCompositeUnit.class.getName()).log(Level.SEVERE, null, ex);
                return;
              } catch (ClassNotFoundException ex) {
                Logger.getLogger(FrameEditDirectedCompositeUnit.class.getName()).log(Level.SEVERE, null, ex);
                return;
              }
              unit.addUnit(newUnit);
              
              DirectedUnit duc = (DirectedUnit)closest;
              int ins = Math.min(duc.getInputs().size(), newUnit.getInputs().size());
              int outs = Math.min(duc.getOutputs().size(), newUnit.getOutputs().size());
              for (int i = 0; i < ins; i++) {
                if (duc.in(i).getConnection() != null) {
                  GDC.addConnection(duc.in(i).getConnection().getInput(), newUnit.in(i));
                }
              }
              for (int i = 0; i < outs; i++) {
                if (duc.out(i).getConnection() != null) {
                  GDC.addConnection(newUnit.out(i), duc.out(i).getConnection().getOutputs().toArray(new InputTerminal[]{}));
                }
              }
              newUnit.setViewDims(closest.getViewWidth(), closest.getViewHeight());
              newUnit.setViewTopLeft(closest.getViewTop(), closest.getViewLeft());
              newUnit.setViewFontSize(closest.getViewFontSize());
              newUnit.recalcView();
              unit.removeUnit((DirectedUnit)closest);
              pd.selectedTerminals.clear();
              pd.selectedUnits.clear();
              doRepaint();
            }
          }
          pd.selectedTerminals.clear();
          pd.selectedUnits.clear();
          doRepaint();
        } else if (radioPlace.isSelected()) {
          Unit newUnitArchetype = (Unit)listUnitTypes.getSelectedValue();
          if (newUnitArchetype == null) {
            return;
          } else if (newUnitArchetype == unitToReplace) {
            JOptionPane.showMessageDialog(FrameEditDirectedCompositeUnit.this, "This is...a bad idea.  And not yet implemented.  I may do so later, if I'm feeling dangerous.");
            return;
          } else if (!(newUnitArchetype instanceof DirectedUnit)) {
            JOptionPane.showMessageDialog(FrameEditDirectedCompositeUnit.this, "Sorry, not a DirectedUnit.");
          }
          DirectedUnit newUnit = null;
          try {
            newUnit = (DirectedUnit)newUnitArchetype.copy();
          } catch (IOException ex) {
            Logger.getLogger(FrameEditDirectedCompositeUnit.class.getName()).log(Level.SEVERE, null, ex);
            return;
          } catch (ClassNotFoundException ex) {
            Logger.getLogger(FrameEditDirectedCompositeUnit.class.getName()).log(Level.SEVERE, null, ex);
            return;
          }
          unit.addUnit(newUnit);
          if (cbAutosource.isSelected() && newUnit.getInputs().size() >= 2) {
            if (unit.internalMetaUnit.getOutputs().size() >= 2) {
              GDC.addConnection(unit.internalMetaUnit.out(0), newUnit.in(0));
              GDC.addConnection(unit.internalMetaUnit.out(1), newUnit.in(1));
            } else {
              boolean foundHigh = false;
              boolean foundLow = false;
              for (DirectedUnit du : unit.allUnits) {
                if (du instanceof SourceHigh) {
                  foundHigh = true;
                  GDC.addConnection(du.out(0), newUnit.in(0));
                } else if (du instanceof SourceLow) {
                  foundLow = true;
                  GDC.addConnection(du.out(0), newUnit.in(1));
                }
                if (foundLow && foundHigh) {
                  break;
                }
              }
            }
          }
          newUnit.setViewLeft(m.getX());
          newUnit.setViewTop(m.getY());
          newUnit.recalcView();
          doRepaint();
        }
      }

      public boolean hadFocus = false;
      private Point2D startPoint = null;
      
      @Override
      public void mousePressed(MouseEvent e) {
        changed = true;
        Point2D m = pd.ati.transform(new Point2D.Double(e.getX(), e.getY()), null);
        startPoint = m;
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
          pd.selectedUnits.clear();
          pd.selectedUnits.add(closest);
          doRepaint();
        } else if (radioConnect.isSelected()) {
        } else if (radioDisconnect.isSelected()) {
        } else if (radioRemove.isSelected()) {
        } else if (radioReplace.isSelected()) {
        } else if (radioPlace.isSelected()) {
        }
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        Point2D m = pd.ati.transform(new Point2D.Double(e.getX(), e.getY()), null);
        pd.selectedUnits.clear();
        IfChain:
        if (radioMove.isSelected()) {
        } else if (radioConnect.isSelected()) {
          if (m.equals(startPoint)) {
            break IfChain;
          }
          Rectangle2D r = new Rectangle2D.Double(startPoint.getX(), startPoint.getY(), m.getX() - startPoint.getX(), m.getY() - startPoint.getY());
          MeUtils.fixRect2DIP(r);
          if (pd.selectedTerminals.isEmpty()) {
            // Whoaaaa, what is this crazy exoticism
            unit.allUnits.stream().forEach((du) -> {
              du.getTerminals().stream().filter((t) -> (r.contains(new Point2D.Double(t.getViewX(), t.getViewY())))).forEach((t) -> {
                pd.selectedTerminals.add(t);
              });
            });
          } else {
            // It's weeeiiiirrrd
            HashSet<Terminal> terms = new HashSet<Terminal>();
            unit.allUnits.stream().forEach((du) -> {
              du.getTerminals().stream().filter((t) -> (r.contains(new Point2D.Double(t.getViewX(), t.getViewY())))).forEach((t) -> {
                terms.add(t);
              });
            });
            ArrayList<Terminal> a = new ArrayList<Terminal>(pd.selectedTerminals);
            ArrayList<Terminal> b = new ArrayList<Terminal>(terms);
            a.sort(new Comparator<Terminal>() {
              @Override
              public int compare(Terminal o1, Terminal o2) {
                return Double.compare(o1.getViewY(), o2.getViewY());
              }
            });
            b.sort(new Comparator<Terminal>() {
              @Override
              public int compare(Terminal o1, Terminal o2) {
                return Double.compare(o1.getViewY(), o2.getViewY());
              }
            });
            for (int i = 0; i < a.size() && i < b.size(); i++) {
              if (a.get(i) instanceof OutputTerminal) {
                if (b.get(i) instanceof InputTerminal) {
                  GDC.addConnection(((OutputTerminal)a.get(i)), ((InputTerminal)b.get(i)));
                } else {
                  // Dunno
                }
              } else if (a.get(i) instanceof InputTerminal) {
                if (b.get(i) instanceof OutputTerminal) {
                  GDC.addConnection(((OutputTerminal)b.get(i)), ((InputTerminal)a.get(i)));
                } else {
                  // Dunno
                }
              } else {
                //TODO Dunno; could support plain terminals, eventually
              }
            }
            pd.selectedTerminals.clear();
          }
        } else if (radioDisconnect.isSelected()) {
          if (m.equals(startPoint)) {
            break IfChain;
          }
          Rectangle2D r = new Rectangle2D.Double(startPoint.getX(), startPoint.getY(), m.getX() - startPoint.getX(), m.getY() - startPoint.getY());
          MeUtils.fixRect2DIP(r);
          // Whoaaaa, what is this crazy exoticism
          unit.allUnits.stream().forEach((du) -> {
            du.getTerminals().stream().filter((t) -> (r.contains(new Point2D.Double(t.getViewX(), t.getViewY())))).forEach((t) -> {
              if (t instanceof InputTerminal) {
                ((InputTerminal)t).breakConnection();
              } else if (t instanceof OutputTerminal) {
                ((OutputTerminal)t).breakConnection();
              }
            });
          });
        } else if (radioRemove.isSelected()) {
        } else if (radioReplace.isSelected()) {
        } else if (radioPlace.isSelected()) {
        }
        startPoint = null;
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
        if (radioMove.isSelected()) {
          if (!pd.selectedUnits.isEmpty()) {
            Point2D m = pd.ati.transform(new Point2D.Double(e.getX(), e.getY()), null);
            for (Unit u : pd.selectedUnits) {
              u.setViewLeft(m.getX());
              u.setViewTop(m.getY());
              u.recalcView();
            }
            changed = true;
            doRepaint();
          }
        } else if (radioConnect.isSelected()) {
        } else if (radioDisconnect.isSelected()) {
        } else if (radioRemove.isSelected()) {
        } else if (radioReplace.isSelected()) {
        } else if (radioPlace.isSelected()) {
          //TODO Resize
        }
      }

      @Override
      public void mouseMoved(MouseEvent e) {
      }
    });
    pd.addMouseWheelListener(new MouseWheelListener() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        if (radioMove.isSelected()) {
          if (!pd.selectedUnits.isEmpty()) {
            double scale = Math.pow(1.1, e.getPreciseWheelRotation());
            for (Unit u : pd.selectedUnits) {
              u.setViewWidth(u.getViewWidth() * scale);
              u.setViewHeight(u.getViewHeight() * scale);
              u.setViewFontSize((float)(u.getViewFontSize() * scale));
              u.recalcView();
            }
            changed = true;
            doRepaint();
          }
        } else if (radioConnect.isSelected()) {
        } else if (radioDisconnect.isSelected()) {
        } else if (radioRemove.isSelected()) {
        } else if (radioReplace.isSelected()) {
        } else if (radioPlace.isSelected()) {
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
    } else {
      DirectedCompositeUnit copy = (DirectedCompositeUnit)unit.copy();
      unitTypes.addElement(copy);
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
    btnValidate = new javax.swing.JButton();
    btnCheckColocation = new javax.swing.JButton();
    jPanel4 = new javax.swing.JPanel();
    radioMove = new javax.swing.JRadioButton();
    radioConnect = new javax.swing.JRadioButton();
    radioDisconnect = new javax.swing.JRadioButton();
    radioPlace = new javax.swing.JRadioButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    listUnitTypes = new javax.swing.JList();
    radioRemove = new javax.swing.JRadioButton();
    cbAutosource = new javax.swing.JCheckBox();
    radioReplace = new javax.swing.JRadioButton();
    jPanel2 = new javax.swing.JPanel();
    spinConnectionTheme = new javax.swing.JSpinner();
    jLabel4 = new javax.swing.JLabel();
    btnRedraw2 = new javax.swing.JButton();
    cbHideSourceCons = new javax.swing.JCheckBox();
    cbDrawIMU = new javax.swing.JCheckBox();

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
    btnSaveUnit.setToolTipText("Save to the main schema (not to file).");
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

    btnValidate.setText("Validate");
    btnValidate.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnValidateActionPerformed(evt);
      }
    });

    btnCheckColocation.setText("Check colocation");
    btnCheckColocation.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnCheckColocationActionPerformed(evt);
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
              .addComponent(jLabel2)
              .addComponent(jLabel3))
            .addGap(18, 18, 18)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(spinOutputs, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(spinInputs, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)))
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addComponent(btnRedraw)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 104, Short.MAX_VALUE)
            .addComponent(btnValidate))
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
            .addGap(0, 0, Short.MAX_VALUE)
            .addComponent(btnCheckColocation)))
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
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 205, Short.MAX_VALUE)
        .addComponent(btnCheckColocation)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(btnRedraw)
          .addComponent(btnValidate))
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
    radioMove.setToolTipText("Scroll while dragging to resize.  Side note: scroll will usually zoom.");

    groupTools.add(radioConnect);
    radioConnect.setText("(C)onnect");
    radioConnect.setToolTipText("Hold shift to keep connecting.  Drag to select/connect a bunch.  (Sorted top down.)");

    groupTools.add(radioDisconnect);
    radioDisconnect.setText("(D)isconnect");
    radioDisconnect.setToolTipText("Drag to disconnect a bunch.");

    groupTools.add(radioPlace);
    radioPlace.setText("(P)lace");

    listUnitTypes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    jScrollPane1.setViewportView(listUnitTypes);

    groupTools.add(radioRemove);
    radioRemove.setText("(R)emove");
    radioRemove.setToolTipText("Aim for the top left corner of a unit.");

    cbAutosource.setSelected(true);
    cbAutosource.setText("Autosource");

    groupTools.add(radioReplace);
    radioReplace.setText("R(e)place");
    radioReplace.setToolTipText("Aim for the top left corner of a unit.");

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
      jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
          .addGroup(jPanel4Layout.createSequentialGroup()
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(radioMove)
              .addComponent(radioConnect)
              .addComponent(radioDisconnect)
              .addComponent(radioRemove)
              .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(radioPlace)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbAutosource))
              .addComponent(radioReplace))
            .addGap(0, 52, Short.MAX_VALUE)))
        .addContainerGap())
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
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(radioRemove)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(radioReplace)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(radioPlace)
          .addComponent(cbAutosource))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
        .addContainerGap())
    );

    jTabbedPane1.addTab("Tools", jPanel4);

    spinConnectionTheme.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1, 1));
    spinConnectionTheme.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        spinConnectionThemeStateChanged(evt);
      }
    });

    jLabel4.setText("Connection theme");

    btnRedraw2.setText("Redraw");
    btnRedraw2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnRedraw2ActionPerformed(evt);
      }
    });

    cbHideSourceCons.setText("Hide source cons");
    cbHideSourceCons.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        cbHideSourceConsStateChanged(evt);
      }
    });
    cbHideSourceCons.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cbHideSourceConsActionPerformed(evt);
      }
    });

    cbDrawIMU.setText("Draw internal meta unit");
    cbDrawIMU.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        cbDrawIMUStateChanged(evt);
      }
    });

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel2Layout.createSequentialGroup()
            .addComponent(jLabel4)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(spinConnectionTheme, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(btnRedraw2)
          .addComponent(cbHideSourceCons)
          .addComponent(cbDrawIMU))
        .addContainerGap(46, Short.MAX_VALUE))
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(spinConnectionTheme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel4))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(cbHideSourceCons)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(cbDrawIMU)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 300, Short.MAX_VALUE)
        .addComponent(btnRedraw2)
        .addContainerGap())
    );

    jTabbedPane1.addTab("View", jPanel2);

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
      unit.recalcView();
      changed = true;
      doRepaint();
    }
  }//GEN-LAST:event_spinInputsStateChanged

  private void spinOutputsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinOutputsStateChanged
    if (!loading) {
      unit.resizeTerminals((Integer)spinInputs.getValue(), (Integer)spinOutputs.getValue());
      unit.recalcView();
      changed = true;
      doRepaint();
    }
  }//GEN-LAST:event_spinOutputsStateChanged

  private void btnRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRunActionPerformed
    try {
      if (!doValidate()) {
        return;
      }
      new FrameRunDirectedCompositeUnit(unitTypes, unit.copy(), unit).setVisible(true);
    } catch (IOException ex) {
      Logger.getLogger(FrameEditDirectedCompositeUnit.class.getName()).log(Level.SEVERE, null, ex);
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(FrameEditDirectedCompositeUnit.class.getName()).log(Level.SEVERE, null, ex);
    }
  }//GEN-LAST:event_btnRunActionPerformed

  private void btnRedrawActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRedrawActionPerformed
    doRepaint();
  }//GEN-LAST:event_btnRedrawActionPerformed

  private void showColocationWarning() {
    HashSet<Unit> colocating = DeviceEngine.findColocatingUnits(unit);
    pd.selectedUnits.clear();
    pd.selectedUnits.addAll(colocating);
    doRepaint();
    JOptionPane.showMessageDialog(this, "Warning: colocating units present.  Highlighted.");
  }
  
  private boolean doValidate() {
    try {
      DeviceEngine.validateUnit(unit);
    } catch (Exception ex) {
      switch (ex.getMessage()) {
        case "disconnected terminal":
          HashSet<Terminal> disconnected = DeviceEngine.findDisconnectedTerminals(unit);
          pd.selectedTerminals.clear();
          pd.selectedTerminals.addAll(disconnected);
          doRepaint();
          //TODO Maybe shouldn't include the metaterminals?
          JOptionPane.showMessageDialog(this, "Disconnected terminals present.  Highlighted.");
          return false;
        case "has external terminals":
          JOptionPane.showMessageDialog(this, "Unit has external terminals (which means you can't run it, by itself).");
          return false;
        case "colocating units":
          showColocationWarning();
          return true;
      }
    }
    return true;
  }
  
  private void btnValidateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnValidateActionPerformed
    doValidate();
  }//GEN-LAST:event_btnValidateActionPerformed

  private void spinConnectionThemeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinConnectionThemeStateChanged
    pd.connectionLineMode = (Integer)spinConnectionTheme.getValue();
    doRepaint();
  }//GEN-LAST:event_spinConnectionThemeStateChanged

  private void btnRedraw2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRedraw2ActionPerformed
    doRepaint();
  }//GEN-LAST:event_btnRedraw2ActionPerformed

  private void cbHideSourceConsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbHideSourceConsStateChanged
    pd.hideSourceConnections = cbHideSourceCons.isSelected();
    doRepaint();
  }//GEN-LAST:event_cbHideSourceConsStateChanged

  private void cbDrawIMUStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbDrawIMUStateChanged
    pd.drawIMU = cbDrawIMU.isSelected();
    doRepaint();
  }//GEN-LAST:event_cbDrawIMUStateChanged

  private void cbHideSourceConsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbHideSourceConsActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_cbHideSourceConsActionPerformed

  private void btnCheckColocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckColocationActionPerformed
    if (!DeviceEngine.findColocatingUnits(unit).isEmpty()) {
      //TODO Yeah, inefficient.
      showColocationWarning();
    }
  }//GEN-LAST:event_btnCheckColocationActionPerformed

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
  private javax.swing.JButton btnCheckColocation;
  private javax.swing.JButton btnRedraw;
  private javax.swing.JButton btnRedraw2;
  private javax.swing.JButton btnRun;
  private javax.swing.JButton btnSaveUnit;
  private javax.swing.JButton btnValidate;
  private javax.swing.JCheckBox cbAutosource;
  private javax.swing.JCheckBox cbDrawIMU;
  private javax.swing.JCheckBox cbHideSourceCons;
  private javax.swing.ButtonGroup groupTools;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JPanel jPanel4;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JSplitPane jSplitPane1;
  private javax.swing.JTabbedPane jTabbedPane1;
  private javax.swing.JList listUnitTypes;
  private javax.swing.JRadioButton radioConnect;
  private javax.swing.JRadioButton radioDisconnect;
  private javax.swing.JRadioButton radioMove;
  private javax.swing.JRadioButton radioPlace;
  private javax.swing.JRadioButton radioRemove;
  private javax.swing.JRadioButton radioReplace;
  private javax.swing.JSpinner spinConnectionTheme;
  private javax.swing.JSpinner spinInputs;
  private javax.swing.JSpinner spinOutputs;
  private javax.swing.JTextField textUnitName;
  // End of variables declaration//GEN-END:variables
}
