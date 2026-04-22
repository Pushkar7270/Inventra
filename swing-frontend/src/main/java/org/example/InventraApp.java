package org.example;

import com.formdev.flatlaf.FlatDarkLaf;

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

    // NEW: We keep a list of the products in memory so the table knows which ones have low stock
    private List<org.example.Product> currentProducts;

    public InventraApp() {
        apiClient = new ApiClient();

        setTitle("My Shop Inventory");
        setSize(900, 600);
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

        // --- NEW: THE PAINTBRUSH THAT HIGHLIGHTS LOW STOCK ROWS ---
        productTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Only change colors if the user hasn't clicked/selected the row
                if (!isSelected && currentProducts != null && row < currentProducts.size()) {
                    org.example.Product p = currentProducts.get(row);

                    // If the current stock is equal to or less than the warning limit, paint it RED!
                    if (p.getCurrentStock() <= p.getReorderThreshold()) {
                        c.setBackground(new Color(110, 30, 30)); // Deep Dark Red
                        c.setForeground(Color.WHITE);
                    } else {
                        // Otherwise, keep it the normal dark theme color
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

        // --- BOTTOM BUTTONS ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));

        JButton btnManage = createBigButton("📦 Manage Items", new Color(40, 40, 40));
        JButton btnSell = createBigButton("🛒 Sell an Item", new Color(180, 40, 40));
        JButton btnExport = createBigButton("🖨️ Print / Download", new Color(40, 100, 40));
        JButton btnRefresh = createBigButton("🔄 Refresh Screen", new Color(40, 40, 100));

        btnManage.addActionListener(e -> showManageMenu());
        btnSell.addActionListener(e -> simpleSellProduct());
        btnExport.addActionListener(e -> simpleDownloadReports());
        btnRefresh.addActionListener(e -> refreshTableData());

        buttonPanel.add(btnManage);
        buttonPanel.add(btnSell);
        buttonPanel.add(btnExport);
        buttonPanel.add(btnRefresh);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshTableData();
    }

    private JButton createBigButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 50));
        return button;
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
        JTextField txtStock = new JTextField("1"); // Default to 1
        JTextField txtThreshold = new JTextField("5");

        JPanel stockSpinner = createQuantitySpinner(txtStock);

        Object[] message = {
                "Item Name:", txtName, "Barcode (Optional):", txtSku,
                "Selling Price ($):", txtPrice, "Initial Stock Amount:", stockSpinner,
                "Warn me when stock drops below:", txtThreshold
        };

        if (JOptionPane.showConfirmDialog(this, message, "Add Item", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                if (txtName.getText().trim().isEmpty()) throw new Exception("Item name cannot be empty.");

                String json = String.format("{\"name\":\"%s\", \"skuCode\":\"%s\", \"price\":%s, \"currentStock\":%s, \"reorderThreshold\":%s}",
                        txtName.getText(), txtSku.getText(), txtPrice.getText(), txtStock.getText(), txtThreshold.getText());
                apiClient.addProduct(json);
                refreshTableData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Oops!", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void simpleEditProduct() {
        String idStr = JOptionPane.showInputDialog(this, "Enter the 'System Number' of the item you want to edit:");
        if (idStr == null || idStr.trim().isEmpty()) return;

        JTextField txtName = new JTextField();
        JTextField txtSku = new JTextField();
        JTextField txtPrice = new JTextField();
        JTextField txtStock = new JTextField("1");
        JTextField txtThreshold = new JTextField();

        JPanel stockSpinner = createQuantitySpinner(txtStock);

        Object[] message = {
                "Update Name:", txtName, "Update Barcode:", txtSku,
                "Update Price ($):", txtPrice, "Update Stock Amount (Restock):", stockSpinner,
                "Update Low Stock Warning:", txtThreshold
        };

        if (JOptionPane.showConfirmDialog(this, message, "Edit / Restock Item", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                String json = String.format("{\"name\":\"%s\", \"skuCode\":\"%s\", \"price\":%s, \"currentStock\":%s, \"reorderThreshold\":%s}",
                        txtName.getText(), txtSku.getText(), txtPrice.getText(), txtStock.getText(), txtThreshold.getText());
                apiClient.updateProduct(Long.parseLong(idStr), json);
                refreshTableData();
                JOptionPane.showMessageDialog(this, "Item updated successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Oops!", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void simpleDeleteProduct() {
        String idStr = JOptionPane.showInputDialog(this, "Enter the 'System Number' of the item you want to DELETE:");
        if (idStr == null || idStr.isEmpty()) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to permanently delete item #" + idStr + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                apiClient.deleteProduct(Long.parseLong(idStr));
                refreshTableData();
                JOptionPane.showMessageDialog(this, "Item deleted.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to delete item.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void simpleSellProduct() {
        JTextField txtId = new JTextField();
        JTextField txtQty = new JTextField("1");

        JPanel qtySpinner = createQuantitySpinner(txtQty);

        Object[] message = { "Enter System Number:", txtId, "How many are they buying?", qtySpinner };

        if (JOptionPane.showConfirmDialog(this, message, "Sell an Item", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                if (txtId.getText().trim().isEmpty()) throw new Exception("System number is required.");

                Long productId = Long.parseLong(txtId.getText().trim());
                int qty = Integer.parseInt(txtQty.getText().trim());

                Long receiptNumber = apiClient.processSale(productId, qty);
                refreshTableData();

                int wantBill = JOptionPane.showConfirmDialog(this,
                        "Sale successful! The stock is updated.\n\nDo you want to download the Customer Bill?",
                        "Download Bill?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (wantBill == JOptionPane.YES_OPTION) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setSelectedFile(new File("Customer_Bill_" + receiptNumber + ".pdf"));
                    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                        apiClient.downloadBill(receiptNumber, fileChooser.getSelectedFile().getAbsolutePath());
                        JOptionPane.showMessageDialog(this, "Bill downloaded to your computer!");
                    }
                }

            } catch (Exception ex) {
                // UPDATED: Now dynamically displays the error thrown by the backend
                String msg = ex.getMessage() != null ? ex.getMessage() : "Oops! Please type a valid number.";
                JOptionPane.showMessageDialog(this, msg, "Hold on!", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void simpleDownloadReports() {
        JDialog dialog = new JDialog(this, "Print / Download", true);
        dialog.setSize(400, 400);
        dialog.setLayout(new GridLayout(2, 1, 20, 20));
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(new Color(15, 15, 15));
        ((JPanel)dialog.getContentPane()).setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JButton btnExcel = createBigButton("📊 Download Excel Spreadsheet", new Color(34, 197, 94));
        JButton btnChalan = createBigButton("🚚 Generate Delivery Chalan", new Color(59, 130, 246));

        btnExcel.addActionListener(e -> {
            dialog.dispose();
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File("My_Sales.xlsx"));
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    apiClient.exportSalesExcel(fileChooser.getSelectedFile().getAbsolutePath());
                    JOptionPane.showMessageDialog(this, "Excel file saved successfully!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Failed to download Excel.");
                }
            }
        });

        btnChalan.addActionListener(e -> {
            dialog.dispose();
            JTextField txtId = new JTextField();
            JTextField txtRecipient = new JTextField(); // NEW FIELD!
            JTextField txtAddress = new JTextField();
            JTextField txtMapLink = new JTextField();

            Object[] inputs = {
                    "Enter Receipt Number:", txtId,
                    "Recipient Name:", txtRecipient, // Added to the popup!
                    "Delivery Address (Text):", txtAddress,
                    "Google Maps Link (Optional):", txtMapLink
            };

            if (JOptionPane.showConfirmDialog(this, inputs, "Delivery Info", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new File("Delivery_Chalan_" + txtId.getText() + ".pdf"));
                if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        apiClient.downloadChalan(
                                Long.parseLong(txtId.getText()),
                                txtAddress.getText(),
                                txtMapLink.getText(),
                                txtRecipient.getText(), // Pass it to the API
                                fileChooser.getSelectedFile().getAbsolutePath()
                        );
                        JOptionPane.showMessageDialog(this, "Delivery Chalan saved!");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Failed to download Chalan. Check Receipt Number.");
                    }
                }
            }
        });

        dialog.add(btnExcel); dialog.add(btnChalan);
        dialog.setVisible(true);
    }

    private void refreshTableData() {
        try {
            // NEW: Save the products to our memory list first
            currentProducts = apiClient.getAllProducts();
            tableModel.setRowCount(0);
            for (org.example.Product p : currentProducts) {
                tableModel.addRow(new Object[]{p.getId(), p.getSkuCode(), p.getName(), "$" + p.getPrice(), p.getCurrentStock()});
            }
        } catch (Exception e) {
            System.err.println("Backend offline.");
        }
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
        } catch (Exception ex) {
            System.err.println("Failed to initialize dark theme");
        }

        SwingUtilities.invokeLater(() -> new InventraApp().setVisible(true));
    }
    // Helper method to create a - [ text field ] + layout
    private JPanel createQuantitySpinner(JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        JButton btnMinus = new JButton("-");
        JButton btnPlus = new JButton("+");

        btnMinus.setBackground(new Color(60, 60, 60));
        btnMinus.setForeground(Color.WHITE);
        btnPlus.setBackground(new Color(60, 60, 60));
        btnPlus.setForeground(Color.WHITE);

        textField.setHorizontalAlignment(JTextField.CENTER);
        if (textField.getText().trim().isEmpty()) {
            textField.setText("1");
        }

        btnMinus.addActionListener(e -> {
            try {
                int val = Integer.parseInt(textField.getText().trim());
                if (val > 0) textField.setText(String.valueOf(val - 1)); // Prevent negative quantities
            } catch (NumberFormatException ex) {
                textField.setText("0");
            }
        });

        btnPlus.addActionListener(e -> {
            try {
                int val = Integer.parseInt(textField.getText().trim());
                textField.setText(String.valueOf(val + 1));
            } catch (NumberFormatException ex) {
                textField.setText("1");
            }
        });

        panel.add(btnMinus, BorderLayout.WEST);
        panel.add(textField, BorderLayout.CENTER);
        panel.add(btnPlus, BorderLayout.EAST);
        panel.setOpaque(false);
        return panel;
    }
}