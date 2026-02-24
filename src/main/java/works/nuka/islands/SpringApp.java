package works.nuka.islands;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import works.nuka.modularkit.ModularModule;
import works.nuka.modularkit.ModuleConfigModel;
import works.nuka.modularkit.ex.ModRegisterEx;
import works.nuka.modularkit.ex.ModUuidEx;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

@SpringBootApplication
@EnableCaching
public class SpringApp extends ModularModule {

    private ApplicationContext applicationContext;

    public SpringApp() throws ModUuidEx, ModRegisterEx {
        super();
    }

    public SpringApp(ModuleConfigModel moduleConfig) throws ModUuidEx, ModRegisterEx {
        super(moduleConfig);
    }

    public static void main(String[] args) {
    }

    @Override
    protected void stop() {
        if (applicationContext != null) {
            SpringApplication.exit(applicationContext, () -> 0);
        }
    }

    private void runLiquibaseMigrations(ClassLoader classLoader) {
        try {
            Properties props = new Properties();
            try (InputStream in = classLoader.getResourceAsStream("application.properties")) {
                if (in != null) props.load(in);
            }
            String url = props.getProperty("spring.datasource.url");
            String username = props.getProperty("spring.datasource.username");
            String password = props.getProperty("spring.datasource.password");

            URL jarUrl = classLoader.getResource("application.properties");
            String jarUrlStr = jarUrl.toString();
            int bangIdx = jarUrlStr.indexOf("!/");
            URL moduleJarUrl = new URL(jarUrlStr.substring(0, bangIdx + 2));

            try (URLClassLoader isolatedLoader = new URLClassLoader(new URL[]{moduleJarUrl}, null);
                 Connection conn = DriverManager.getConnection(url, username, password)) {
                Database database = DatabaseFactory.getInstance()
                        .findCorrectDatabaseImplementation(new JdbcConnection(conn));
                try (Liquibase liquibase = new Liquibase(
                        "db/changelog/db.changelog-master.xml",
                        new ClassLoaderResourceAccessor(isolatedLoader),
                        database)) {
                    liquibase.update("");
                    System.out.println("[Islands] Liquibase migrations applied successfully");
                }
            }
        } catch (Exception e) {
            System.err.println("[Islands] Liquibase migration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void start() {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();

        try {
            ClassLoader moduleClassLoader = this.getClass().getClassLoader();
            Thread.currentThread().setContextClassLoader(moduleClassLoader);

            runLiquibaseMigrations(moduleClassLoader);

            System.setProperty("org.springframework.boot.logging.LoggingSystem", "none");
            System.setProperty("server.port", "8081");
            SpringApplication app = new SpringApplication(SpringApp.class);
            app.setResourceLoader(new DefaultResourceLoader(moduleClassLoader));
            applicationContext = app.run();

        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
            System.clearProperty("org.springframework.boot.logging.LoggingSystem");
            System.clearProperty("server.port");
        }
    }

    @Override
    protected void load() {
    }

    @Override
    protected void unload() {
    }
}