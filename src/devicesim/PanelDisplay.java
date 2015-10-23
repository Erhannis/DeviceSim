/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicesim;

import devicesim.units.defaults.DirectedCompositeUnit;
import devicesim.units.defaults.InternalMetaUnit;
import devicesim.units.defaults.SourceHigh;
import devicesim.units.defaults.SourceLow;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import mathnstuff.MeUtils;

/**
 *
 * @author erhannis
 */
public class PanelDisplay extends javax.swing.JPanel {
    public HashSet<? extends Unit> rootUnits = new HashSet<Unit>();
  
    public HashSet<Unit> selectedUnits = new HashSet<Unit>();
    public HashSet<Terminal> selectedTerminals = new HashSet<Terminal>();
    
    public boolean skipRender = false;
    public AffineTransform at = new AffineTransform();
    public AffineTransform ati = new AffineTransform();
    public static final Font FONT = new Font("Monospaced", 0, 14);
    
    public static final int CLMODE_DIRECT = 0;
    public static final int CLMODE_SQUARE = 1;

    public int connectionLineMode = CLMODE_DIRECT;
    public boolean hideSourceConnections = false;
    public boolean hideText = false;
    public boolean drawIMU = false;
    public boolean recursiveRender = false;
    
    private static final Color COLOR_BACKGROUND = Color.LIGHT_GRAY;
    private static final Color COLOR_NORMAL = Color.BLACK;
    private static final Color COLOR_HIGHLIGHT = Color.CYAN;
    private static final Color COLOR_HIGH = Color.GREEN;
    private static final Color COLOR_LOW = Color.BLACK;
    
    private static final double STATE_SPLIT_POINT = 0.5;
    
    public Path2D getConnectionPath(double ax, double ay, double bx, double by) {
      Path2D path = new Path2D.Double();
      switch (connectionLineMode) {
        case CLMODE_SQUARE:
          if (ax == bx || ay == by) {
            path.moveTo(ax, ay);
            path.lineTo(bx, by);
            return path;
          }
          double slopeFactor = Math.atan(Math.abs(by - ay) / Math.abs(bx - ax)) / (Math.PI / 2);
          double midpoint = ax + ((bx - ax) * slopeFactor);
          path.moveTo(ax, ay);
          path.lineTo(midpoint, ay);
          path.lineTo(midpoint, by);
          path.lineTo(bx, by);
          return path;
        case CLMODE_DIRECT:
        default:
          path.moveTo(ax, ay);
          path.lineTo(bx, by);
          return path;
      }
    }

    private static AffineTransform getInternalsTransform(Unit u) {
      AffineTransform t2 = new AffineTransform();
      t2.translate(u.getViewLeft(), u.getViewTop());
      double scale = u.getViewWidth() / 400;
      t2.scale(scale, scale);
      t2.translate(-50, -80);
      return t2;
    }
    
