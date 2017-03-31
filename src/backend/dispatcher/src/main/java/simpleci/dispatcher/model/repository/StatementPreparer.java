package simpleci.dispatcher.model.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface StatementPreparer {
    void prepare(PreparedStatement ps) throws SQLException;
}
