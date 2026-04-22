package org.example;

import com.formdev.flatlaf.FlatDarkLaf;
import com.google.gson.JsonObject;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class InventraApp extends JFrame {
    private final ApiClient apiClient;
    private DefaultTableModel tableModel;
    private JTable productTable;
    private List<org.example.Product> currentProducts;

    public InventraApp() {
        apiClient = new ApiClient();

        setTitle("My Shop Inventory");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("  📦 My Shop Inventory", SwingConstants.LEFT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"System Number", "Barcode", "Item Name", "Price", "Items Left"};
        tableModel = new DefaultTableModel(columns, 0);
        productTable = new JTable(tableModel);
        productTable.setRowHeight(40);
        productTable.setFont(new Font("SansSerif", Font.PLAIN, 16));
        productTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 16));

        productTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected && currentProducts != null && row < currentProducts.size()) {
                    org.example.Product p = currentProducts.get(row);
                    if (p.getCurrentStock() <= p.getReorderThreshold()) {
                        c.setBackground(new Color(110, 30, 30));
                        c.setForeground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(15, 15, 15));
                        c.setForeground(Color.WHITE);
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));

        JButton btnManage = createBigButton("📦 Manage Items", new Color(40, 40, 40));
        JButton btnSell = createBigButton("🛒 Sell an Item", new Color(180, 40, 40));
        JButton btnHistory = createBigButton("📜 Sales History", new Color(100, 40, 100));
        JButton btnExport = createBigButton("📊 Excel Sheet", new Color(34, 197, 94));
        JButton btnRefresh = createBigButton("🔄 Refresh", new Color(40, 40, 100));

        btnManage.addActionListener(e -> showManageMenu());
        btnSell.addActionListener(e -> simpleSellProduct());
        btnHistory.addActionListener(e -> showTransactionHistory());
        btnExport.addActionListener(e -> downloadExcelDirectly());
        btnRefresh.addActionListener(e -> refreshTableData());

        buttonPanel.add(btnManage);
        buttonPanel.add(btnSell);
        buttonPanel.add(btnHistory);
        buttonPanel.add(btnExport);
        buttonPanel.add(btnRefresh);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshTableData();
    }

    private JButton createBigButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(180, 50));
        return button;
    }

    private JPanel createQuantitySpinner(JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        JButton btnMinus = new JButton("-");
        JButton btnPlus = new JButton("+");

        btnMinus.setBackground(new Color(80, 80, 80));
        btnMinus.setForeground(Color.WHITE);
        btnMinus.setFont(new Font("SansSerif", Font.BOLD, 20));
        btnMinus.setPreferredSize(new Dimension(50, 45));

        btnPlus.setBackground(new Color(80, 80, 80));
        btnPlus.setForeground(Color.WHITE);
        btnPlus.setFont(new Font("SansSerif", Font.BOLD, 20));
        btnPlus.setPreferredSize(new Dimension(50, 45));

        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setFont(new Font("SansSerif", Font.BOLD, 20));
        textField.setPreferredSize(new Dimension(80, 45));
        if (textField.getText().trim().isEmpty()) textField.setText("1");

        btnMinus.addActionListener(e -> {
            try {
                int val = Integer.parseInt(textField.getText().trim());
                if (val > 0) textField.setText(String.valueOf(val - 1));
            } catch (Exception ex) { textField.setText("0"); }
        });

        btnPlus.addActionListener(e -> {
            try {
                int val = Integer.parseInt(textField.getText().trim());
                textField.setText(String.valueOf(val + 1));
            } catch (Exception ex) { textField.setText("1"); }
        });

        panel.add(btnMinus, BorderLayout.WEST);
        panel.add(textField, BorderLayout.CENTER);
        panel.add(btnPlus, BorderLayout.EAST);
        panel.setOpaque(false);
        return panel;
    }

    private void showManageMenu() {
        JDialog dialog = new JDialog(this, "Manage Items", true);
        dialog.setSize(400, 400);
        dialog.setLayout(new GridLayout(3, 1, 20, 20));
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(new Color(15, 15, 15));
        ((JPanel)dialog.getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnAdd = createBigButton("➕ Add New Item", new Color(40, 40, 40));
        JButton btnEdit = createBigButton("✏️ Edit / Restock Item", new Color(40, 40, 40));
        JButton btnDelete = createBigButton("❌ Delete Item", new Color(180, 40, 40));

        btnAdd.addActionListener(e -> { dialog.dispose(); simpleAddProduct(); });
        btnEdit.addActionListener(e -> { dialog.dispose(); simpleEditProduct(); });
        btnDelete.addActionListener(e -> { dialog.dispose(); simpleDeleteProduct(); });

        dialog.add(btnAdd); dialog.add(btnEdit); dialog.add(btnDelete);
        dialog.setVisible(true);
    }

    private void simpleAddProduct() {
        JTextField txtName = new JTextField();
        JTextField txtSku = new JTextField();
        JTextField txtPrice = new JTextField();
        JTextField txtStock = new JTextField("1");

        Object[] message = {
                "Item Name:", txtName, "Barcode (Optional):", txtSku,
                "Selling Price (₹):", txtPrice, "Initial Stock Amount:", createQuantitySpinner(txtStock)
        };

        if (JOptionPane.showConfirmDialog(this, message, "Add Item", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                if (txtName.getText().trim().isEmpty()) throw new Exception("Item name cannot be empty.");
                String json = String.format("{\"name\":\"%s\", \"skuCode\":\"%s\", \"price\":%s, \"currentStock\":%s}",
                        txtName.getText(), txtSku.getText(), txtPrice.getText(), txtStock.getText());
                apiClient.addProduct(json);
                refreshTableData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Oops!", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void simpleEditProduct() {
        String idStr = JOptionPane.showInputDialog(this, "Enter the 'System Number' from the table:");
        if (idStr == null || idStr.trim().isEmpty()) return;

        try {
            int displayIndex = Integer.parseInt(idStr.trim()) - 1;
            if (displayIndex < 0 || displayIndex >= currentProducts.size()) throw new Exception("Invalid System Number.");
            Long actualDatabaseId = currentProducts.get(displayIndex).getId();

            JTextField txtName = new JTextField();
            JTextField txtSku = new JTextField();
            JTextField txtPrice = new JTextField();
            JTextField txtStock = new JTextField("1");

            Object[] message = {
                    "Update Name:", txtName, "Update Barcode:", txtSku,
                    "Update Price (₹):", txtPrice, "Update Stock Amount (Restock):", createQuantitySpinner(txtStock)
            };

            if (JOptionPane.showConfirmDialog(this, message, "Edit / Restock Item", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                String json = String.format("{\"name\":\"%s\", \"skuCode\":\"%s\", \"price\":%s, \"currentStock\":%s}",
                        txtName.getText(), txtSku.getText(), txtPrice.getText(), txtStock.getText());
                apiClient.updateProduct(actualDatabaseId, json);
                refreshTableData();
                JOptionPane.showMessageDialog(this, "Item updated successfully!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Oops!", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void simpleDeleteProduct() {
        String idStr = JOptionPane.showInputDialog(this, "Enter the 'System Number' to DELETE:");
        if (idStr == null || idStr.isEmpty()) return;

        try {
            int displayIndex = Integer.parseInt(idStr.trim()) - 1;
            if (displayIndex < 0 || displayIndex >= currentProducts.size()) throw new Exception("Invalid System Number.");
            Long actualDatabaseId = currentProducts.get(displayIndex).getId();

            int confirm = JOptionPane.showConfirmDialog(this, "Permanently delete item #" + idStr + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                apiClient.deleteProduct(actualDatabaseId);
                refreshTableData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void simpleSellProduct() {
        JTextField txtId = new JTextField();
        JTextField txtQty = new JTextField("1");

        Object[] message = { "Enter System Number:", txtId, "How many are they buying?", createQuantitySpinner(txtQty) };

        if (JOptionPane.showConfirmDialog(this, message, "Sell an Item", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                if (txtId.getText().trim().isEmpty()) throw new Exception("System number is required.");

                int displayIndex = Integer.parseInt(txtId.getText().trim()) - 1;
                if (displayIndex < 0 || displayIndex >= currentProducts.size()) throw new Exception("Invalid System Number.");
                Long actualDatabaseId = currentProducts.get(displayIndex).getId();

                int qty = Integer.parseInt(txtQty.getText().trim());

                Long receiptNumber = apiClient.processSale(actualDatabaseId, qty);
                refreshTableData();

                int wantBill = JOptionPane.showConfirmDialog(this,
                        "Sale successful!\nDo you want to download the Customer Bill?",
                        "Download Bill?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (wantBill == JOptionPane.YES_OPTION) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setSelectedFile(new File("Customer_Bill_" + receiptNumber + ".pdf"));
                    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                        apiClient.downloadBill(receiptNumber, fileChooser.getSelectedFile().getAbsolutePath());
                        JOptionPane.showMessageDialog(this, "Bill downloaded!");
                    }
                }
            } catch (Exception ex) {
                String msg = ex.getMessage() != null ? ex.getMessage() : "Oops! Please type a valid number.";
                JOptionPane.showMessageDialog(this, msg, "Hold on!", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showTransactionHistory() {
        try {
            com.google.gson.JsonArray transactions = apiClient.getAllTransactions();

            JDialog dialog = new JDialog(this, "Sales History", true);
            dialog.setSize(850, 500);
            dialog.setLocationRelativeTo(this);

            // UI UPDATE: Changed column name to S.No
            String[] columns = {"S.No", "Product Name", "Qty Sold", "Total (₹)", "Date", "Action"};
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 5;
                }
            };

            // NEW: Hidden list to store the actual DB IDs so we don't display them
            java.util.List<Long> transactionDbIds = new java.util.ArrayList<>();
            int displaySequence = 1;

            for (com.google.gson.JsonElement el : transactions) {
                JsonObject t = el.getAsJsonObject();
                Long pid = t.get("productId").getAsLong();
                String pName = "Unknown/Deleted";

                if (currentProducts != null) {
                    for (org.example.Product p : currentProducts) {
                        if (p.getId().equals(pid)) { pName = p.getName(); break; }
                    }
                }

                // Add the real DB ID to our hidden mapping list
                transactionDbIds.add(t.get("id").getAsLong());

                // Add the sequence number to the table UI
                model.addRow(new Object[]{
                        displaySequence++, pName, t.get("quantity").getAsInt(),
                        t.get("totalPrice").getAsDouble(), t.get("transactionDate").getAsString(), "Generate Chalan"
                });
            }

            JTable table = new JTable(model);
            table.setRowHeight(40);
            table.setFont(new Font("SansSerif", Font.PLAIN, 14));

            table.getColumn("Action").setCellRenderer(new ButtonRenderer());
            // We pass the hidden list of IDs directly into the button editor
            table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), transactionDbIds));

            dialog.add(new JScrollPane(table));
            dialog.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load transactions.");
        }
    }

    private void promptAndDownloadChalan(Long transactionId) {
        JTextField txtRecipient = new JTextField();
        JTextField txtAddress = new JTextField();
        JTextField txtMapLink = new JTextField();

        Object[] inputs = {
                "Recipient Name:", txtRecipient,
                "Delivery Address (Text):", txtAddress,
                "Google Maps Link (Optional):", txtMapLink
        };

        // UI UPDATE: We removed the "for Receipt #" here to hide the DB ID completely
        if (JOptionPane.showConfirmDialog(this, inputs, "Delivery Info", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File("Delivery_Chalan.pdf"));
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    apiClient.downloadChalan(transactionId, txtAddress.getText(), txtMapLink.getText(), txtRecipient.getText(), fileChooser.getSelectedFile().getAbsolutePath());
                    JOptionPane.showMessageDialog(this, "Delivery Chalan saved!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Failed to download Chalan.");
                }
            }
        }
    }

    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() { setOpaque(true); setBackground(new Color(59, 130, 246)); setForeground(Color.WHITE); }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "Generate Chalan" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean isPushed;
        private Long currentTransactionId;
        private java.util.List<Long> transactionIds; // Added list reference

        // Constructor now accepts the hidden mapping list
        public ButtonEditor(JCheckBox checkBox, java.util.List<Long> transactionIds) {
            super(checkBox);
            this.transactionIds = transactionIds;
            button = new JButton("Generate Chalan");
            button.setOpaque(true);
            button.setBackground(new Color(59, 130, 246));
            button.setForeground(Color.WHITE);
            button.addActionListener(e -> fireEditingStopped());
        }
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            isPushed = true;
            // Fetch the REAL database ID from the hidden list using the row number
            currentTransactionId = transactionIds.get(row);
            return button;
        }
        public Object getCellEditorValue() {
            if (isPushed) promptAndDownloadChalan(currentTransactionId);
            isPushed = false;
            return "Generate Chalan";
        }
        public boolean stopCellEditing() { isPushed = false; return super.stopCellEditing(); }
    }

    private void downloadExcelDirectly() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("My_Sales.xlsx"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                apiClient.exportSalesExcel(fileChooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Excel file saved successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to download Excel.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshTableData() {
        try {
            currentProducts = apiClient.getAllProducts();
            tableModel.setRowCount(0);
            int displaySequence = 1;
            for (org.example.Product p : currentProducts) {
                tableModel.addRow(new Object[]{displaySequence++, p.getSkuCode(), p.getName(), "₹" + p.getPrice(), p.getCurrentStock()});
            }
        } catch (Exception e) { System.err.println("Backend offline."); }
    }

    public static void main(String[] args) {
        try {
            FlatDarkLaf.setup();
            UIManager.put("Panel.background", new Color(0, 0, 0));
            UIManager.put("RootPane.background", new Color(0, 0, 0));
            UIManager.put("OptionPane.background", new Color(0, 0, 0));
            UIManager.put("Table.background", new Color(15, 15, 15));
            UIManager.put("Table.gridColor", new Color(40, 40, 40));
            UIManager.put("TableHeader.background", new Color(20, 20, 20));
            UIManager.put("TextField.background", new Color(20, 20, 20));
            UIManager.put("Button.arc", 12);
        } catch (Exception ex) {}

        SwingUtilities.invokeLater(() -> new InventraApp().setVisible(true));
    }
}