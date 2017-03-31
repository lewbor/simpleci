package simpleci.dispatcher.model.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetRowMapper<T> {
    T mapRow(ResultSet resultSet) throws SQLException;
}
