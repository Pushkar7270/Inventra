package org.example;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        // Install a look-and-feel BEFORE creating any Swing component
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // ignore
        }

        // Global FlatLaf tweaks to match our custom palette
        UIManager.put("Panel.background",           org.example.Theme.BG_PANEL);
        UIManager.put("OptionPane.background",      org.example.Theme.BG_PANEL);
        UIManager.put("OptionPane.messageForeground", org.example.Theme.TEXT_PRI);
        UIManager.put("TextField.background",       org.example.Theme.BG_ROW);
        UIManager.put("TextField.foreground",       org.example.Theme.TEXT_PRI);
        UIManager.put("TextField.caretForeground",  org.example.Theme.ACCENT);
        UIManager.put("TextField.selectionBackground", org.example.Theme.ACCENT);
        UIManager.put("Label.foreground",           org.example.Theme.TEXT_SEC);
        UIManager.put("Button.arc",                 8);
        UIManager.put("Component.arc",              8);
        UIManager.put("ScrollBar.thumb",            org.example.Theme.BG_ROW);
        UIManager.put("ScrollBar.width",            8);
        UIManager.put("TitlePane.background",       org.example.Theme.BG_DEEP);
        UIManager.put("TitlePane.foreground",       org.example.Theme.TEXT_PRI);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Inventra");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setMinimumSize(new Dimension(1100, 700));
            frame.setPreferredSize(new Dimension(1280, 800));
            frame.setContentPane(new MainWindow());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

class MainWindow extends JPanel {

    // Pages
    private final org.example.PosPanel posPanel       = new org.example.PosPanel();
    private final org.example.InventoryPanel inventoryPanel = new org.example.InventoryPanel();
    private final ExportsPanel   exportsPanel   = new ExportsPanel();

    // Nav buttons (kept as fields to toggle active state)
    private final JButton btnPos       = UI.navBtn("🖥   POS Terminal");
    private final JButton btnInventory = UI.navBtn("📦  Inventory");
    private final JButton btnExports   = UI.navBtn("📊  Reports & Exports");

    private final JLabel lblPageTitle = UI.heading("POS Terminal");
    private final JPanel contentArea  = UI.deepPanel(new CardLayout());

    public MainWindow() {
        setLayout(new BorderLayout());
        setBackground(org.example.Theme.BG_DEEP);

        add(buildSidebar(), BorderLayout.WEST);
        add(buildMain(),    BorderLayout.CENTER);

        contentArea.add(posPanel,       "pos");
        contentArea.add(inventoryPanel, "inventory");
        contentArea.add(exportsPanel,   "exports");

        activateNav(btnPos, "pos", "POS Terminal");
    }

    // ── Sidebar ─────────────────────────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel side = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Right-side border line
                g.setColor(Theme.BORDER);
                g.fillRect(getWidth()-1, 0, 1, getHeight());
            }
        };
        side.setLayout(new BorderLayout());
        side.setBackground(org.example.Theme.BG_DEEP);
        side.setPreferredSize(new Dimension(220, 0));

        // Brand header
        JPanel brand = UI.deepPanel(new BorderLayout());
        brand.setBorder(BorderFactory.createEmptyBorder(28, 20, 16, 20));
        JLabel logo = new JLabel("📦 INVENTRA");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        logo.setForeground(Theme.ACCENT);
        JLabel sub = UI.label("Inventory & POS System");
        sub.setFont(org.example.Theme.FONT_SMALL);
        brand.add(logo, BorderLayout.NORTH);
        brand.add(sub,  BorderLayout.SOUTH);

        // Nav buttons
        JPanel nav = UI.deepPanel(new GridLayout(0, 1, 0, 4));
        nav.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        nav.add(btnPos);
        nav.add(btnInventory);
        nav.add(btnExports);

        btnPos.addActionListener(e       -> activateNav(btnPos,       "pos",       "POS Terminal"));
        btnInventory.addActionListener(e -> { activateNav(btnInventory,"inventory","Inventory"); inventoryPanel.refresh(); });
        btnExports.addActionListener(e   -> activateNav(btnExports,   "exports",   "Reports & Exports"));

        // Status badge bottom
        JPanel status = UI.deepPanel(new BorderLayout());
        status.setBorder(BorderFactory.createEmptyBorder(16, 18, 20, 18));
        JLabel dot = new JLabel("● System Online");
        dot.setFont(Theme.FONT_SMALL);
        dot.setForeground(Theme.ACCENT);
        status.add(dot, BorderLayout.SOUTH);

        side.add(brand,  BorderLayout.NORTH);
        side.add(nav,    BorderLayout.CENTER);
        side.add(status, BorderLayout.SOUTH);
        return side;
    }

    // ── Header + content ────────────────────────────────────────────────────────

    private JPanel buildMain() {
        JPanel main = UI.deepPanel(new BorderLayout(0, 0));

        // Header bar
        JPanel header = UI.deepPanel(new BorderLayout());
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(14, 28, 14, 28)));
        header.add(lblPageTitle, BorderLayout.WEST);

        // Content with padding
        JPanel content = UI.deepPanel(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        content.add(contentArea, BorderLayout.CENTER);

        main.add(header,  BorderLayout.NORTH);
        main.add(content, BorderLayout.CENTER);
        return main;
    }

    // ── Nav activation ───────────────────────────────────────────────────────────

    private void activateNav(JButton active, String card, String title) {
        for (JButton b : new JButton[]{btnPos, btnInventory, btnExports}) {
            boolean sel = b == active;
            ((JButton)b).putClientProperty("selected", sel);
            b.setForeground(sel ? Theme.TEXT_PRI : Theme.TEXT_SEC);
            // toggle via our custom navBtn's selected state
            try {
                java.lang.reflect.Method m = b.getClass().getDeclaredMethod("setSelected", boolean.class);
                m.setAccessible(true);
                m.invoke(b, sel);
            } catch (Exception ignored) {}
        }
        ((CardLayout)contentArea.getLayout()).show(contentArea, card);
        lblPageTitle.setText(title);
    }
}
