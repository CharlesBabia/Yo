//InvoiceGUI.java
package invoice;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class InvoiceGUI extends JFrame {

    private Invoice invoiceSystem;

    public InvoiceGUI() {
        invoiceSystem = new Invoice(0, 0, null, null);
        setTitle("Invoice Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Add tabs
        tabbedPane.addTab("Clients", createClientsPanel());
        tabbedPane.addTab("Services", createServicesPanel());
        tabbedPane.addTab("Orders", createOrdersPanel());
        tabbedPane.addTab("Invoices", createInvoicesPanel());
        tabbedPane.addTab("Analytics", createAnalyticsPanel());
        

        add(tabbedPane);
    }
    //Creates the Invoice Panel
    private JPanel createInvoicesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"ID", "Client ID", "Invoice Date", "Total Amount"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        loadInvoicesData(model);

        
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));


        JButton deleteButton = new JButton("Delete");
        formPanel.add(deleteButton);


        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) model.getValueAt(selectedRow, 0);
                invoiceSystem.deleteInvoice(id);
                loadInvoicesData(model);
            }
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);

        return panel;
    }
    //Orders Panel
    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Place an Order"));

       
        JTextField clientIdField = new JTextField();
        JTextField serviceIdField = new JTextField();
        JTextField quantityField = new JTextField();

        JButton orderButton = new JButton("Place Order");
        JTextArea resultArea = new JTextArea(5, 30);
        resultArea.setEditable(false);

        // Adding components to the panel
        panel.add(new JLabel("Client ID:"));
        panel.add(clientIdField);
        panel.add(new JLabel("Service ID:"));
        panel.add(serviceIdField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
        panel.add(orderButton);
        panel.add(new JScrollPane(resultArea));

        orderButton.addActionListener(e -> {
            try {
                int clientId = Integer.parseInt(clientIdField.getText());
                int serviceId = Integer.parseInt(serviceIdField.getText());
                int quantity = Integer.parseInt(quantityField.getText());

                // Para e check kung existing ba ang Client ug Service ID
                Invoice.Client client = invoiceSystem.getClientById(clientId);
                Invoice.Service service = invoiceSystem.getServiceById(serviceId);

                if (client == null) {
                    resultArea.setText("Client ID not found.");
                    return;
                }

                if (service == null) {
                    resultArea.setText("Service ID not found.");
                    return;
                }

                // Create invoice item and add to the system
                List<Invoice.InvoiceItem> items = new ArrayList<>();
                items.add(new Invoice.InvoiceItem(serviceId, service.getName(), service.getRate(), quantity));

                Date invoiceDate = new Date(System.currentTimeMillis());
                invoiceSystem.addInvoice(clientId, invoiceDate, items);

                resultArea.setText("Order placed successfully and invoice created!");

                // Refresh the invoices panel para dli na kaylangan mag close2 ayha ma display
                loadInvoicesData((DefaultTableModel) ((JTable) ((JScrollPane) ((JPanel) ((JTabbedPane) getContentPane()
                        .getComponent(0)).getComponentAt(3)).getComponent(0)).getViewport().getView()).getModel());

            } catch (NumberFormatException ex) {
                resultArea.setText("Please enter valid numbers for Client ID, Service ID, and Quantity.");
            }
        });

        return panel;
    }

