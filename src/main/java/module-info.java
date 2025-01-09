module com.lantictactoe.lantictactoe {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.xerial.sqlitejdbc;



    opens com.lantictactoe.lantictactoe to javafx.fxml;
    exports com.lantictactoe.lantictactoe;
    exports com.lantictactoe.lantictactoe.Controllers;
    opens com.lantictactoe.lantictactoe.Controllers to javafx.fxml;
}