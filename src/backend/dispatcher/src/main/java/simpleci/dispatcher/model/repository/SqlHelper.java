package simpleci.dispatcher.model.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlHelper {
    private final static Logger logger = LoggerFactory.getLogger(SqlHelper.class);

    private final DataSource dataSource;

    public SqlHelper(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> T findOne(String query, StatementPreparer preparer, ResultSetRowMapper<T> rowMapper ) {
        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    preparer.prepare(statement);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        boolean found = resultSet.next();
                        if(!found) {
                            return null;
                        }
                        return rowMapper.mapRow(resultSet);
                        }
                    }
                }
        } catch (SQLException e) {
            logger.error("", e);
            return null;
        }

    }
}
