package checkers;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class ChineseCheckersVisitor {
    private Graphics2D g;
    private double componentH;
    private double componentW;
    private double radius;

    public ChineseCheckersVisitor(Graphics2D g, double componentH, double componentW){
        this.g = g;
        this.componentH = componentH;
        this.componentW = componentW;
        this.radius = (Math.min(componentH, componentW)) / 34;
    }

    public void visitHighlight(int xi, int yi, Color color){
        double[] coords = pointConverter(xi, yi);
        Ellipse2D e = new Ellipse2D.Double(coords[0] - (radius * 1.1), coords[1] - (radius * 1.1), radius * 2.2, radius * 2.2);
        g.setColor(color);
        g.fill(e);
    }

    public void visitBlue(int xi, int yi){
        double[] coords = pointConverter(xi, yi);
        Ellipse2D e = new Ellipse2D.Double(coords[0] - (radius), coords[1] - (radius), radius * 2, radius * 2);
        g.setColor(Color.BLUE);
        g.fill(e);
    }

    public void visitMagenta(int xi, int yi){
        double[] coords = pointConverter(xi, yi);
        Ellipse2D e = new Ellipse2D.Double(coords[0] - (radius), coords[1] - (radius), radius * 2, radius * 2);
        g.setColor(Color.MAGENTA);
        g.fill(e);
    }

    public void visitYellow(int xi, int yi){
        double[] coords = pointConverter(xi, yi);
        Ellipse2D e = new Ellipse2D.Double(coords[0] - (radius), coords[1] - (radius), radius * 2, radius * 2);
        g.setColor(Color.YELLOW);
        g.fill(e);
    }

    public void visitRed(int xi, int yi){
        double[] coords = pointConverter(xi, yi);
        Ellipse2D e = new Ellipse2D.Double(coords[0] - (radius), coords[1] - (radius), radius * 2, radius * 2);
        g.setColor(Color.RED);
        g.fill(e);
    }

    public void visitGreen(int xi, int yi){
        double[] coords = pointConverter(xi, yi);
        Ellipse2D e = new Ellipse2D.Double(coords[0] - (radius), coords[1] - (radius), radius * 2, radius * 2);
        g.setColor(Color.GREEN);
        g.fill(e);
    }

    public void visitCyan(int xi, int yi){
        double[] coords = pointConverter(xi, yi);
        Ellipse2D e = new Ellipse2D.Double(coords[0] - (radius), coords[1] - (radius), radius * 2, radius * 2);
        g.setColor(Color.CYAN);
        g.fill(e);
    }

    public void visitBackground(int xi, int yi){
        double[] coords = pointConverter(xi, yi);
        Ellipse2D e = new Ellipse2D.Double(coords[0] - (radius), coords[1] - (radius), radius * 2, radius * 2);
        g.setColor(Color.BLACK);
        g.fill(e);
    }

    public double[] pointConverter(int xi, int yi){
        double minDimension = Math.min(componentH, componentW);
        double unitR = 1 / (32 * Math.cos(Math.PI / 6) + 2);  // "r" in formula

        double xn = 0.5 + unitR * xi;
        double yn = 0.5 + unitR * Math.tan(Math.PI / 3) * yi;
        double xs = componentW / 2 + (xn - 0.5) * minDimension;
        double ys = componentH / 2 + (-yn + 0.5) * minDimension;

        return new double[] {xs, ys};
    }
}
