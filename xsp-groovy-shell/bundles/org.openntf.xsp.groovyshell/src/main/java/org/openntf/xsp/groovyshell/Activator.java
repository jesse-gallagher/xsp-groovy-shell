package org.openntf.xsp.groovyshell;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import com.ibm.commons.log.Log;
import com.ibm.commons.log.LogMgr;

public class Activator extends Plugin {
	
	public static final String LOG_GROUP = Activator.class.getPackage().getName();
	public static final LogMgr LOG = Log.load(LOG_GROUP);
	static {
		LOG.setLogLevel(LogMgr.LOG_TRACEDEBUG_LEVEL);
	}
	
	private static Activator instance;
	
	public static Activator getInstance() {
		return instance;
	}

	public Activator() {
	}

	
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		if(LOG.isInfoEnabled()) {
			LOG.info("{0}: init", getClass().getName());
		}
		
		instance = this;
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		
		if(LOG.isInfoEnabled()) {
			LOG.info("{0}: term", getClass().getName());
		}
		
		instance = null;
	}
}
