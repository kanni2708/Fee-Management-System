
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class Student {
    private int id;
    private String name;

    public Student(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

public class FeeManagementSystem extends JFrame {
    private Map<Integer, Student> students = new HashMap<>();
    private Map<Integer, Double> feeMap = new HashMap<>();
    private Map<Integer, Double> paymentMap = new HashMap<>();

    private DefaultListModel<String> feeListModel = new DefaultListModel<>();
    private DefaultListModel<String> paymentListModel = new DefaultListModel<>();
    private JTextArea balanceTextArea;

    public FeeManagementSystem() {
        setTitle("Fee Management System");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Fee Records", createFeePanel());
        tabs.addTab("Payments", createPaymentPanel());
        tabs.addTab("Balance", createBalancePanel());
        tabs.addTab("Utilities", createUtilitiesPanel());

        add(tabs);
        setVisible(true);
    }

    private JPanel createFeePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JList<String> list = new JList<>(feeListModel);
        panel.add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel input = new JPanel(new GridLayout(4, 2));
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField amountField = new JTextField();
        JButton addBtn = new JButton("Add Fee");

        input.add(new JLabel("Student ID:"));
        input.add(idField);
        input.add(new JLabel("Name:"));
        input.add(nameField);
        input.add(new JLabel("Fee Amount:"));
        input.add(amountField);
        input.add(new JLabel());
        input.add(addBtn);

        addBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                double amount = Double.parseDouble(amountField.getText());

                students.put(id, new Student(id, name));
                feeMap.put(id, feeMap.getOrDefault(id, 0.0) + amount);
                updateFeeList();
                updateBalanceArea();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input.");
            }
        });

        panel.add(input, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createPaymentPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JList<String> list = new JList<>(paymentListModel);
        panel.add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel input = new JPanel(new GridLayout(3, 2));
        JTextField idField = new JTextField();
        JTextField amountField = new JTextField();
        JButton addBtn = new JButton("Add Payment");

        input.add(new JLabel("Student ID:"));
        input.add(idField);
        input.add(new JLabel("Payment Amount:"));
        input.add(amountField);
        input.add(new JLabel());
        input.add(addBtn);

        addBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                double amount = Double.parseDouble(amountField.getText());

                if (!students.containsKey(id)) {
                    JOptionPane.showMessageDialog(this, "Student not found.");
                    return;
                }

                paymentMap.put(id, paymentMap.getOrDefault(id, 0.0) + amount);
                updatePaymentList();
                updateBalanceArea();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input.");
            }
        });

        panel.add(input, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createBalancePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        balanceTextArea = new JTextArea();
        balanceTextArea.setEditable(false);
        panel.add(new JScrollPane(balanceTextArea), BorderLayout.CENTER);

        JButton calculateBtn = new JButton("Calculate Balance");
        calculateBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Student ID:"));
                double fee = feeMap.getOrDefault(id, 0.0);
                double paid = paymentMap.getOrDefault(id, 0.0);
                double balance = fee - paid;

                balanceTextArea.setText("Balance for Student ID " + id + ": $" + balance);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID input.");
            }
        });

        panel.add(calculateBtn, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createUtilitiesPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 5, 5));

        JButton searchBtn = new JButton("Search Student by ID");
        JButton deleteBtn = new JButton("Delete Student Records");
        JButton summaryBtn = new JButton("Show Summary Report");
        JButton clearBtn = new JButton("Clear All Records");

        panel.setBorder(BorderFactory.createTitledBorder("Utilities"));
        panel.add(searchBtn);
        panel.add(deleteBtn);
        panel.add(summaryBtn);
        panel.add(clearBtn);

        searchBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Student ID:"));
                if (students.containsKey(id)) {
                    Student s = students.get(id);
                    double fee = feeMap.getOrDefault(id, 0.0);
                    double payment = paymentMap.getOrDefault(id, 0.0);
                    double balance = fee - payment;

                    JOptionPane.showMessageDialog(this,
                            "ID: " + s.getId() + "\nName: " + s.getName() +
                                    "\nFee: $" + fee + "\nPayment: $" + payment + "\nBalance: $" + balance,
                            "Student Details", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Student not found.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID input.");
            }
        });

        deleteBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Student ID to delete:"));
                if (students.containsKey(id)) {
                    students.remove(id);
                    feeMap.remove(id);
                    paymentMap.remove(id);
                    updateFeeList();
                    updatePaymentList();
                    updateBalanceArea();
                    JOptionPane.showMessageDialog(this, "Student and records deleted.");
                } else {
                    JOptionPane.showMessageDialog(this, "Student not found.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID input.");
            }
        });

        summaryBtn.addActionListener(e -> {
            double totalFees = feeMap.values().stream().mapToDouble(Double::doubleValue).sum();
            double totalPayments = paymentMap.values().stream().mapToDouble(Double::doubleValue).sum();
            double totalBalance = totalFees - totalPayments;

            JOptionPane.showMessageDialog(this,
                    "Total Fees: $" + totalFees +
                            "\nTotal Payments: $" + totalPayments +
                            "\nOutstanding Balance: $" + totalBalance,
                    "Summary Report", JOptionPane.INFORMATION_MESSAGE);
        });

        clearBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Clear all data?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                students.clear();
                feeMap.clear();
                paymentMap.clear();
                updateFeeList();
                updatePaymentList();
                updateBalanceArea();
                JOptionPane.showMessageDialog(this, "All records cleared.");
            }
        });

        return panel;
    }

    private void updateFeeList() {
        feeListModel.clear();
        for (int id : feeMap.keySet()) {
            feeListModel.addElement("ID: " + id + ", Fee: $" + feeMap.get(id));
        }
    }

    private void updatePaymentList() {
        paymentListModel.clear();
        for (int id : paymentMap.keySet()) {
            paymentListModel.addElement("ID: " + id + ", Payment: $" + paymentMap.get(id));
        }
    }

    private void updateBalanceArea() {
        balanceTextArea.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FeeManagementSystem());
    }
}
