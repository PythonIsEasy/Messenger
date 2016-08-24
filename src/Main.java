import com.mysql.jdbc.PreparedStatement;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main extends Application {


    //global vars
    private TextField textField;
    private TextArea textArea;
    private PrintWriter writer;
    private String username;
    private ListView onlineList;
    private ListView offlineList;
    private Stage mainWindow;
    private Socket socket;

    // launch the app
    public static void main(String[] args) {
        launch(args);
    }

    // start primaryStage
    public void start(Stage primaryStage) {
        mainWindow = primaryStage;
        mainWindow.setWidth(500);
        mainWindow.setHeight(600);
        mainWindow.setScene(mainScene());

        // network function for connecting with server
        network();

        // close request function
        mainWindow.setOnCloseRequest(e -> {
            sendMessage(strOut());
            try {
                logout();
            } catch (IOException io) {
                io.printStackTrace();
            }
        });
    }


    // main scene
    private Scene mainScene() {
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(20, 20, 20, 20));

        MenuBar menuBar = new MenuBar();

        Menu menuProfile = new Menu(username);

        MenuItem menuLogout = new MenuItem("Logout");
        MenuItem menuAccount = new MenuItem("Account");


        menuAccount.setOnAction(e->mainWindow.setScene(accountScene()));

        // logout function
        menuLogout.setOnAction(e -> {
            sendMessage(strOut());
            try {
                logout();
                Login log = new Login();
                log.start(mainWindow);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        menuProfile.getItems().addAll(menuAccount,menuLogout);
        menuBar.getMenus().add(menuProfile);

        textField = new TextField();
        textArea = new TextArea();
        textArea.setPrefHeight(500);
        textArea.setEditable(false);
        textField.setOnAction(e -> {
            String message = username+": "+textField.getText();
            sendMessage(message);
            textField.clear();
            System.out.println(message);
        });

        onlineList = new ListView();
        offlineList = new ListView();

        VBox mainBox = new VBox();

        VBox leftBox = new VBox();
        leftBox.setPrefWidth(400);
        leftBox.setPadding(new Insets(5,5,5,5));
        leftBox.getChildren().addAll(textArea,textField);
        VBox rightBox = new VBox();

        Label lblOnline = new Label("Online");
        Label lblOffline = new Label("Offline");
        rightBox.setPrefWidth(100);
        rightBox.setPadding(new Insets(5,5,5,0));
        rightBox.getChildren().addAll(lblOnline,onlineList,lblOffline,offlineList);

        HBox centerBox = new HBox();
        centerBox.setPadding(new Insets(5,5,5,5));
        centerBox.getChildren().addAll(leftBox,rightBox);
        mainBox.getChildren().addAll(menuBar,centerBox);
        Scene scene = new Scene(mainBox);
        scene.getStylesheets().add("login.css");

        return scene;
    }

    private Scene accountScene(){
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10,50,50,50));

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(20,20,20,30));

        HBox hbButtons = new HBox();
        hbButtons.setPadding(new Insets(5,5,5,5));

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20,20,20,20));
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        Label lblUsername = new Label("New Username");
        TextField txtUsername = new TextField();
        Label lblEmail = new Label("New Email");
        TextField txtEmail = new TextField();
        Label lblPassword = new Label("New Password");
        PasswordField pf = new PasswordField();
        Label lblPassword2 = new Label("Confirm Password");
        PasswordField pf2 = new PasswordField();
        Button btnCnc = new Button("Cancel");
        Button btnReg = new Button("Save");

        btnCnc.setOnAction(e->mainWindow.setScene(mainScene()));
        btnReg.setOnAction(e->{
            String user = txtUsername.getText();
            String email = txtEmail.getText();
            String psw = pf.getText();
            String psw2 = pf2.getText();
            if (!user.equals("") && !email.equals("") && !psw.equals("")&& !psw2.equals("") && psw.equals(psw2)) {
                changeUserData(user,email,psw);
            }
            else{
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("Data is missing or wrong");
                alert.setContentText("Please review your data");
                alert.showAndWait().ifPresent(rs->{
                    if(rs == ButtonType.OK){
                        txtUsername.clear();
                        txtEmail.clear();
                        pf.clear();
                        pf2.clear();
                    }
                });
            }
        });


        hbButtons.getChildren().addAll(btnReg,btnCnc);

        gridPane.add(lblUsername,0,0);
        gridPane.add(txtUsername,1,0);
        gridPane.add(lblEmail,0,1);
        gridPane.add(txtEmail,1,1);
        gridPane.add(lblPassword,0,2);
        gridPane.add(pf,1,2);
        gridPane.add(lblPassword2,0,3);
        gridPane.add(pf2,1,3);
        gridPane.add(hbButtons,1,4);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetX(5);
        dropShadow.setOffsetY(5);

        Text text = new Text(username);
        text.setFont(Font.font("Aerial", FontWeight.BOLD,28));
        text.setEffect(dropShadow);

        hbox.getChildren().add(text);

        borderPane.setId("borderPane");
        gridPane.setId("gridPane");
        btnCnc.setId("btnSecond");
        text.setId("text");
        btnReg.setId("btnFirst");

        borderPane.setTop(hbox);
        borderPane.setCenter(gridPane);

        Scene scene = new Scene(borderPane);
        scene.getStylesheets().add("login.css");
        return scene;
    }

    // change the user data
    private void changeUserData(String name, String email, String psw){
        databaseHandler db = new databaseHandler();

        try {

            // mysql query
            String query = "UPDATE users SET username='" + name + "', email='" + email + "', password='" + psw +
                    "' WHERE username='" + username + "'";

            // statement to execute the query
            PreparedStatement ps = db.getPreparedStatement(query);
            ps.executeUpdate();
            ps.close();

            // alert
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Successful Change");
            alert.setHeaderText("Changing was successful");
            alert.setContentText("You successfully changed your data!");

            username = name;

            // alert's waiting for user
            alert.showAndWait().ifPresent(rs->{
                if(rs == ButtonType.OK){
                    mainWindow.setScene(mainScene());
                    userList();
                }
            });

        }catch(SQLException sql){
            sql.printStackTrace();
        }

    }

    // set username
    void setUsername(String user){
        username = user;
    }

    // updates the user lists
    private void userList(){
        try{
            databaseHandler db = new databaseHandler();

            // get all users
            String query = "SELECT * FROM users";
            ResultSet resultSet = db.getResultSet(query);


            // create the lists
            ObservableList<String> offlineUsers = FXCollections.observableArrayList();
            ObservableList<String> onlineUsers = FXCollections.observableArrayList();


            while(resultSet.next()){
                if(resultSet.getString("online_status").equals("online")){
                    // add user to online list
                    onlineUsers.add(resultSet.getString("username"));
                }else{
                    // add user to offline list
                    offlineUsers.add(resultSet.getString("username"));
                }
            }

            // set list items
            onlineList.setItems(onlineUsers);
            offlineList.setItems(offlineUsers);

            resultSet.close();

        }catch (SQLException se){
            se.printStackTrace();
        }

    }

    // logout function
    private void logout() throws IOException {
        try {
            databaseHandler db = new databaseHandler();

            // updates online status to offline
            String query = "UPDATE users SET online_status='offline' WHERE username='" + username + "'";

            PreparedStatement ps = db.getPreparedStatement(query);
            ps.executeUpdate();
            ps.close();

            // close the streams
            writer.close();
            socket.close();

        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private void network(){
        try{
            clientHandler client = new clientHandler();

            // open the streams
            socket = client.getSocket();
            writer = client.getWriter(socket);


            Thread readerThread = new Thread(new readMessage()); // new thread
            readerThread.start(); // start a new thread
            String message = username+" has logged in"; // message for all users
            sendMessage(message);

        }catch(IOException io){
            io.printStackTrace();
        }
    }

    private String strOut(){
        return username+" has logged off";
    }

    private void sendMessage(String message){
        // send a message to server
        writer.println(message);
        writer.flush();
        userList(); // update the list
    }

    // read message function
    private class readMessage implements Runnable{
        public void run() {
            try {
                String message;
                clientHandler client = new clientHandler();
                while ((message = client.getReader(socket).readLine()) != null) {
                    System.out.println(message);
                    textArea.appendText(message+"\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}



