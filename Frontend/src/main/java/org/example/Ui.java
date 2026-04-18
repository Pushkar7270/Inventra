package org.example;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Factory for consistently-styled Swing components. */
public final class UI {
    private UI() {}

    public static JPanel panel(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(Theme.BG_PANEL);
        p.setBorder(BorderFactory.createEmptyBorder());
        return p;
    }

    public static JPanel deepPanel(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(Theme.BG_DEEP);
        p.setBorder(BorderFactory.createEmptyBorder());
        return p;
    }

    public static JPanel card(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(Theme.BG_PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        return p;
    }

    public static JLabel title(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_TITLE);
        l.setForeground(Theme.TEXT_PRI);
        return l;
    }

    public static JLabel heading(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_HEAD);
        l.setForeground(Theme.TEXT_PRI);
        return l;
    }

    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_BODY);
        l.setForeground(Theme.TEXT_SEC);
        return l;
    }

    public static JLabel accentLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_HEAD);
        l.setForeground(Theme.ACCENT);
        return l;
    }

    public static JTextField field(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(Theme.FONT_BODY);
        tf.setForeground(Theme.TEXT_PRI);
        tf.setBackground(Theme.BG_ROW);
        tf.setCaretColor(Theme.ACCENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        tf.putClientProperty("JTextField.placeholderText", placeholder);
        return tf;
    }

    public static JButton btnPrimary(String text) {
        return filledButton(text, Theme.ACCENT, Theme.BG_DEEP);
    }

    public static JButton btnAmber(String text) {
        return filledButton(text, Theme.ACCENT2, Theme.BG_DEEP);
    }

    public static JButton btnDanger(String text) {
        return filledButton(text, Theme.DANGER, Theme.BG_DEEP);
    }

    public static JButton btnGhost(String text) {
        JButton b = new JButton(text);
        b.setFont(Theme.FONT_BODY);
        b.setForeground(Theme.TEXT_PRI);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setBorder(ghostBorder());
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        installHoverRepaint(b);
        return b;
    }

    public static JButton navBtn(String text) {
        return new NavButton(text);
    }

    public static void styleTable(JTable t) {
        t.setBackground(Theme.BG_PANEL);
        t.setForeground(Theme.TEXT_PRI);
        t.setFont(Theme.FONT_BODY);
        t.setRowHeight(36);
        t.setGridColor(Theme.BORDER);
        t.setSelectionBackground(new Color(0, 200, 160, 40));
        t.setSelectionForeground(Theme.TEXT_PRI);
        t.setShowVerticalLines(false);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setFillsViewportHeight(true);

        JTableHeader header = t.getTableHeader();
        header.setBackground(Theme.BG_DEEP);
        header.setForeground(Theme.TEXT_SEC);
        header.setFont(Theme.FONT_SMALL);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));
        Component renderer = header.getDefaultRenderer();
        if (renderer instanceof DefaultTableCellRenderer) {
            ((DefaultTableCellRenderer) renderer).setHorizontalAlignment(SwingConstants.LEFT);
        }

        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                setFont(Theme.FONT_BODY);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                if (sel) {
                    setBackground(new Color(0, 200, 160, 50));
                    setForeground(Theme.TEXT_PRI);
                } else {
                    setBackground(row % 2 == 0 ? Theme.BG_PANEL : Theme.BG_ROW);
                    setForeground(Theme.TEXT_PRI);
                }
                return this;
            }
        });
    }

    public static JScrollPane scrollPane(JComponent c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1, true));
        sp.getViewport().setBackground(Theme.BG_PANEL);
        sp.setBackground(Theme.BG_PANEL);
        return sp;
    }

    public static JSeparator separator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(Theme.BORDER);
        sep.setBackground(Theme.BORDER);
        return sep;
    }

    private static JButton filledButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text) {
            private boolean hover = false;

            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                d.height = Math.max(d.height, 44);
                return d;
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hover ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(Theme.FONT_BODY);
        b.setForeground(fg);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { setHover(b, true); }
            @Override public void mouseExited(MouseEvent e) { setHover(b, false); }

            private void setHover(JButton button, boolean h) {
                try {
                    java.lang.reflect.Field f = button.getClass().getDeclaredField("hover");
                    f.setAccessible(true);
                    f.setBoolean(button, h);
                } catch (Exception ignored) {
                    // ignore
                }
                button.repaint();
            }
        });
        return b;
    }

    private static Border ghostBorder() {
        Insets ins = UIManager.getInsets("Button.margin");
        if (ins == null) {
            ins = new Insets(8, 12, 8, 12);
        }
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(ins.top, ins.left, ins.bottom, ins.right));
    }

    private static void installHoverRepaint(final JComponent c) {
        c.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { c.repaint(); }
            @Override public void mouseExited(MouseEvent e) { c.repaint(); }
        });
    }

    private static final class NavButton extends JButton {
        private boolean selected = false;
        private boolean hover = false;

        NavButton(String text) {
            super(text);
            setFont(Theme.FONT_BODY);
            setForeground(Theme.TEXT_SEC);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
            repaint();
        }

        public boolean isSelected() {
            return selected;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (selected) {
                g2.setColor(new Color(0, 200, 160, 30));
                g2.fillRoundRect(4, 2, getWidth() - 8, getHeight() - 4, 10, 10);
                g2.setColor(Theme.ACCENT);
                g2.setStroke(new BasicStroke(3f));
                g2.drawLine(6, 6, 6, getHeight() - 6);
            } else if (hover) {
                g2.setColor(new Color(255, 255, 255, 10));
                g2.fillRoundRect(4, 2, getWidth() - 8, getHeight() - 4, 10, 10);
            }
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            d.height = Math.max(d.height, 40);
            return d;
        }
    }
}

