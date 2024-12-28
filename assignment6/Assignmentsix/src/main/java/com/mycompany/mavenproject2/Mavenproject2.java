package com.mycompany.mavenproject2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Mavenproject2 extends JFrame {
    private Color selectedColor = Color.BLACK;
    private String selectedShape = "Line";
    private Point startPoint, endPoint;
    private ArrayList<Shape> shapes = new ArrayList<>();
    private ArrayList<Color> shapeColors = new ArrayList<>();
    private ArrayList<Boolean> shapeFillFlags = new ArrayList<>(); // Track fill status per shape
    private boolean fillShapes = false; // Temporary fill flag for the current shape
    private boolean dottedLines = false; // Flag to determine if lines should be dotted

    public Mavenproject2() {
        setTitle("Paint Brush");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel toolbar = new JPanel();
        toolbar.setLayout(new FlowLayout());

        String[] shapeOptions = {"Line", "Rectangle", "Oval", "Pencil", "Triangle", "Square"};
        for (String shape : shapeOptions) {
            JButton shapeButton = new JButton(shape);
            shapeButton.addActionListener(e -> selectedShape = shape);
            toolbar.add(shapeButton);
        }

        JButton colorButton = new JButton("Choose Color");
        colorButton.addActionListener(e -> {
            Color color = JColorChooser.showDialog(null, "Choose a color", selectedColor);
            if (color != null) {
                selectedColor = color;
            }
        });
        toolbar.add(colorButton);

        JCheckBox fillCheckBox = new JCheckBox("Fill");
        fillCheckBox.addActionListener(e -> fillShapes = fillCheckBox.isSelected());
        toolbar.add(fillCheckBox);

        JCheckBox dottedCheckBox = new JCheckBox("Dotted ");
        dottedCheckBox.addActionListener(e -> dottedLines = dottedCheckBox.isSelected());
        toolbar.add(dottedCheckBox);

        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> {
            if (!shapes.isEmpty()) {
                shapes.remove(shapes.size() - 1);
                shapeColors.remove(shapeColors.size() - 1);
                shapeFillFlags.remove(shapeFillFlags.size() - 1);
                repaint();
            }
        });
        toolbar.add(undoButton);

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> {
            shapes.clear();
            shapeColors.clear();
            shapeFillFlags.clear();
            repaint();
        });
        toolbar.add(clearButton);

        add(toolbar, BorderLayout.NORTH);

        JPanel drawingArea = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                for (int i = 0; i < shapes.size(); i++) {
                    g2d.setColor(shapeColors.get(i));
                    if (shapes.get(i) instanceof java.awt.geom.Line2D) {
                        if (dottedLines) {
                            float[] dashPattern = {10, 10};
                            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dashPattern, 0));
                        } else {
                            g2d.setStroke(new BasicStroke(2));
                        }
                        g2d.draw(shapes.get(i));
                    } else {
                        if (shapeFillFlags.get(i)) {
                            g2d.fill(shapes.get(i));
                        } else {
                            g2d.draw(shapes.get(i));
                        }
                    }
                }
            }
        };
        drawingArea.setBackground(Color.WHITE);

        drawingArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                endPoint = e.getPoint();
                Shape shape = null;

                switch (selectedShape) {
                    case "Line":
                        shape = new java.awt.geom.Line2D.Double(startPoint, endPoint);
                        break;
                    case "Rectangle":
                        shape = new Rectangle(Math.min(startPoint.x, endPoint.x),
                                Math.min(startPoint.y, endPoint.y),
                                Math.abs(startPoint.x - endPoint.x),
                                Math.abs(startPoint.y - endPoint.y));
                        break;
                    case "Square":
                        int side = Math.min(Math.abs(startPoint.x - endPoint.x), Math.abs(startPoint.y - endPoint.y));
                        shape = new Rectangle(Math.min(startPoint.x, endPoint.x),
                                Math.min(startPoint.y, endPoint.y), side, side);
                        break;
                    case "Oval":
                        shape = new java.awt.geom.Ellipse2D.Double(Math.min(startPoint.x, endPoint.x),
                                Math.min(startPoint.y, endPoint.y),
                                Math.abs(startPoint.x - endPoint.x),
                                Math.abs(startPoint.y - endPoint.y));
                        break;
                    case "Triangle":
                        int x1 = startPoint.x;
                        int y1 = startPoint.y;
                        int x2 = endPoint.x;
                        int y2 = endPoint.y;
                        int x3 = x1 + (x2 - x1) / 2;
                        int y3 = y1 - Math.abs(x2 - x1);
                        Polygon triangle = new Polygon(new int[]{x1, x2, x3}, new int[]{y1, y2, y3}, 3);
                        shape = triangle;
                        break;
                }

                if (shape != null) {
                    shapes.add(shape);
                    shapeColors.add(selectedColor);
                    shapeFillFlags.add(fillShapes); // Store fill status for this shape
                    repaint();
                }
            }
        });

        drawingArea.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedShape.equals("Pencil")) {
                    endPoint = e.getPoint();
                    Shape shape = new java.awt.geom.Line2D.Double(startPoint, endPoint);
                    shapes.add(shape);
                    shapeColors.add(selectedColor);
                    shapeFillFlags.add(false); // Pencil strokes are not filled
                    startPoint = endPoint;
                    repaint();
                } else {
                    repaint();
                }
            }
        });

        add(drawingArea, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Mavenproject2 frame = new Mavenproject2();
            frame.setVisible(true);
        });
    }
}
