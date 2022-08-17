package datasourceConnectionPool;

import lombok.SneakyThrows;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

public class Demo {
    public static void main(String[] args) {
        PGSimpleDataSource simpleDataSource = getDataSource();
        PooledDataSource pooledDataSource = new PooledDataSource(getDataSource());
        var start1 = System.nanoTime();
        testConnection(simpleDataSource);
        System.out.println((System.nanoTime() - start1) / 1000_000 + " ms");
        var start2 = System.nanoTime();
        testConnection(pooledDataSource);
        System.out.println((System.nanoTime() - start2) / 1000_000 + " ms");
    }

    @SneakyThrows
    private static void testConnection(DataSource dataSource) {
        try (var connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            for (int i = 0; i < 100_000; i++) {
                try (var statement = connection.createStatement()) {
                    var rs = statement.executeQuery("select random() from products");
                }
            }
            connection.rollback();
        }
    }

    static PGSimpleDataSource getDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/ProductsDatabase");
        dataSource.setUser("postgres");
        dataSource.setPassword("admin");
        return dataSource;
    }
}
