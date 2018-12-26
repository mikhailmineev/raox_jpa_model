package dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;

import org.hibernate.Session;
import org.hibernate.jpa.HibernatePersistenceProvider;

public final class SqlDataProvider {

	private static final String PROPERTY_PERSISTENCE_PASSWORD = "javax.persistence.jdbc.password";
	private static final String PROPERTY_PERSISTENCE_USER = "javax.persistence.jdbc.user";
	private static final String PROPERTY_PERSISTENCE_URL = "javax.persistence.jdbc.url";
	private static final String PROPERTY_PERSISTENCE_DRIVER = "javax.persistence.jdbc.driver";
	private static final String PROPERTY_PERSISTENCE_LOADED_CLASSES = org.hibernate.cfg.AvailableSettings.LOADED_CLASSES;
	private static final String PROPERTY_PERSISTENCE_DIALECT = org.hibernate.cfg.AvailableSettings.DIALECT;

	private String persistenceUnitName = "default";
	private final String driver;
	private final String url;
	private final String user;
	private final String password;
	private final String dialect;
	private final List<Class<?>> entities;

	private EntityManager entityManager;

	public SqlDataProvider(String driver, String url, String user, String password, String dialect, Class<?> entity,
			Class<?>... entities) {
		this.driver = driver;
		this.url = url;
		this.user = user;
		this.password = password;
		this.dialect = dialect;
		this.entities = new ArrayList<>(Arrays.asList(entities));
		this.entities.add(entity);
	}

	public SqlDataProvider setPersistenceUnitName(String persistenceUnitName) {
		this.persistenceUnitName = persistenceUnitName;
		return this;
	}

	public void clear() {
		Session session = getEntityManager().unwrap(Session.class);
		session.clear();
	}

	public EntityManager getEntityManager() {
		if (entityManager == null) {
			entityManager = createEntityManager();
		}
		return entityManager;
	}

	private EntityManager createEntityManager() {
		try {
			ClassLoader modelClassLoader = entities.get(0).getClassLoader();
			Map<String, Object> properties = new HashMap<>();
			properties.put(PROPERTY_PERSISTENCE_DRIVER, driver);
			properties.put(PROPERTY_PERSISTENCE_DIALECT, dialect);
			properties.put(PROPERTY_PERSISTENCE_LOADED_CLASSES, entities);
			properties.put(PROPERTY_PERSISTENCE_URL, url);
			properties.put(PROPERTY_PERSISTENCE_USER, user);
			properties.put(PROPERTY_PERSISTENCE_PASSWORD, password);

			PersistenceUnitInfo persistenceUnitInfo = new PersistenceUnitInfoImpl(persistenceUnitName,
					modelClassLoader);
			EntityManagerFactory emf = new HibernatePersistenceProvider()
					.createContainerEntityManagerFactory(persistenceUnitInfo, properties);
			return emf.createEntityManager();

		} catch (org.hibernate.exception.JDBCConnectionException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}