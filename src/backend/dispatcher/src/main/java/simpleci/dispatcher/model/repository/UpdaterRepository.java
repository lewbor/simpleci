package simpleci.dispatcher.model.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpleci.dispatcher.model.entity.JobStatus;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class UpdaterRepository
{
    final static Logger logger = LoggerFactory.getLogger(UpdaterRepository.class);

    private final DataSource dataSource;

    public UpdaterRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void jobStarted(long jobId, String status, Date startedAt) {
        String query = "UPDATE job set status = ?, started_at = ? WHERE id = ?";
        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, status);
                    statement.setTimestamp(2, new java.sql.Timestamp(startedAt.getTime()));
                    statement.setLong(3, jobId);
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            logger.error("", e);
        }
    }

    public void jobLog(long jobId, String output) {
        String query = "UPDATE job set log = CONCAT(log, ?) WHERE id = ?";
        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, output);
                    statement.setLong(2, jobId);
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            logger.error("", e);
        }
    }

    public void jobEnded(long jobId, String status, Date endedAt) {
        String query = "UPDATE job set status = ?, ended_at = ? WHERE id = ?";
        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, status);
                    statement.setTimestamp(2, new java.sql.Timestamp(endedAt.getTime()));
                    statement.setLong(3, jobId);
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            logger.error("", e);
        }
    }

    public void buildStarted(long buildId, Date startedAt) {
        String query = "UPDATE build set status = ?, started_at = ? WHERE id = ?";
        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, JobStatus.RUNNING);
                    statement.setTimestamp(2, new java.sql.Timestamp(startedAt.getTime()));
                    statement.setLong(3, buildId);
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            logger.error("", e);
        }
    }

    public void buildStopped(long buildId, String status, Date endedAt) {
        String query = "UPDATE build set status = ?, ended_at = ? WHERE id = ?";
        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, status);
                    statement.setTimestamp(2, new java.sql.Timestamp(endedAt.getTime()));
                    statement.setLong(3, buildId);
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            logger.error("", e);
        }
    }
}
