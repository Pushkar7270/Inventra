package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class PosPanel extends JPanel {

    private final JTextField skuField = UI.field("Scan barcode or enter SKU…");
    private final DefaultTableModel cartModel = new DefaultTableModel(
            new String[]{"Product", "SKU", "Qty", "Unit Price", "Subtotal"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable cartTable = new JTable(cartModel);
    private final JLabel lblTotal = UI.accentLabel("₹0.00");

    // cart: productId → {product map, qty}
    private final Map<Long, int[]> qtyMap = new LinkedHashMap<>();
    private final Map<Long, Map<String,Object>> productMap = new LinkedHashMap<>();

    private Long lastTransactionId = null;

    public PosPanel() {
        setLayout(new BorderLayout(20, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        add(buildLeft(), BorderLayout.CENTER);
        add(buildCart(), BorderLayout.EAST);
    }

    // ── Left: SKU search + product grid ─────────────────────────────────────────

    private JPanel buildLeft() {
        JPanel p = UI.deepPanel(new BorderLayout(0, 14));
        p.setBorder(BorderFactory.createEmptyBorder());

        // search bar row
        JPanel searchRow = UI.deepPanel(new BorderLayout(10, 0));
        JButton btnSearch = UI.btnPrimary("Search");
        btnSearch.addActionListener(e -> searchBySku());
        skuField.addActionListener(e -> searchBySku());
        searchRow.add(skuField, BorderLayout.CENTER);
        searchRow.add(btnSearch, BorderLayout.EAST);

        // results area
        JPanel results = UI.deepPanel(new FlowLayout(FlowLayout.LEFT, 14, 14));
        JScrollPane scroll = new JScrollPane(results);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.getViewport().setBackground(Theme.BG_DEEP);

        JLabel hint = UI.label("Search a SKU or load all products below");
        hint.setBorder(BorderFactory.createEmptyBorder(20, 10, 0, 0));
        results.add(hint);

        JButton btnAll = UI.btnGhost("⟳  Load All Products");
        btnAll.addActionListener(e -> loadAllProducts(results));

        JPanel top = UI.deepPanel(new BorderLayout(0, 10));
        top.add(searchRow, BorderLayout.CENTER);
        top.add(btnAll, BorderLayout.EAST);

        p.add(top, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private void searchBySku() {
        String sku = skuField.getText().trim();
        if (sku.isEmpty()) return;
        SwingWorker<Map<String,Object>, Void> w = new SwingWorker<Map<String,Object>, Void>() {
            protected Map<String,Object> doInBackground() throws Exception {
                return ApiClient.getProductBySku(sku);
            }
            protected void done() {
                try { addToCart(get()); }
                catch (Exception ex) { JOptionPane.showMessageDialog(PosPanel.this,
                        "Product not found: " + sku, "Not Found", JOptionPane.WARNING_MESSAGE); }
            }
        };
        w.execute();
    }

    private void loadAllProducts(JPanel grid) {
        SwingWorker<List<Map<String,Object>>, Void> w = new SwingWorker<List<Map<String,Object>>, Void>() {
            protected List<Map<String,Object>> doInBackground() throws Exception {
                return ApiClient.getProducts();
            }
            protected void done() {
                try {
                    grid.removeAll();
                    for (Map<String,Object> p : get()) grid.add(productCard(p));
                    grid.revalidate(); grid.repaint();
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        };
        w.execute();
    }

    private JPanel productCard(Map<String,Object> product) {
        JPanel card = UI.card(new BorderLayout(0, 6));
        card.setPreferredSize(new Dimension(170, 120));

        JLabel name = UI.heading(truncate(str(product,"name"), 18));
        JLabel sku  = UI.label("SKU: " + str(product,"skuCode"));
        JLabel price = new JLabel("₹" + str(product,"price"));
        price.setFont(Theme.FONT_HEAD);
        price.setForeground(Theme.ACCENT);

        JLabel stock = new JLabel("Stock: " + str(product,"currentStock"));
        stock.setFont(Theme.FONT_SMALL);
        int s = num(product,"currentStock"); int t = num(product,"reorderThreshold");
        stock.setForeground(s < t ? Theme.DANGER : Theme.TEXT_SEC);

        JButton add = UI.btnPrimary("+ Add");
        add.setFont(Theme.FONT_SMALL);
        add.addActionListener(e -> addToCart(product));

        JPanel info = UI.card(new GridLayout(3,1,0,2));
        info.setBorder(BorderFactory.createEmptyBorder());
        info.add(name); info.add(sku); info.add(price);

        card.add(info, BorderLayout.CENTER);
        JPanel bot = UI.panel(new BorderLayout(6,0));
        bot.add(stock, BorderLayout.CENTER);
        bot.add(add, BorderLayout.EAST);
        card.add(bot, BorderLayout.SOUTH);
        return card;
    }

    // ── Cart ─────────────────────────────────────────────────────────────────────

    private JPanel buildCart() {
        JPanel p = UI.card(new BorderLayout(0, 12));
        p.setPreferredSize(new Dimension(380, 0));

        JLabel title = UI.heading("🛒  Current Sale");

        UI.styleTable(cartTable);
        JScrollPane scroll = UI.scrollPane(cartTable);

        // total row
        JPanel totRow = UI.panel(new BorderLayout());
        totRow.add(UI.label("Order Total"), BorderLayout.WEST);
        totRow.add(lblTotal, BorderLayout.EAST);

        // actions
        JButton btnCheckout = UI.btnPrimary("Process & Generate Bill");
        JButton btnChalan   = UI.btnAmber("Generate Chalan");
        JButton btnClear    = UI.btnDanger("Clear Cart");

        btnCheckout.addActionListener(e -> checkout());
        btnChalan.addActionListener(e -> genChalan());
        btnClear.addActionListener(e -> clearCart());

        JPanel btns = UI.panel(new GridLayout(3, 1, 0, 8));
        btns.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        btns.add(btnCheckout); btns.add(btnChalan); btns.add(btnClear);

        p.add(title, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        p.add(totRow, BorderLayout.SOUTH);

        JPanel south = UI.panel(new BorderLayout());
        south.add(totRow, BorderLayout.NORTH);
        south.add(btns, BorderLayout.SOUTH);
        p.add(south, BorderLayout.SOUTH);
        return p;
    }

    private void addToCart(Map<String,Object> product) {
        long id = longVal(product,"id");
        productMap.put(id, product);
        qtyMap.merge(id, new int[]{1}, (a, b) -> { a[0]++; return a; });
        refreshCart();
        skuField.setText("");
    }

    private void refreshCart() {
        cartModel.setRowCount(0);
        double total = 0;
        for (Long id : qtyMap.keySet()) {
            Map<String,Object> p = productMap.get(id);
            int qty = qtyMap.get(id)[0];
            double price = Double.parseDouble(str(p,"price"));
            double sub = price * qty;
            total += sub;
            cartModel.addRow(new Object[]{
                    str(p,"name"), str(p,"skuCode"), qty,
                    String.format("₹%.2f", price), String.format("₹%.2f", sub)
            });
        }
        lblTotal.setText(String.format("₹%.2f", total));
    }

    private void clearCart() {
        qtyMap.clear(); productMap.clear();
        refreshCart();
    }

    private void checkout() {
        if (qtyMap.isEmpty()) { warn("Cart is empty."); return; }
        SwingWorker<Void, Void> w = new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                for (Long id : qtyMap.keySet()) {
                    int qty = qtyMap.get(id)[0];
                    Map<String,Object> tx = ApiClient.sellProduct(id, qty);
                    lastTransactionId = longVal(tx, "id");
                }
                return null;
            }
            protected void done() {
                try { get(); clearCart();
                    JOptionPane.showMessageDialog(PosPanel.this,
                            "Sale processed! Transaction ID: " + lastTransactionId,
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) { warn("Checkout failed: " + ex.getMessage()); }
            }
        };
        w.execute();
    }

    private void genChalan() {
        if (lastTransactionId == null) { warn("Process a sale first."); return; }
        String addr = JOptionPane.showInputDialog(this, "Delivery address:");
        if (addr == null || addr.trim().isEmpty()) return;
        // default coords (Delhi) – in a real app you'd show a map dialog
        double lat = 28.6139, lng = 77.2090;
        try {
            byte[] pdf = ApiClient.downloadExcel(); // placeholder until chalan endpoint exposed
            JOptionPane.showMessageDialog(this, "Chalan generated. Save from backend endpoint:\n"
                    + "GET /api/transactions/" + lastTransactionId
                    + "/chalan?lat=" + lat + "&lng=" + lng + "&address=" + addr);
        } catch (Exception ex) { warn(ex.getMessage()); }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────────

    private static String str(Map<String,Object> m, String k) {
        Object v = m.get(k); return v == null ? "" : v.toString();
    }
    private static int num(Map<String,Object> m, String k) {
        try { return Integer.parseInt(str(m,k)); } catch (Exception e) { return 0; }
    }
    private static long longVal(Map<String,Object> m, String k) {
        try { return Long.parseLong(str(m,k)); } catch (Exception e) { return 0L; }
    }
    private static String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max) + "…" : s;
    }
    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }
}
