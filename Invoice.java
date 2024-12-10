//Invoice.java
package invoice;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class Invoice {
    
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/invoice";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Legendary1!";

    public Invoice(int id, int clientId, Date invoiceDate, BigDecimal totalAmount) {
		// TODO Auto-generated constructor stub
	}


    private Connection getConnection() throws SQLException {
	
        return DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/invoice", "root", "Legendary1!");
    }

  //CLIENT PARTS OF CODE
    public void addClient(String name, String email, String phone) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO clients (name, email, phone) VALUES (?, ?, ?)")) {
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, phone);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    

    public List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM clients")) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                clients.add(new Client(id, name, email, phone));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }

    public void updateClient(int clientId, String name, String email, String phone) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE clients SET name=?, email=?, phone=? WHERE id=?")) {
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, phone);
            statement.setInt(4, clientId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

   
    public void deleteClient(int clientId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM clients WHERE id=?")) {
            statement.setInt(1, clientId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public Client getClientById(int clientId) {
        Client client = null;
        String query = "SELECT * FROM clients WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, clientId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String email = resultSet.getString("email");
                    String phone = resultSet.getString("phone");
                    client = new Client(clientId, name, email, phone);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return client;
    }
    
    
    
    //SERVICE PART OF CODE

    public void addService(String name, BigDecimal rate) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO services (name, rate) VALUES (?, ?)")) {
            statement.setString(1, name);
            statement.setBigDecimal(2, rate);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<Service> getAllServices() {
        List<Service> services = new ArrayList<>();
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM services")) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                BigDecimal rate = resultSet.getBigDecimal("rate");
                services.add(new Service(id, name, rate));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }

 
    public void updateService(int serviceId, String name, BigDecimal rate) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE services SET name=?, rate=? WHERE id=?")) {
            statement.setString(1, name);
            statement.setBigDecimal(2, rate);
            statement.setInt(3, serviceId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

 
    public void deleteService(int serviceId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM services WHERE id=?")) {
            statement.setInt(1, serviceId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
   
    public Service getServiceById(int serviceId) {
        Service service = null;
        String query = "SELECT * FROM services WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, serviceId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    BigDecimal rate = resultSet.getBigDecimal("rate");
                    service = new Service(serviceId, name, rate);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return service;
    }

    
    static class Service {
        private int id;
        private String name;
        private BigDecimal rate;

        public Service(int id, String name, BigDecimal rate) {
            this.id = id;
            this.name = name;
            this.rate = rate;
        }

         
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public BigDecimal getRate() {
            return rate;
        }

        public void setRate(BigDecimal rate) {
            this.rate = rate;
        }
    }
    
//INVOICE PART OF CODE
 
    public void createInvoice(int clientId, Date invoiceDate, List<InvoiceItem> items) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO invoices (client_id, invoice_date) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, clientId);
            statement.setDate(2, invoiceDate);
            statement.executeUpdate();

           
            ResultSet generatedKeys = statement.getGeneratedKeys();
            int invoiceId;
            if (generatedKeys.next()) {
                invoiceId = generatedKeys.getInt(1);

             
                for (InvoiceItem item : items) {
                    insertInvoiceItem(connection, invoiceId, item.getServiceId(), item.getHours());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void addInvoice(int clientId, Date invoiceDate, List<InvoiceItem> items) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO invoices (client_id, invoice_date) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, clientId);
            statement.setDate(2, invoiceDate);
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            int invoiceId = 0;
            if (generatedKeys.next()) {
                invoiceId = generatedKeys.getInt(1);
                for (InvoiceItem item : items) {
                    insertInvoiceItem(connection, invoiceId, item.getServiceId(), item.getHours());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

 
    
    public List<Invoicing> getAllInvoices() {
        List<Invoicing> invoices = new ArrayList<>();
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM invoices")) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int clientId = resultSet.getInt("client_id");
                Date invoiceDate = resultSet.getDate("invoice_date");
                BigDecimal totalAmount = getTotalAmountForInvoice(id);
                invoices.add(new Invoicing(id, clientId, invoiceDate, totalAmount));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }

    private void insertInvoiceItem(Connection connection, int invoiceId, int serviceId, int hours) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO invoice_services (invoice_id, service_id, hours) VALUES (?, ?, ?)")) {
            statement.setInt(1, invoiceId);
            statement.setInt(2, serviceId);
            statement.setInt(3, hours);
            statement.executeUpdate();
        }
    }
    
    public void updateInvoice(int invoiceId, Date invoiceDate, List<InvoiceItem> items) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE invoices SET invoice_date=? WHERE id=?")) {
                statement.setDate(1, invoiceDate);
                statement.setInt(2, invoiceId);
                statement.executeUpdate();
            }

            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM invoice_services WHERE invoice_id=?")) {
                statement.setInt(1, invoiceId);
                statement.executeUpdate();
            }

            for (InvoiceItem item : items) {
                insertInvoiceItem(connection, invoiceId, item.getServiceId(), item.getHours());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteInvoice(int invoiceId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM invoices WHERE id=?")) {
            statement.setInt(1, invoiceId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
 
    public BigDecimal getTotalAmountForInvoice(int invoiceId) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        String query = "SELECT SUM(services.rate * invoice_services.hours) AS total_amount " +
                "FROM invoice_services " +
                "JOIN services ON invoice_services.service_id = services.id " +
                "WHERE invoice_services.invoice_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, invoiceId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    totalAmount = resultSet.getBigDecimal("total_amount");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalAmount;
    }



   
    static class Invoicing {
        private int id;
        private int clientId;
        private Date invoiceDate;
        private BigDecimal totalAmount;

        public Invoicing(int id, int clientId, Date invoiceDate, BigDecimal totalAmount) {
            this.id = id;
            this.clientId = clientId;
            this.invoiceDate = invoiceDate;
            this.totalAmount = totalAmount;
        }
        public int getid() {
        	return id;
        }
        public void setid(int id) {
        	this.id = id;
        	
        }
        public int getClientId() {
        	return clientId;
        }
        public void setClientId(int clientId) {
        	this.clientId = clientId;
        }
        public Date getInvoiceDate() {
        	return invoiceDate;
        }
        public void setInvoiceDate(Date invoiceDate) {
        	this.invoiceDate = invoiceDate;
        }
        public BigDecimal getTotalAmount() {
        	return totalAmount;
        }
        public void setTotalAmount(BigDecimal totalAmount) {
        	this.totalAmount = totalAmount;
        }
         
    }


    static class InvoiceItem {
        private int serviceId;
        private String serviceName;
        private BigDecimal serviceRate;
        private int hours;

        // Constructor with serviceId, serviceName, serviceRate, and hours
        public InvoiceItem(int serviceId, String serviceName, BigDecimal serviceRate, int hours) {
            this.serviceId = serviceId;
            this.serviceName = serviceName;
            this.serviceRate = serviceRate;
            this.hours = hours;
        }

        // Getters and setters
        public int getServiceId() {
            return serviceId;
        }

        public void setServiceId(int serviceId) {
            this.serviceId = serviceId;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public BigDecimal getServiceRate() {
            return serviceRate;
        }

        public void setServiceRate(BigDecimal serviceRate) {
            this.serviceRate = serviceRate;
        }

        public int getHours() {
            return hours;
        }

        public void setHours(int hours) {
            this.hours = hours;
        }
    }


    // ANALYTICS PART OF CODE
    public BigDecimal getTotalIncomeForPeriod(Date startDate, Date endDate) {
        BigDecimal totalIncome = BigDecimal.ZERO;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT SUM(services.rate * invoice_services.hours) AS total_income " +
                     "FROM invoice_services " +
                     "JOIN services ON invoice_services.service_id = services.id " +
                     "JOIN invoices ON invoice_services.invoice_id = invoices.id " +
                     "WHERE invoices.invoice_date BETWEEN ? AND ?")) {
            statement.setDate(1, startDate);
            statement.setDate(2, endDate);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    totalIncome = resultSet.getBigDecimal("total_income");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalIncome;
    }

    public Service getMostPopularServiceForPeriod(Date startDate, Date endDate) {
        Service mostPopular = null;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT services.id, services.name, services.rate, SUM(invoice_services.hours) AS total_hours " +
                     "FROM invoice_services " +
                     "JOIN services ON invoice_services.service_id = services.id " +
                     "JOIN invoices ON invoice_services.invoice_id = invoices.id " +
                     "WHERE invoices.invoice_date BETWEEN ? AND ? " +
                     "GROUP BY services.id " +
                     "ORDER BY total_hours DESC LIMIT 1")) {
            statement.setDate(1, startDate);
            statement.setDate(2, endDate);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    BigDecimal rate = resultSet.getBigDecimal("rate");
                    mostPopular = new Service(id, name, rate);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mostPopular;
    }

    public Client getTopClientForPeriod(Date startDate, Date endDate) {
        Client topClient = null;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT clients.id, clients.name, clients.email, clients.phone, SUM(services.rate * invoice_services.hours) AS total_spent " +
                     "FROM invoices " +
                     "JOIN clients ON invoices.client_id = clients.id " +
                     "JOIN invoice_services ON invoices.id = invoice_services.invoice_id " +
                     "JOIN services ON invoice_services.service_id = services.id " +
                     "WHERE invoices.invoice_date BETWEEN ? AND ? " +
                     "GROUP BY clients.id " +
                     "ORDER BY total_spent DESC LIMIT 1")) {
            statement.setDate(1, startDate);
            statement.setDate(2, endDate);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String email = resultSet.getString("email");
                    String phone = resultSet.getString("phone");
                    topClient = new Client(id, name, email, phone);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topClient;
    }

  
    static class Client {
        private int id;
        private String name;
        private String email;
        private String phone;

        public Client(int id, String name, String email, String phone) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.phone = phone;
        }

         
        public int getID() {
        	return id;
        }
        public void setID(int id) {
        	this.id = id;
        }
        public String getName() {
        	return name;
        }
        public void setName(String name) {
        	this.name = name;
        }
        public String getEmail() {
        	return email;
        }
        public void setEmail(String email) {
        	this.email = email;
        }
        public String getPhone () {
        	return phone;
        }
        public void setPhone(String phone) {
        	this.phone = phone;
        }
    }

    

    public static void main(String[] args) {
        Invoice system = new Invoice(0, 0, null, null);
        
       
        
        
        
    }
    }
