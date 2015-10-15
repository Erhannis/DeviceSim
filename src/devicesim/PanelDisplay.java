/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author erhannis
 */
public class PanelDisplay extends javax.swing.JPanel {
    public HashSet<Unit> units = new HashSet<Unit>();
  
    public Unit selectedUnit = null;
    
    public boolean skipRender = false;
    public AffineTransform at = new AffineTransform();
    public AffineTransform ati = new AffineTransform();
    public final Font FONT = new Font("Monospaced", 0, 14);
    
    public static final int CLMODE_DIRECT = 0;
    public static final int CLMODE_SQUARE = 1;

    public int connectionLineMode = CLMODE_DIRECT;
    
    public ArrayList<Line2D.Double> getConnectionLines(double ax, double ay, double bx, double by) {
      ArrayList<Line2D.Double> lines = new ArrayList<Line2D.Double>();
      switch (connectionLineMode) {
        case CLMODE_SQUARE:
          if (ax == bx || ay == by) {
            lines.add(new Line2D.Double(ax, ay, bx, by));
            return lines;
          }
          double slopeFactor = Math.atan(Math.abs(by - ay) / Math.abs(bx - ax)) / (Math.PI / 2);
          double midpoint = ax + ((bx - ax) * slopeFactor);
          lines.add(new Line2D.Double(ax, ay, midpoint, ay));
          lines.add(new Line2D.Double(midpoint, ay, midpoint, by));
          lines.add(new Line2D.Double(midpoint, by, bx, by));
          return lines;
        case CLMODE_DIRECT:
        default:
          lines.add(new Line2D.Double(ax, ay, bx, by));
          return lines;
      }
    }
    
    @Override
    protected void paintComponent(Graphics g0) {
      if (skipRender) {
        return;
      }
      super.paintComponent(g0); //To change body of generated methods, choose Tools | Templates.
      Graphics2D g = (Graphics2D)g0;
      g.setFont(FONT);
      g.setStroke(new BasicStroke(0));
      AffineTransform saveAT = g.getTransform();
      g.transform(at);
      
      //TODO Render better
      for (Unit u : units) {
        //TODO Make more efficient; cache stuff, etc.
        g.draw(new Rectangle.Double(u.getViewLeft(), u.getViewTop(), u.getViewWidth(), u.getViewHeight()));
        g.setFont(FONT.deriveFont(u.getViewFontSize()));
        g.drawString(u.getName(), (float)u.getViewLeft(), (float)u.getViewTop());
        if (u instanceof DirectedUnit) {
          //TODO Yes, I know this is cheating.
          ArrayList<OutputTerminal> outputs = ((DirectedUnit)u).getOutputs();
          for (int i = 0; i < outputs.size(); i++) {
            double ax = u.getViewLeft() + u.getViewWidth();
            double ay = u.getViewTop() + (((i + 0.5) / outputs.size()) * u.getViewHeight());
            double socketRadius = 0.5 * 0.4 * (u.getViewHeight() / outputs.size());
            g.draw(new Ellipse2D.Double(ax - socketRadius, ay - socketRadius, 2 * socketRadius, 2 * socketRadius));
            OutputTerminal ot = outputs.get(i);
            if (ot.getConnection() != null) {
              for (InputTerminal it : ot.getConnection().getOutputs()) {
                DirectedUnit bu = it.getUnit();
                ArrayList<InputTerminal> bInputs = bu.getInputs();
                double bx = bu.getViewLeft();
                double by = bu.getViewTop() + (((bInputs.indexOf(it) + 0.5) / bInputs.size()) * bu.getViewHeight());
                double bSocketRadius = 0.5 * 0.4 * (bu.getViewHeight() / bInputs.size());
                g.draw(new Ellipse2D.Double(bx - bSocketRadius, by - bSocketRadius, 2 * bSocketRadius, 2 * bSocketRadius));
                //g.draw(new Line2D.Double(ax, ay, bx, by));
                for (Line2D.Double line : getConnectionLines(ax, ay, bx, by)) {
                  g.draw(line);
                }
              }
            }
          }
        }
      }
      
      g.setTransform(saveAT);
    }
  
  /**
   * Creates new form PanelDisplay
   */
  public PanelDisplay() {
    initComponents();
    this.setBackground(Color.WHITE);
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    addMouseWheelListener(new java.awt.event.MouseWheelListener() {
      public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
        formMouseWheelMoved(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 400, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 300, Short.MAX_VALUE)
    );
  }// </editor-fold>//GEN-END:initComponents

  private void formMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_formMouseWheelMoved
    double scale = Math.pow(1.1, -evt.getPreciseWheelRotation());
    at.preConcatenate(AffineTransform.getTranslateInstance(-evt.getX(), -evt.getY()));
    at.preConcatenate(AffineTransform.getScaleInstance(scale, scale));
    at.preConcatenate(AffineTransform.getTranslateInstance(evt.getX(), evt.getY()));
      try {
        ati = at.createInverse();
      } catch (NoninvertibleTransformException ex) {
        Logger.getLogger(PanelDisplay.class.getName()).log(Level.SEVERE, null, ex);
      }
    repaint();
  }//GEN-LAST:event_formMouseWheelMoved


  // Variables declaration - do not modify//GEN-BEGIN:variables
  // End of variables declaration//GEN-END:variables
}
