import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.*;
import java.util.*;
import java.io.*;import java.beans.*;


public class Exercise33_09Server extends Application {
  private TextArea taServer = new TextArea();
  private TextArea taClient = new TextArea();
  DataInputStream inputFromClient;
  DataOutputStream outputToClient;
 
  @Override // Override the start method in the Application class
  public void start(Stage primaryStage) {
    taServer.setWrapText(true);
    taClient.setWrapText(true);
    taServer.setDisable(true);
    taServer.setStyle("-fx-opacity: 3;");

    BorderPane pane1 = new BorderPane();
    pane1.setTop(new Label("History"));
    pane1.setCenter(new ScrollPane(taServer));
    BorderPane pane2 = new BorderPane();
    pane2.setTop(new Label("New Message"));
    pane2.setCenter(new ScrollPane(taClient));
    
    VBox vBox = new VBox(5);
    vBox.getChildren().addAll(pane1, pane2);

    // Create a scene and place it in the stage
    Scene scene = new Scene(vBox, 200, 200);
    primaryStage.setTitle("Exercise31_09Server"); // Set the stage title
    primaryStage.setScene(scene); // Place the scene in the stage
    primaryStage.show(); // Display the stage
    
    new Thread(() -> {
      try {
        // Create a server socket
        ServerSocket serverSocket = new ServerSocket(8000);
        Platform.runLater(() ->
        taServer.appendText("Server started at " + new Date() + '\n'));

        // Listen for a connection request
        Socket socket = serverSocket.accept();

        // Create data input and output streams
        inputFromClient = new DataInputStream(socket.getInputStream());
        outputToClient = new DataOutputStream(socket.getOutputStream());
        
        EventHandler event = new EnterHandler();
        
        taClient.setOnKeyPressed(event);  
        
        while (true) {
          // Receive infor from the client
          int newMessageLength=inputFromClient.readInt();
          
          char[] temp = new char[newMessageLength];
          
          for (int i=0; i<newMessageLength; i++){
            temp[i] = inputFromClient.readChar();
          }
          
          String line = String.valueOf(temp);
        
          Platform.runLater(() -> {
            taServer.appendText("C: " + line + '\n');
          });
        }
      }
      catch(IOException ex) {
        ex.printStackTrace();
      }
    }).start();
    
  }


  class EnterHandler implements EventHandler<KeyEvent> {
    int messageLength = 0;
    
    @Override
    public void handle(KeyEvent e) {
      try{
        if (e.getCode().equals(KeyCode.ENTER)){
          taServer.appendText("S: " + taClient.getText() + '\n');
          messageLength = taClient.getText().length();
          outputToClient.writeInt(messageLength);
          outputToClient.writeChars(taClient.getText().trim());
          taClient.setText("");
        }
      }
      catch(IOException ex) {
        ex.printStackTrace();
      }
    }
  }
  
  
  /**
   * The main method is only needed for the IDE with limited
   * JavaFX support. Not needed for running from the command line.
   */
  public static void main(String[] args) {
    launch(args);
  }
}
