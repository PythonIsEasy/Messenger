import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.ResultSet;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;
import java.sql.SQLException;

class databaseHandler {


    // return a data source
    private MysqlDataSource getDataSource() throws SQLException{
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setDatabaseName("messenger");
        dataSource.setServerName("localhost");
        dataSource.setPort(3306);
        return dataSource;
    }

    // return da jdbc connection
    private com.mysql.jdbc.Connection con() throws SQLException {
        return (com.mysql.jdbc.Connection) getDataSource().getConnection("root","");
    }

    // return a statement
    private Statement getStatement() throws SQLException{
        return (Statement)con().createStatement();
    }

    // return a prepared statement
    PreparedStatement getPreparedStatement(String query) throws SQLException{
        return (PreparedStatement)con().prepareStatement(query);
    }

    // return da resultset
    ResultSet getResultSet(String query) throws SQLException {
        return getStatement().executeQuery(query);
    }
}