//Analytics Panel
    private JPanel createAnalyticsPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Analytics"));

        JLabel totalIncomeLabel = new JLabel("Total Income for Period:");
        JTextField startDateField = new JTextField("2024-01-01");
        JTextField endDateField = new JTextField("2024-12-31");
        JButton calculateButton = new JButton("Calculate");

        JTextArea resultArea = new JTextArea(5, 30);
        resultArea.setEditable(false);

        panel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        panel.add(startDateField);
        panel.add(new JLabel("End Date (YYYY-MM-DD):"));
        panel.add(endDateField);
        panel.add(totalIncomeLabel);
        panel.add(calculateButton);
        panel.add(new JScrollPane(resultArea));

        calculateButton.addActionListener(e -> {
            Date startDate = Date.valueOf(startDateField.getText());
            Date endDate = Date.valueOf(endDateField.getText());

            BigDecimal totalIncome = invoiceSystem.getTotalIncomeForPeriod(startDate, endDate);
            Invoice.Service mostPopularService = invoiceSystem.getMostPopularServiceForPeriod(startDate, endDate);
            Invoice.Client topClient = invoiceSystem.getTopClientForPeriod(startDate, endDate);

            StringBuilder sb = new StringBuilder();
            sb.append("Total Income: $").append(totalIncome).append("\n\n");

            if (mostPopularService != null) {
                sb.append("Most Popular Service:\n")
                  .append("Name: ").append(mostPopularService.getName())
                  .append(", Rate: $").append(mostPopularService.getRate()).append("\n\n");
            } else {
                sb.append("Most Popular Service: None\n\n");
            }

            if (topClient != null) {
                sb.append("Top Client:\n")
                  .append("Name: ").append(topClient.getName())
                  .append(", Email: ").append(topClient.getEmail())
                  .append(", Phone: ").append(topClient.getPhone()).append("\n");
            } else {
                sb.append("Top Client: None\n");
            }

            resultArea.setText(sb.toString());
        });

        return panel;
    }

    private void loadInvoicesData(DefaultTableModel model) {
        model.setRowCount(0);
        List<Invoice.Invoicing> invoices = invoiceSystem.getAllInvoices();
        for (Invoice.Invoicing invoice : invoices) {
            model.addRow(new Object[]{
                    invoice.getid(),
                    invoice.getClientId(),
                    invoice.getInvoiceDate(),
                    invoice.getTotalAmount()
            });
        }
    }

    private JPanel createClientsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table for displaying clients
        String[] columns = {"ID", "Name", "Email", "Phone"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Load data
        loadClientsData(model);

        // Form for adding/updating clients
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Client Details"));

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);

        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");

        formPanel.add(addButton);
        formPanel.add(updateButton);
        formPanel.add(deleteButton);

        // Add listeners
        addButton.addActionListener(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();
            invoiceSystem.addClient(name, email, phone);
            loadClientsData(model);
        });

        updateButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) model.getValueAt(selectedRow, 0);
                String name = nameField.getText();
                String email = emailField.getText();
                String phone = phoneField.getText();
                invoiceSystem.updateClient(id, name, email, phone);
                loadClientsData(model);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) model.getValueAt(selectedRow, 0);
                invoiceSystem.deleteClient(id);
                loadClientsData(model);
            }
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createServicesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table for displaying services
        String[] columns = {"ID", "Name", "Rate"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Load data
        loadServicesData(model);

        // Form for adding/updating services
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Service Details"));

        JTextField nameField = new JTextField();
        JTextField rateField = new JTextField();

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Rate:"));
        formPanel.add(rateField);

        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");

        formPanel.add(addButton);
        formPanel.add(updateButton);
        formPanel.add(deleteButton);

        // Add listeners
        addButton.addActionListener(e -> {
            String name = nameField.getText();
            BigDecimal rate = new BigDecimal(rateField.getText());
            invoiceSystem.addService(name, rate);
            loadServicesData(model);
        });

        updateButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) model.getValueAt(selectedRow, 0);
                String name = nameField.getText();
                BigDecimal rate = new BigDecimal(rateField.getText());
                invoiceSystem.updateService(id, name, rate);
                loadServicesData(model);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) model.getValueAt(selectedRow, 0);
                invoiceSystem.deleteService(id);
                loadServicesData(model);
            }
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadClientsData(DefaultTableModel model) {
        model.setRowCount(0);
        List<Invoice.Client> clients = invoiceSystem.getAllClients();
        for (Invoice.Client client : clients) {
            model.addRow(new Object[]{client.getID(), client.getName(), client.getEmail(), client.getPhone()});
        }
    }

    private void loadServicesData(DefaultTableModel model) {
        model.setRowCount(0);
        List<Invoice.Service> services = invoiceSystem.getAllServices();
        for (Invoice.Service service : services) {
            model.addRow(new Object[]{service.getId(), service.getName(), service.getRate()});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InvoiceGUI gui = new InvoiceGUI();
            gui.setVisible(true);
        });
    }
}