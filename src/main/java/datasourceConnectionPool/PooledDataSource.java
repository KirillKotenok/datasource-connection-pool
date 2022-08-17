package datasourceConnectionPool;

import lombok.SneakyThrows;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PooledDataSource extends PGSimpleDataSource {
    private DataSource dataSource;
    private Queue<Connection> connectionPool;

    public PooledDataSource(DataSource dataSource) {
        this.connectionPool = new ConcurrentLinkedQueue<>();
        this.dataSource = dataSource;
        initConnectionPool(10);
    }

    @Override
    public Connection getConnection() {
        return connectionPool.poll();
    }

    @SneakyThrows
    private void initConnectionPool(int size) {
        for (int i = 0; i < size; i++) {
            Connection connection = new ConnectionProxy(dataSource.getConnection(), connectionPool);
            connectionPool.add(dataSource.getConnection());
        }
    }
}
