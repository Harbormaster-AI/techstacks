#header()
package  ${aib.getRootPackageName(true)}.provider;

import org.axonframework.common.jpa.EntityManagerProvider;
import javax.persistence.PersistenceContext;

import org.springframework.context.annotation.Bean;
import javax.persistence.EntityManager;

public class CustomEntityManagerProvider implements EntityManagerProvider {

    private EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @PersistenceContext(unitName = "eventStore")
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}