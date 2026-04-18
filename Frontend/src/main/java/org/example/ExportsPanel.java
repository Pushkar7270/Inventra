package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;

public class ExportsPanel extends JPanel {

    private final JLabel statusLabel = org.example.UI.label("Ready to export.");

    public ExportsPanel() {
        setLayout(new GridBagLayout());
        setOpaque(false);

        JPanel card = org.example.UI.card(new BorderLayout(0, 24));
        card.setPreferredSize(new Dimension(480, 300));

        JLabel title = org.example.UI.title("📊  Reports & Exports");
        JLabel sub   = org.example.UI.label("Download sales data and transaction records");

        JPanel titleBlock = org.example.UI.panel(new BorderLayout(0, 4));
        titleBlock.add(title, BorderLayout.NORTH);
        titleBlock.add(sub, BorderLayout.SOUTH);

        JPanel buttons = org.example.UI.panel(new GridLayout(1, 2, 16, 0));

        JButton btnExcel = org.example.UI.btnPrimary("⬇  Export Sales Excel (.xlsx)");
        JButton btnBill  = org.example.UI.btnAmber("⬇  Download Latest Bill (.pdf)");

        btnExcel.setPreferredSize(new Dimension(0, 52));
        btnBill.setPreferredSize(new Dimension(0, 52));

        btnExcel.addActionListener(e -> exportExcel());
        btnBill.addActionListener(e -> downloadBill());

        buttons.add(btnExcel);
        buttons.add(btnBill);

        card.add(titleBlock, BorderLayout.NORTH);
        card.add(buttons, BorderLayout.CENTER);
        card.add(statusLabel, BorderLayout.SOUTH);

        add(card);
    }

    private void exportExcel() {
        status("Exporting…");
        SwingWorker<byte[], Void> w = new SwingWorker<byte[], Void>() {
            protected byte[] doInBackground() throws Exception {
                return org.example.ApiClient.downloadExcel();
            }
            protected void done() {
                try {
                    byte[] data = get();
                    JFileChooser fc = new JFileChooser();
                    fc.setSelectedFile(new File("sales_export.xlsx"));
                    if (fc.showSaveDialog(ExportsPanel.this) == JFileChooser.APPROVE_OPTION) {
                        Files.write(fc.getSelectedFile().toPath(), data);
                        status("✓ Saved: " + fc.getSelectedFile().getName());
                    } else {
                        status("Export cancelled.");
                    }
                } catch (Exception ex) { status("Error: " + ex.getMessage()); }
            }
        };
        w.execute();
    }

    private void downloadBill() {
        String idStr = JOptionPane.showInputDialog(this, "Enter Transaction ID:");
        if (idStr == null || idStr.trim().isEmpty()) return;
        long id;
        try { id = Long.parseLong(idStr.trim()); }
        catch (Exception e) { status("Invalid ID."); return; }

        status("Downloading bill…");
        SwingWorker<byte[], Void> w = new SwingWorker<byte[], Void>() {
            protected byte[] doInBackground() throws Exception {
                return org.example.ApiClient.downloadBill(id);
            }
            protected void done() {
                try {
                    byte[] data = get();
                    JFileChooser fc = new JFileChooser();
                    fc.setSelectedFile(new File("bill_" + id + ".pdf"));
                    if (fc.showSaveDialog(ExportsPanel.this) == JFileChooser.APPROVE_OPTION) {
                        Files.write(fc.getSelectedFile().toPath(), data);
                        status("✓ Saved: " + fc.getSelectedFile().getName());
                    } else {
                        status("Download cancelled.");
                    }
                } catch (Exception ex) { status("Error: " + ex.getMessage()); }
            }
        };
        w.execute();
    }

    private void status(String msg) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(msg));
    }
}

