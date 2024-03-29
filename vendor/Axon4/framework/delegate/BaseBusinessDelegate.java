#header()
package ${aib.getRootPackageName()}.#getDelegatePackageName();

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import org.springframework.beans.BeansException;

/**
 * Base class for application business delegates.
 * <p>
 * @author ${aib.getAuthor()}
 */
@Component
public class BaseBusinessDelegate implements ApplicationContextAware {
 
	public BaseBusinessDelegate() {	
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