    public void render(Graphics2D g, AffineTransform t, HashSet<? extends Unit> units, boolean checkSelected) {
      AffineTransform saveAT = g.getTransform();
      g.transform(t);
      
      g.setFont(FONT);
      g.setColor(COLOR_NORMAL);
      g.setStroke(new BasicStroke(0));
      
      //TODO Render better
      for (Unit u : units) {
        // Uncomment for resizing for recursive redraw.
//        if (u instanceof InternalMetaUnit) {
//          InternalMetaUnit imu = (InternalMetaUnit)u;
//          imu.setViewHeight((imu.getViewWidth() / -30) * 20);
//          imu.recalcView();
//        }
        if (recursiveRender && u instanceof DirectedCompositeUnit) {
          //TODO Yeah yeah cheating
          AffineTransform t2 = getInternalsTransform(u);
          render(g, t2, ((DirectedCompositeUnit)u).allUnits, false);
        }
        //TODO Make more efficient; cache stuff, etc.
        if (checkSelected && selectedUnits.contains(u)) {
          g.setColor(COLOR_HIGHLIGHT);
        }
        if (u instanceof InternalMetaUnit) {
          if (drawIMU) {
            g.draw(MeUtils.fixRect2DIP(new Rectangle.Double(u.getViewLeft(), u.getViewTop(), u.getViewWidth(), u.getViewHeight())));
            if (!hideText) {
              g.setFont(FONT.deriveFont(u.getViewFontSize()));
              g.drawString(u.getName(), (float)u.getViewLeft(), (float)u.getViewTop());
            }
          }
        } else {
          g.draw(new Rectangle.Double(u.getViewLeft(), u.getViewTop(), u.getViewWidth(), u.getViewHeight()));
          if (!hideText) {
            g.setFont(FONT.deriveFont(u.getViewFontSize()));
            g.drawString(u.getName(), (float)u.getViewLeft(), (float)u.getViewTop());
          }
        }
        if (checkSelected && selectedUnits.contains(u)) {
          g.setColor(COLOR_NORMAL);
        }
        if (u instanceof DirectedUnit) {
          //TODO Yes, I know this is cheating.
          if (hideSourceConnections && ((u instanceof SourceHigh) || (u instanceof SourceLow))) {
            continue;
          }
          ArrayList<OutputTerminal> outputs = ((DirectedUnit)u).getOutputs();
          for (int i = 0; i < outputs.size(); i++) {
            OutputTerminal ot = outputs.get(i);
            double oax = ot.getViewX();
            double oay = ot.getViewY();
            double oSocketRadius = ot.getViewSocketRadius();
            if (checkSelected && selectedTerminals.contains(ot)) {
              g.setColor(COLOR_HIGHLIGHT);
              g.draw(new Ellipse2D.Double(oax - oSocketRadius, oay - oSocketRadius, 2 * oSocketRadius, 2 * oSocketRadius));
              g.setColor(COLOR_NORMAL);
            } else {
              g.draw(new Ellipse2D.Double(oax - oSocketRadius, oay - oSocketRadius, 2 * oSocketRadius, 2 * oSocketRadius));
            }
            if (hideSourceConnections && (u instanceof InternalMetaUnit) && i < 2) {
              continue;
            }
            //TODO Not sure about this one.  Maybe a continuum of values?
            if (ot.getValue() >= STATE_SPLIT_POINT) {
              g.setColor(COLOR_HIGH);
            } else {
              g.setColor(COLOR_LOW);
            }
            if (ot.getConnection() != null) {
              for (InputTerminal it : ot.getConnection().getOutputs()) {
                double ibx = it.getViewX();
                double iby = it.getViewY();
                g.draw(getConnectionPath(oax, oay, ibx, iby));
              }
            }
            g.setColor(COLOR_NORMAL);
          }
          
          ArrayList<InputTerminal> inputs = ((DirectedUnit)u).getInputs();
          for (int i = 0; i < inputs.size(); i++) {
            InputTerminal it = inputs.get(i);
            double iax = it.getViewX();
            double iay = it.getViewY();
            double iSocketRadius = it.getViewSocketRadius();
            if (checkSelected && selectedTerminals.contains(it)) {
              g.setColor(COLOR_HIGHLIGHT);
              g.draw(new Ellipse2D.Double(iax - iSocketRadius, iay - iSocketRadius, 2 * iSocketRadius, 2 * iSocketRadius));
              g.setColor(COLOR_NORMAL);
            } else {
              g.draw(new Ellipse2D.Double(iax - iSocketRadius, iay - iSocketRadius, 2 * iSocketRadius, 2 * iSocketRadius));
            }
          }          
        }
      }
      
      g.setTransform(saveAT);
    }
    
    @Override
    protected void paintComponent(Graphics g0) {
      if (skipRender) {
        return;
      }
      super.paintComponent(g0); //To change body of generated methods, choose Tools | Templates.
      Graphics2D g = (Graphics2D)g0;
      render(g, at, rootUnits, true);
    }
  
  /**
   * Creates new form PanelDisplay
   */
  public PanelDisplay() {
    initComponents();
    this.setBackground(COLOR_BACKGROUND);
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
    if (selectedUnits.isEmpty()) { // Otherwise we're resizing an element
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
    }
  }//GEN-LAST:event_formMouseWheelMoved


  // Variables declaration - do not modify//GEN-BEGIN:variables
  // End of variables declaration//GEN-END:variables
}
