CREATE DATABASE invoice;

Use invoice;
CREATE TABLE Clients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(15)
);

CREATE TABLE Invoices (
    id INT AUTO_INCREMENT PRIMARY KEY,
    client_id INT,
    invoice_date DATE,
    total_amount DECIMAL(10, 2),
    FOREIGN KEY (client_id) REFERENCES Clients(id)
);

CREATE TABLE Services (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255),
    rate DECIMAL(10, 2)
);

CREATE TABLE InvoiceItems (
    id INT PRIMARY KEY AUTO_INCREMENT,
    invoice_id INT,
    service_id INT,
    quantity INT,
    FOREIGN KEY (invoice_id) REFERENCES Invoices(id),
    FOREIGN KEY (service_id) REFERENCES Services(id)
);

CREATE TABLE invoice_services (
    id INT PRIMARY KEY AUTO_INCREMENT,
    invoice_id INT,
    service_id INT,
    quantity INT,
    hours DECIMAL(5,2),
    FOREIGN KEY (invoice_id) REFERENCES invoices(id),
    FOREIGN KEY (service_id) REFERENCES services(id)
);

