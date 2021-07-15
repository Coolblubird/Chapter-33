//Jordan Ashe 07-14-21
// Exercise31_01Server.java: The server can communicate with
// multiple clients concurrently using the multiple threads
import java.util.*;
import java.io.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import java.net.*;

public class Exercise33_01Server extends Application {
  // Text area for displaying contents
  private TextArea ta = new TextArea();

  @Override // Override the start method in the Application class
  public void start(Stage primaryStage) {
    ta.setWrapText(true);
   
    // Create a scene and place it in the stage
    Scene scene = new Scene(new ScrollPane(ta), 400, 200);
    primaryStage.setTitle("Exercise31_01Server"); // Set the stage title
    primaryStage.setScene(scene); // Place the scene in the stage
    primaryStage.show(); // Display the stage
    
    new Thread(() -> {
      try {
        // Create a server socket
        ServerSocket serverSocket = new ServerSocket(8000);
        Platform.runLater(() ->
        ta.appendText("Server started at " + new Date() + '\n'));
        
        // Listen for a connection request
        Socket socket = serverSocket.accept();
        
        // Create data input and output streams
        DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
        DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());
        
        while (true) {
          // Receive infor from the client
          double uAIR = inputFromClient.readDouble();
          double uLoan = inputFromClient.readDouble();
          int uYears = inputFromClient.readInt();
          
          Loan userLoan = new Loan(uAIR, uYears, uLoan);
          
          // Send area back to the client
          outputToClient.writeDouble(userLoan.getMonthlyPayment());
          outputToClient.writeDouble(userLoan.getTotalPayment());
          
          
          
          Platform.runLater(() -> {
            ta.appendText("AIR recieved from client: " + userLoan.getAnnualInterestRate() + '\n');
            ta.appendText("Loan Amount recieved from client: " + userLoan.getLoanAmount() + '\n');
            ta.appendText("Number of Years recieved from client: " + userLoan.getNumberOfYears() + '\n');
            ta.appendText("Monthly Payment recieved from client: " + userLoan.getMonthlyPayment() + '\n');
            ta.appendText("Total Payment recieved from client: " + userLoan.getTotalPayment() + '\n');
          });
        }
      }
      catch(IOException ex) {
        ex.printStackTrace();
      }
    }).start();
  }
    
  /**
   * The main method is only needed for the IDE with limited
   * JavaFX support. Not needed for running from the command line.
   */
  public static void main(String[] args) {
    launch(args);
  }
}
