import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;

public class SupermarketManagementSystem extends Frame {
    private TextField employeeNameField, employeePositionField, productNameField, productPriceField, productQuantityField;
    private TextArea displayArea, sellProductArea;
    private TextField sellProductNameField, sellQuantityField;
    private Button addEmployeeButton, addProductButton, deleteEmployeeButton, deleteProductButton, viewDataButton, sellProductButton;

    private Connection connection;

    public SupermarketManagementSystem() {
        initializeDatabase();
        initializeUI();
    }

    private void initializeDatabase() {
        try {
            // Connect to your MySQL database
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/supermarket", "root", "Akhil@2004");

            // Create tables if they do not exist
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS employees (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL," +
                    "position VARCHAR(255) NOT NULL)");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS products (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL," +
                    "price DOUBLE NOT NULL," +
                    "quantity INT NOT NULL)");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS sales (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "product_name VARCHAR(255) NOT NULL," +
                    "quantity_sold INT NOT NULL," +
                    "total_price DOUBLE NOT NULL)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeUI() {
        setTitle("Supermarket Management System");
        setSize(1200, 1000);
        setLayout(null);
        setBackground(Color.gray);

        Label title = new Label("Super Market Management System");
        setLabelProperties(title, 250, 20, 500, 100);

        Label employeeNameLabel = new Label("Employee Name:");
        setLabelProperties(employeeNameLabel, 50, 150, 200, 50);

        Label employeePositionLabel = new Label("Employee Position:");
        setLabelProperties(employeePositionLabel, 50, 200, 200, 50);

        Label productNameLabel = new Label("Product Name:");
        setLabelProperties(productNameLabel, 50, 300, 200, 50);

        Label productPriceLabel = new Label("Product Price:");
        setLabelProperties(productPriceLabel, 50, 350, 200, 50);

        Label productQuantityLabel = new Label("Product Quantity:");
        setLabelProperties(productQuantityLabel, 50, 400, 200, 50);

        employeeNameField = createTextField(250, 150, 300, 50);
        employeePositionField = createTextField(250, 200, 300, 50);
        productNameField = createTextField(250, 300, 300, 50);
        productPriceField = createTextField(250, 350, 300, 50);
        productQuantityField = createTextField(250, 400, 300, 50);

        addEmployeeButton = createButton("Add Employee", 600, 180, 100, 50, Color.GREEN);
        addProductButton = createButton("Add Product", 600, 330, 100, 50, Color.GREEN);
        viewDataButton = createButton("View Data", 400, 450, 100, 50, Color.RED);
        deleteEmployeeButton = createButton("Delete Employee", 600, 230, 100, 50, Color.RED);
        deleteProductButton = createButton("Delete Product", 600, 380, 100, 50, Color.RED);

        add(deleteEmployeeButton);
        add(deleteProductButton);

        displayArea = new TextArea();
        displayArea.setBounds(20, 500, 700, 300);
        displayArea.setEditable(false);

        addEmployeeButton.addActionListener(e -> {
            addEmployee();
            displayData();
        });

        addProductButton.addActionListener(e -> {
            addProduct();
            displayData();
        });

        deleteEmployeeButton.addActionListener(e -> {
            deleteEmployee();
            displayData();
        });

        deleteProductButton.addActionListener(e -> {
            deleteProduct();
            displayData();
        });

        viewDataButton.addActionListener(e -> displayData());

        // Adding components for selling products
        Label sellProductLabel = new Label("Sell Product:");
        setLabelProperties(sellProductLabel, 750, 120, 200, 50);

        sellProductNameField = createTextField(750, 180, 300, 50);
        Label sellQuantityLabel = new Label("Quantity:");
        setLabelProperties(sellQuantityLabel, 750, 250, 200, 50);
        sellQuantityField = createTextField(750, 300, 100, 50);

        sellProductButton = createButton("Sell Product", 750, 400, 100, 50, Color.BLUE);

        sellProductButton.addActionListener(e -> {
            sellProduct();
            displayData();
            displaySoldProducts();
        });

        sellProductArea = new TextArea();
        sellProductArea.setBounds(750, 500, 400, 300);
        sellProductArea.setEditable(false);

        // Add the components to the frame
        addComponents(title, employeeNameLabel, employeeNameField, employeePositionLabel, employeePositionField,
                addEmployeeButton, productNameLabel, productNameField, productPriceLabel, productPriceField,
                productQuantityLabel, productQuantityField, addProductButton, viewDataButton, displayArea,
                sellProductLabel, sellProductNameField, sellQuantityLabel, sellQuantityField, sellProductButton, sellProductArea);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    private void setLabelProperties(Label label, int x, int y, int width, int height) {
        label.setBounds(x, y, width, height);
        label.setForeground(Color.BLUE);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        add(label);
    }

    private TextField createTextField(int x, int y, int width, int height) {
        TextField textField = new TextField();
        textField.setBounds(x, y, width, height);
        add(textField);
        return textField;
    }

    private Button createButton(String label, int x, int y, int width, int height, Color color) {
        Button button = new Button(label);
        button.setBounds(x, y, width, height);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        add(button);
        return button;
    }

    private void addComponents(Component... components) {
        for (Component component : components) {
            add(component);
        }
    }

    private void deleteEmployee() {
        try {
            String employeeId = (employeeNameField.getText());
            String deleteEmployeeSQL = "DELETE FROM employees WHERE name = ?";
            executeDeleteStatement(deleteEmployeeSQL, employeeId);
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteProduct() {
        try {
            String productId = productNameField.getText();
            String deleteProductSQL = "DELETE FROM products WHERE name = ?";
            executeDeleteStatement(deleteProductSQL, productId);
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void executeDeleteStatement(String deleteSQL, String id) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
            preparedStatement.setString(1, id);
            preparedStatement.executeUpdate();
        }
        clearFields();
    }

    private void addEmployee() {
        String name = employeeNameField.getText();
        String position = employeePositionField.getText();
        executeInsertStatement("INSERT INTO employees (name, position) VALUES (?, ?)", name, position);
    }

    private void addProduct() {
        String name = productNameField.getText();
        String priceStr = productPriceField.getText();
        String quantityStr = productQuantityField.getText();

        try {
            double price = Double.parseDouble(priceStr);
            int quantity = Integer.parseInt(quantityStr);

            executeInsertStatement("INSERT INTO products (name, price, quantity) VALUES (?, ?, ?)", name, price, quantity);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
    }

    private void executeInsertStatement(String insertSQL, Object... values) {
        try {
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                int parameterIndex = 1;
                for (Object value : values) {
                    preparedStatement.setObject(parameterIndex++, value);
                }
                preparedStatement.executeUpdate();
            }
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayData() {
        try {
            StringBuilder result = new StringBuilder("Employee Data:\n");
            appendDataFromResultSet(connection.createStatement().executeQuery("SELECT * FROM employees"), result, "ID", "Name", "Position");

            result.append("\nProduct Data:\n");
            appendDataFromResultSet(connection.createStatement().executeQuery("SELECT * FROM products"), result, "ID", "Name", "Price", "Quantity");

            displayArea.setText(result.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void sellProduct() {
        String productName = sellProductNameField.getText();
        String quantityStr = sellQuantityField.getText();

        try {
            int quantity = Integer.parseInt(quantityStr);

            // Check if the product is available in the inventory
            if (isProductAvailable(productName, quantity)) {
                // Update the quantity in the products table (assuming the product name is unique)
                String updateProductSQL = "UPDATE products SET quantity = quantity - ? WHERE name = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(updateProductSQL)) {
                    preparedStatement.setInt(1, quantity);
                    preparedStatement.setString(2, productName);
                    preparedStatement.executeUpdate();
                }

                // Insert the sale record into the 'sales' table
                executeInsertStatement("INSERT INTO sales (product_name, quantity_sold, total_price) VALUES (?, ?, ?)", productName, quantity, calculateTotalPrice(productName, quantity));

                // Display the sold product
                sellProductArea.append("Sold Product: " + productName + ", Quantity: " + quantity + "\n");
            } else {
                sellProductArea.append("Product not available in sufficient quantity.\n");
            }
        } catch (NumberFormatException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    private boolean isProductAvailable(String productName, int quantityToSell) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT quantity FROM products WHERE name = ?");
        preparedStatement.setString(1, productName);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            int availableQuantity = resultSet.getInt("quantity");
            return availableQuantity >= quantityToSell;
        } else {
            return false;
        }
    }

    private double calculateTotalPrice(String productName, int quantity) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT price FROM products WHERE name = ?");
        preparedStatement.setString(1, productName);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            double price = resultSet.getDouble("price");
            return price * quantity;
        } else {
            return 0;
        }
    }

    private void displaySoldProducts() {
        try {
            StringBuilder result = new StringBuilder("Sold Products:\n");
            ResultSet salesResultSet = connection.createStatement().executeQuery("SELECT * FROM sales");

            while (salesResultSet.next()) {
                int saleId = salesResultSet.getInt("id");
                String productName = salesResultSet.getString("product_name");
                int quantitySold = salesResultSet.getInt("quantity_sold");
                double totalPrice = salesResultSet.getDouble("total_price");

                // Retrieve the price from the products table for the sold product
                PreparedStatement productPriceStatement = connection.prepareStatement("SELECT price FROM products WHERE name = ?");
                productPriceStatement.setString(1, productName);
                ResultSet productPriceResultSet = productPriceStatement.executeQuery();
                double productPrice = 0;
                if (productPriceResultSet.next()) {
                    productPrice = productPriceResultSet.getDouble("price");
                }

                result.append("ID: ").append(saleId).append(", Product Name: ").append(productName)
                        .append(", Quantity Sold: ").append(quantitySold).append(", Price: ").append(productPrice)
                        .append(", Total Price: ").append(totalPrice).append("\n");
            }

            sellProductArea.setText(result.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void appendDataFromResultSet(ResultSet resultSet, StringBuilder result, String... columnNames) throws SQLException {
        while (resultSet.next()) {
            for (String columnName : columnNames) {
                result.append(columnName).append(": ").append(resultSet.getObject(columnName)).append(", ");
            }
            result.setLength(result.length() - 2); // Remove the trailing comma and space
            result.append("\n");
        }
    }

    private void clearFields() {
        employeeNameField.setText("");
        employeePositionField.setText("");
        productNameField.setText("");
        productPriceField.setText("");
        productQuantityField.setText("");
        sellProductNameField.setText("");
        sellQuantityField.setText("");
    }

    public static void main(String[] args)throws Exception {
        new SupermarketManagementSystem();
    }
}
