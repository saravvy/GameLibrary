package app.config;

import app.entities.*;
import app.utils.Utils;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.LogManager;

public class HibernateConfig {
    /**
     * Purpose: This class is used to configure Hibernate and create an EntityManagerFactory.
     * Author: Thomas Hartmann
     */
    private static EntityManagerFactory emf;
    private static EntityManagerFactory emfForTest;
    private static boolean isIntegrationTest = false; // this flag is set for

    // Load logging configuration from logging.properties to remove some noise from Hibernate logs
    static {
        try (InputStream is = HibernateConfig.class.getClassLoader().getResourceAsStream("logging.properties")) {
            if (is != null) {
                LogManager.getLogManager().readConfiguration(is);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not load logging.properties", e);
        }
    }

    public static void setTestMode(boolean isTest) {
        HibernateConfig.isIntegrationTest = isTest;
    }

    public static EntityManagerFactory getEntityManagerFactoryForTest(){
        setTestMode(true);
        EntityManagerFactory testEmf = getEntityManagerFactory();
        setTestMode(false);
        return testEmf;
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (isIntegrationTest) {
            if (emfForTest == null || !emfForTest.isOpen())
                emfForTest = createEMF();
            return emfForTest;
        } else {
            if (emf == null || !emf.isOpen())
                emf = createEMF();
            return emf;
        }
    }

    public static void shutdownTestEmf() {
        if (emfForTest != null) {
            try {
                if (emfForTest.isOpen()) emfForTest.close();
            } finally {
                emfForTest = null;
            }
        }
    }

    public static void shutdown() {
        if (emf != null) {
            try {
                if (emf.isOpen()) emf.close();
            } finally {
                emf = null;
            }
        }
    }

    // TODO: IMPORTANT: Add Entity classes here for them to be registered with Hibernate
    private static void getAnnotationConfiguration(Configuration configuration) {
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Game.class);
        configuration.addAnnotatedClass(GameInLibrary.class);
        configuration.addAnnotatedClass(Library.class);
        configuration.addAnnotatedClass(Review.class);
        configuration.addAnnotatedClass(Wishlist.class);
        configuration.addAnnotatedClass(GameInWishlist.class);
    }

    private static EntityManagerFactory createEMF() {
        try {
            Configuration configuration = new Configuration();
            Properties props = new Properties();
            // Set the properties
            setBaseProperties(props);
            if (isIntegrationTest) {
                props = setTestProperties(props);
            } else if (System.getenv("DEPLOYED") != null) {
                setDeployedProperties(props);
            } else {
                props = setDevProperties(props);
            }
            configuration.setProperties(props);
            getAnnotationConfiguration(configuration);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            SessionFactory sf = configuration.buildSessionFactory(serviceRegistry);
            EntityManagerFactory emf = sf.unwrap(EntityManagerFactory.class);
            return emf;
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }


    private static String getDBName() {
        String dbName = System.getenv("DB_NAME");
        if (dbName == null || dbName.isBlank()) {
            dbName = System.getProperty("db.name");
        }
        if (dbName == null || dbName.isBlank()) {
            dbName = Utils.getPropertyValue("db.name", "config.properties");
        }
        return dbName;
    }

    private static Properties setBaseProperties(Properties props) {
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.connection.driver_class", "org.postgresql.Driver");
        props.put("hibernate.hbm2ddl.auto", "update");
        props.put("hibernate.current_session_context_class", "thread");
        String showSql = Utils.getPropertyValue("hibernate.show_sql", "config.properties");
        props.put("hibernate.show_sql", showSql);
        props.put("hibernate.format_sql", showSql);
        props.put("hibernate.use_sql_comments", showSql);
        return props;
    }

    private static Properties setDeployedProperties(Properties props) {
        props.setProperty("hibernate.connection.url", System.getenv("CONNECTION_STR") + getDBName());
        props.setProperty("hibernate.connection.username", System.getenv("DB_USERNAME"));
        props.setProperty("hibernate.connection.password", System.getenv("DB_PASSWORD"));
        return props;
    }

    private static Properties setDevProperties(Properties props) {
        String host = Utils.getPropertyValue("db.host", "config.properties");
        String port = Utils.getPropertyValue("db.port", "config.properties");
        props.put("hibernate.connection.url", "jdbc:postgresql://" + host + ":" + port + "/" + getDBName());
        props.put("hibernate.connection.username", Utils.getPropertyValue("db.username", "config.properties"));
        props.put("hibernate.connection.password", Utils.getPropertyValue("db.password", "config.properties"));
        return props;
    }

    private static Properties setTestProperties(Properties props) {
//        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.connection.driver_class", "org.testcontainers.jdbc.ContainerDatabaseDriver");
        props.put("hibernate.connection.url", "jdbc:tc:postgresql:15.3-alpine3.18:///test_db");
        props.put("hibernate.connection.username", "postgres");
        props.put("hibernate.connection.password", "postgres");
        props.put("hibernate.archive.autodetection", "class");
        props.put("hibernate.hbm2ddl.auto", "create-drop");
        return props;
    }
}
