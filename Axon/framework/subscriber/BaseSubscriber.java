#header()
package ${aib.getRootPackageName()}.subscriber;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import org.springframework.beans.BeansException;

/**
 * Base class for subscribers
 * <p>
 * @author ${aib.getAuthor()}
 */
@Component
public class BaseSubscriber implements ApplicationContextAware {
 
	public BaseSubscriber() {	
	}
	
	@Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        applicationContext = ctx;
    }    

//************************************************************************
// Protected / Private Methods
//************************************************************************
    protected static ApplicationContext applicationContext;

}



