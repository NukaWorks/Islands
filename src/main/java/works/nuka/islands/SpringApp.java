package works.nuka.islands;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import works.nuka.modularkit.ModularModule;
import works.nuka.modularkit.ModuleConfigModel;
import works.nuka.modularkit.ex.ModRegisterEx;
import works.nuka.modularkit.ex.ModUuidEx;

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

    @Override
    protected void start() {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();

        try {
            ClassLoader moduleClassLoader = this.getClass().getClassLoader();
            Thread.currentThread().setContextClassLoader(moduleClassLoader);
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