package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class InventoryPanel extends JPanel {

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Name", "SKU Code", "Price (₹)", "Stock", "Reorder At"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    public InventoryPanel() {
        setLayout(new BorderLayout(0, 14));
        setOpaque(false);

        UI.styleTable(table);

        // highlight low-stock rows red
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setFont(Theme.FONT_BODY);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                int stock   = safeInt(t.getValueAt(row, 4));
                int reorder = safeInt(t.getValueAt(row, 5));
                if (sel) {
                    setBackground(new Color(0, 200, 160, 50));
                    setForeground(Theme.TEXT_PRI);
                } else if (stock < reorder) {
                    setBackground(new Color(224, 92, 92, 25));
                    setForeground(Theme.DANGER);
                } else {
                    setBackground(row % 2 == 0 ? Theme.BG_PANEL : Theme.BG_ROW);
                    setForeground(Theme.TEXT_PRI);
                }
                return this;
            }
            private int safeInt(Object o) {
                try { return Integer.parseInt(o == null ? "0" : o.toString()); }
                catch (Exception e) { return 0; }
            }
        });

        add(buildToolbar(), BorderLayout.NORTH);
        add(UI.scrollPane(table), BorderLayout.CENTER);

        refresh();
    }

    // ── Toolbar ─────────────────────────────────────────────────────────────────

    private JPanel buildToolbar() {
        JPanel p = UI.deepPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JButton btnRefresh = UI.btnGhost("⟳  Refresh");
        JButton btnAdd     = UI.btnPrimary("+ Add Product");
        JButton btnEdit    = UI.btnGhost("✎  Edit");
        JButton btnDel     = UI.btnDanger("✕  Delete");
        JButton btnLow     = UI.btnAmber("⚠  Low Stock");

        btnRefresh.addActionListener(e -> refresh());
        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> showEditDialog());
        btnDel.addActionListener(e -> deleteSelected());
        btnLow.addActionListener(e -> showLowStock());

        p.add(btnAdd); p.add(btnEdit); p.add(btnDel);
        p.add(Box.createHorizontalStrut(16));
        p.add(btnLow); p.add(btnRefresh);
        return p;
    }

    // ── Data ────────────────────────────────────────────────────────────────────

    public void refresh() {
        SwingWorker<List<Map<String,Object>>, Void> w = new SwingWorker<List<Map<String,Object>>, Void>() {
            protected List<Map<String,Object>> doInBackground() throws Exception {
                return ApiClient.getProducts();
            }
            protected void done() {
                try { populate(get()); } catch (Exception ex) { ex.printStackTrace(); }
            }
        };
        w.execute();
    }

    private void populate(List<Map<String,Object>> products) {
        model.setRowCount(0);
        for (Map<String,Object> p : products) {
            model.addRow(new Object[]{
                    p.get("id"), p.get("name"), p.get("skuCode"),
                    p.get("price"), p.get("currentStock"), p.get("reorderThreshold")
            });
        }
    }

    // ── Dialogs ─────────────────────────────────────────────────────────────────

    private void showAddDialog() {
        JTextField fName  = UI.field("Name");
        JTextField fSku   = UI.field("SKU Code");
        JTextField fPrice = UI.field("Price");
        JTextField fStock = UI.field("Initial Stock");
        JTextField fThres = UI.field("Reorder Threshold");

        JPanel form = buildForm(
                "Product Name", fName, "SKU Code", fSku,
                "Price (₹)", fPrice, "Initial Stock", fStock, "Reorder Threshold", fThres);

        int res = JOptionPane.showConfirmDialog(this, form, "Add Product",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        try {
            ApiClient.addProduct(fName.getText(), fSku.getText(),
                    Double.parseDouble(fPrice.getText()),
                    Integer.parseInt(fStock.getText()),
                    Integer.parseInt(fThres.getText()));
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Select a product first."); return; }

        long id = Long.parseLong(model.getValueAt(row,0).toString());
        JTextField fName  = UI.field("Name");   fName.setText(model.getValueAt(row,1).toString());
        JTextField fSku   = UI.field("SKU");    fSku.setText(model.getValueAt(row,2).toString());
        JTextField fPrice = UI.field("Price");  fPrice.setText(model.getValueAt(row,3).toString());
        JTextField fThres = UI.field("Reorder");fThres.setText(model.getValueAt(row,5).toString());

        JPanel form = buildForm(
                "Product Name", fName, "SKU Code", fSku,
                "Price (₹)", fPrice, "Reorder Threshold", fThres);

        int res = JOptionPane.showConfirmDialog(this, form, "Edit Product",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        try {
            ApiClient.updateProduct(id, fName.getText(), fSku.getText(),
                    Double.parseDouble(fPrice.getText()),
                    Integer.parseInt(fThres.getText()));
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Select a product first."); return; }
        long id = Long.parseLong(model.getValueAt(row,0).toString());
        String name = model.getValueAt(row,1).toString();
        int res = JOptionPane.showConfirmDialog(this,
                "Delete \"" + name + "\"?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (res != JOptionPane.YES_OPTION) return;
        try { ApiClient.deleteProduct(id); refresh(); }
        catch (Exception ex) { warn(ex.getMessage()); }
    }

    private void showLowStock() {
        SwingWorker<List<Map<String,Object>>, Void> w = new SwingWorker<List<Map<String,Object>>, Void>() {
            protected List<Map<String,Object>> doInBackground() throws Exception {
                return ApiClient.getLowStock();
            }
            protected void done() {
                try {
                    List<Map<String,Object>> list = get();
                    if (list.isEmpty()) {
                        JOptionPane.showMessageDialog(InventoryPanel.this,
                                "All products are adequately stocked ✓",
                                "Low Stock", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        StringBuilder sb = new StringBuilder("Low stock products:\n\n");
                        for (Map<String,Object> p : list) {
                            sb.append("• ").append(p.get("name"))
                                    .append("  (Stock: ").append(p.get("currentStock"))
                                    .append(" / Threshold: ").append(p.get("reorderThreshold")).append(")\n");
                        }
                        JOptionPane.showMessageDialog(InventoryPanel.this, sb.toString(),
                                "⚠ Low Stock Alert", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        };
        w.execute();
    }

    // ── Helpers ─────────────────────────────────────────────────────────────────

    /** Build a 2-column label+field form panel */
    private JPanel buildForm(Object... labelsAndFields) {
        JPanel p = new JPanel(new GridLayout(0, 2, 10, 8));
        p.setBackground(Theme.BG_PANEL);
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        for (int i = 0; i < labelsAndFields.length; i += 2) {
            p.add(UI.label(labelsAndFields[i].toString()));
            p.add((Component)labelsAndFields[i+1]);
        }
        return p;
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }
}
