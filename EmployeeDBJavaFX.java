import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

// Create a JavaFX application to accept input from user for Employees table and insert that into the Employees table.

public class EmployeeDBJavaFX extends Application {

	static TextField idField = new TextField();
	static TextField nameField = new TextField();
	static TextField titleField = new TextField();
	static TextField salaryField = new TextField();
	static TextField deptField = new TextField();
	static Button submitBtn = new Button("Submit");

	@Override
	public void start(Stage primaryStage) throws Exception {

		primaryStage.setTitle("Employee Database Update");
		GridPane pane = createFormPane(); // call method to create grid
		addUI(pane); // call method to add UI

		Scene scene = new Scene(pane);

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	} // end main

	GridPane createFormPane() {
		GridPane pane = new GridPane();
		pane.setAlignment(Pos.CENTER);
		pane.setPadding(new Insets(20, 20, 20, 20));
		pane.setHgap(15);
		pane.setVgap(15);
		return pane;
	}

	public void addUI(GridPane gridPane) {
		// header-----------------------------------
		Label headerLabel = new Label("New Employee Database Entry");
		headerLabel.setFont(Font.font("arial", FontWeight.BOLD, 16));
		gridPane.add(headerLabel, 0, 0, 3, 1);

		// employee ID ----------------------------------
		Label idLabel = new Label("Employee ID:");
		gridPane.add(idLabel, 0, 1);
		gridPane.add(idField, 1, 1);

		// name-------------------------------
		Label nameLabel = new Label("Employee name:");
		gridPane.add(nameLabel, 0, 2);
		gridPane.add(nameField, 1, 2);

		// job title -------------------------------
		Label titleLabel = new Label("Job title:");
		gridPane.add(titleLabel, 0, 3);
		gridPane.add(titleField, 1, 3);

		// salary ----------------------------------
		Label salaryLabel = new Label("Salary:");
		gridPane.add(salaryLabel, 0, 4);
		gridPane.add(salaryField, 1, 4);

		// dept ID ----------------------------------
		Label deptLabel = new Label("Department ID:");
		gridPane.add(deptLabel, 0, 5);
		gridPane.add(deptField, 1, 5);

		// submit btn ----------------------------------
		submitBtn.setPrefHeight(30);
		gridPane.add(submitBtn, 1, 6);

		// submit button event handler
		submitBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				// ********* DATABASE **********************
				String url = "jdbc:ucanaccess://ABC.accdb";
				Connection connection = null;
				Statement stmt = null;

				try {

					Class.forName("net.ucanaccess.jdbc.UcanaccessDriver"); // driver
					connection = DriverManager.getConnection(url, "", ""); // establish connection
					stmt = connection.createStatement(); // statement object to run SQL

					// assign user entry to employee table
					String id = idField.getText();
					String empName = nameField.getText();
					String empJobTitle = titleField.getText();
					double empSalary = Double.parseDouble(salaryField.getText());
					String deptId = deptField.getText();

					String SQL = "Insert into EMPLOYEES (empID, empName, empJobTitle, empSalary, DeptID)" + "Values('"
							+ id + "','" + empName + "','" + empJobTitle + "','" + empSalary + "','" + deptId + "')";
					
//					String SQL = "Delete from EMPLOYEES where empID = '" + id + "'";

					stmt.executeUpdate(SQL);

					SQL = "SELECT * FROM EMPLOYEES";
					ResultSet rst = stmt.executeQuery(SQL);
					ResultSetMetaData rsmd = rst.getMetaData();

					// display results to console
					for (int i = 1; i <= rsmd.getColumnCount(); i++)
						System.out.printf("%-20s", rsmd.getColumnName(i)); // get column headings from table
					System.out.println();
					System.out.println("----------------------------------------------------------------------------------");

					while (rst.next()) {
						for (int i = 1; i <= rsmd.getColumnCount(); i++) {
							if (!rsmd.getColumnName(i).equalsIgnoreCase("empSalary")) // bc salary is a diff data type
								System.out.printf("%-20s", rst.getObject(i));
							else
								System.out.printf("%-20s", NumberFormat.getCurrencyInstance().format(rst.getObject(i)));
						}
						System.out.println();
					}

					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setHeaderText(null);
					alert.setContentText("Entry Successful!");
					alert.show();

					connection.close();

				} catch (SQLException | ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		});

	} // end addUI
}
