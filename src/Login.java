import com.mysql.jdbc.PreparedStatement;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Login extends Application {
    private Stage mainWindow;

    // launch the app
    public static void main(String[] args) {
        launch(args);
    }

    // start the main window
    public void start(Stage primaryStage) {
        mainWindow = primaryStage;
        mainWindow.setTitle("Instant Messenger");
        mainWindow.setScene(loginScene());
        mainWindow.setResizable(false);
        mainWindow.show();

    }

    // login scene
    private Scene loginScene(){
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

        Label lblUsername = new Label("Username");
        TextField txtUsername = new TextField();
        Label lblPassword = new Label("Password");
        PasswordField pf = new PasswordField();
        Button btnLogin = new Button("Login");
        Button btnReg = new Button("Register");

        Label notification = new Label("");
        notification.setId("infoLabel");

        txtUsername.setText("imperius");
        pf.setText("imp");

        hbButtons.getChildren().addAll(btnLogin,btnReg);

        btnReg.setOnAction(e->{
            mainWindow.setScene(registerScene());
            mainWindow.setWidth(600);
            mainWindow.setHeight(400);
        });

        btnLogin.setOnAction(e->{
            login(txtUsername.getText(),pf.getText(),notification);
        });
        gridPane.add(lblUsername,0,0);
        gridPane.add(txtUsername,1,0);
        gridPane.add(lblPassword,0,1);
        gridPane.add(pf,1,1);
        gridPane.add(hbButtons,1,2);
        gridPane.add(notification,0,3);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetX(5);
        dropShadow.setOffsetY(5);

        Text text = new Text("Login");
        text.setFont(Font.font("Aerial",FontWeight.BOLD,28));
        text.setEffect(dropShadow);

        hbox.getChildren().add(text);

        borderPane.setId("borderPane");
        gridPane.setId("gridPane");
        btnLogin.setId("btnFirst");
        text.setId("text");
        btnReg.setId("btnSecond");

        borderPane.setTop(hbox);
        borderPane.setCenter(gridPane);

        Scene scene = new Scene(borderPane);
        scene.getStylesheets().add("login.css");
        return scene;
    }

    // register scene
    private Scene registerScene(){
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

        Label lblUsername = new Label("Username");
        TextField txtUsername = new TextField();
        Label lblEmail = new Label("Email");
        TextField txtEmail = new TextField();
        Label lblPassword = new Label("Password");
        PasswordField pf = new PasswordField();
        Label lblPassword2 = new Label("Password 2");
        PasswordField pf2 = new PasswordField();
        Button btnCnc = new Button("Cancel");
        Button btnReg = new Button("Register");

        Label userInfo = new Label("");
        userInfo.setId("infoLabel");
        Label emailInfo = new Label("");
        emailInfo.setId("infoLabel");
        Label pswInfo = new Label("");
        pswInfo.setId("infoLabel");


        hbButtons.getChildren().addAll(btnReg,btnCnc);
        btnCnc.setOnAction(e->mainWindow.setScene(loginScene()));


        btnReg.setOnAction(e->{
            boolean user,email,psw;

            if (txtUsername.getText().equals("")){
                userInfo.setText("Type in valid Username");
                user = false;
            }else{
                userInfo.setText("");
                user = true;
            }
            if (txtEmail.getText().equals("")){
                emailInfo.setText("Type in valid Email");
                email = false;
            }else{
                emailInfo.setText("");
                email = true;
            }
            if (!pf.getText().equals(pf2.getText())){
                pswInfo.setText("Password do not match");
                psw = false;
            }else if(pf.getText().equals("")){
                pswInfo.setText("Type in valid password");
                psw = false;
            }
            else{
                pswInfo.setText("");
                psw = true;
            }

            if(user && email && psw){
                register(txtUsername.getText(),txtEmail.getText(),pf.getText());
            }
        });

        gridPane.add(lblUsername,0,0);
        gridPane.add(txtUsername,1,0);
        gridPane.add(userInfo,2,0);
        gridPane.add(lblEmail,0,1);
        gridPane.add(txtEmail,1,1);
        gridPane.add(emailInfo,2,1);
        gridPane.add(lblPassword,0,2);
        gridPane.add(pf,1,2);
        gridPane.add(pswInfo,2,2);
        gridPane.add(lblPassword2,0,3);
        gridPane.add(pf2,1,3);
        gridPane.add(hbButtons,1,4);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetX(5);
        dropShadow.setOffsetY(5);

        Text text = new Text("Registration");
        text.setFont(Font.font("Aerial",FontWeight.BOLD,28));
        text.setEffect(dropShadow);

        hbox.getChildren().add(text);

        borderPane.setId("borderPane");
        gridPane.setId("gridPane");
        btnCnc.setId("btnSecond");
        text.setId("text");
        btnReg.setId("btnFirst");

        borderPane.setTop(hbox);
        borderPane.setCenter(gridPane);

        // new scene
        Scene scene = new Scene(borderPane);
        scene.getStylesheets().add("login.css");
        return scene;
    }

    // register function
    private void register(String username,String email,String psw){
        try {
            databaseHandler db = new databaseHandler();
            // query for updating the database
            String query = "INSERT INTO users(username,email,password,online_status)" +
                    "VALUES('" + username + "'," + "'"+email + "'," + "'" + psw + "'"+",'offline')";

            PreparedStatement ps = db.getPreparedStatement(query);

            // close streams
            ps.executeUpdate();
            ps.close();

            // new alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Successful Registration");
            alert.setHeaderText("Registration was successful");
            alert.setContentText("You can now login!");
            alert.showAndWait().ifPresent(rs->{
                if(rs == ButtonType.OK){

                    // return to login scene
                    mainWindow.setScene(loginScene());
                }
            });

        }catch(SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Error occurred with the Database");
            alert.setContentText(e.toString());
        }

    }

    // login function
    private void login(String username,String psw, Label notification){
        try{
            databaseHandler db = new databaseHandler();

            // get data query
            String query = "SELECT username,password FROM users WHERE username='"+username+"'";
            ResultSet resultSet = db.getResultSet(query);

            boolean password = false;

            // verify password
            while (resultSet.next()){
                if(psw.equals(resultSet.getString("password"))){
                    password = true;
                }
                else{
                    notification.setText("Password wrong");
                }
            }

            // if username not found
            if(!resultSet.first()){
                notification.setText("User does not exist.");
            }
            if(password){
                Main main = new Main();
                main.setUsername(username);
                main.start(mainWindow);
            }

            resultSet.close();

            // updates the online status to online
            query = "UPDATE users SET online_status='online' WHERE username='"+username+"'";
            PreparedStatement ps = db.getPreparedStatement(query);


            // close the streams
            ps.executeUpdate();
            ps.close();

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

}
