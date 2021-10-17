package net.ausiasmarch.blogbuster2021;

import java.sql.Connection;
import java.sql.SQLException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariConnection {

    private HikariDataSource oPool;

    public HikariConnection(
            String connectionChain,
            String login,
            String password,
            Integer databaseMinPoolSize,
            Integer databaseMaxPoolSize) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(connectionChain);
        config.setUsername(login);
        config.setPassword(password);
        config.setMaximumPoolSize(databaseMaxPoolSize);
        config.setMinimumIdle(databaseMinPoolSize);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setLeakDetectionThreshold(15000);
        config.setConnectionTestQuery("SELECT 1");
        config.setConnectionTimeout(2000);

        oPool = new HikariDataSource(config);
    }

    public Connection newConnection() throws SQLException {
        return oPool.getConnection();
    }

    public void closePool() throws SQLException {
        if (oPool != null) {
            oPool.close();
        }
    }

}
