package org.openntf.xsp.groovyshell;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.ibm.commons.log.LogMgr;
import com.ibm.commons.util.StringUtil;
import com.ibm.domino.xsp.module.nsf.NSFComponentModule;
import com.ibm.domino.xsp.module.nsf.NotesContext;
import com.ibm.xsp.application.ApplicationEx;
import com.ibm.xsp.application.DesignerApplicationEx;
import com.ibm.xsp.application.events.ApplicationListener2;

import me.bazhenov.groovysh.GroovyShellService;

public class ApplicationListener implements ApplicationListener2 {
	
	public static final String PROP_PORT = ApplicationListener.class.getPackage().getName() + ".port"; //$NON-NLS-1$
	static final LogMgr log = Activator.LOG;
	
	private Map<String, GroovyShellService> servers = Collections.synchronizedMap(new HashMap<>());
	private NSFComponentModule module;

	@Override
	public void applicationCreated(ApplicationEx app) {
		if(usesLibrary(app)) {
			startService(app);
			
			NotesContext context = NotesContext.getCurrent();
			module = context.getModule();
		}
	}

	@Override
	public void applicationDestroyed(ApplicationEx app) {
		stopService(app);
	}

	@Override
	public void applicationRefreshed(ApplicationEx app) {
		stopService(app);
	}

	// *******************************************************************************
	// * Utility methods
	// *******************************************************************************
	
	private boolean usesLibrary(ApplicationEx app) {
		String prop = app.getProperty("xsp.library.depends", ""); //$NON-NLS-1$ //$NON-NLS-2$
		return Arrays.asList(prop.split(",")).contains(GroovyShellLibrary.LIBRARY_ID); //$NON-NLS-1$
	}
	
	private void startService(ApplicationEx app) {
		if(app instanceof DesignerApplicationEx) {
			com.ibm.designer.runtime.Application designerApp = ((DesignerApplicationEx)app).getDesignerApplication();
			String appName = designerApp.getAppName();
			
			String portProperty = app.getProperty(PROP_PORT, ""); //$NON-NLS-1$
			if(StringUtil.isEmpty(portProperty)) {
				if(log.isWarnEnabled()) {
					log.warn("{0}: Application '{1}' is missing required property '{2}'", GroovyShellLibrary.LIBRARY_ID, appName, PROP_PORT);
				}
			} else {
				int port;
				try {
					port = Integer.parseInt(portProperty, 10);
				} catch(NumberFormatException e) {
					if(log.isWarnEnabled()) {
						log.warn("{0}: Application '{1}' is configured for unparseable port '{2}'", GroovyShellLibrary.LIBRARY_ID, appName, portProperty);
					}
					return;
				}
				
				if(log.isInfoEnabled()) {
					log.info("{0}: Initializing Groovy shell in application '{1}' on SSH port {2}", GroovyShellLibrary.LIBRARY_ID, appName, port);
				}
				
				GroovyShellService service = createServer(app, port);
				try {
					service.start();
				} catch(IOException e) {
					if(log.isErrorEnabled()) {
						log.error(e, "{0}: Error initializing Groovy shell server for application '{1}' on port {2}", GroovyShellLibrary.LIBRARY_ID, appName, port);
					}
				}
				servers.put(appName, service);
			}
		}
	}
	
	private void stopService(ApplicationEx app) {
		if(app instanceof DesignerApplicationEx) {
			com.ibm.designer.runtime.Application designerApp = ((DesignerApplicationEx)app).getDesignerApplication();
			String appName = designerApp.getAppName();
			if(servers.containsKey(appName)) {
				GroovyShellService service = servers.remove(appName);
				if(service != null) {
					try {
						if(log.isInfoEnabled()) {
							log.info("{0}: Stopping Groovy shell in application '{1}'", GroovyShellLibrary.LIBRARY_ID, appName);
						}
						
						service.destroy();
					} catch(IOException e) {
						if(log.isErrorEnabled()) {
							log.error(e, "{0}: Error stopping Groovy shell server for application '{1}'", GroovyShellLibrary.LIBRARY_ID, appName);
						}
					}
				}
			}
		}
	}
	
	private GroovyShellService createServer(ApplicationEx app, int port) {
		GroovyShellService service = new GroovyShellService();
		service.setPort(port);
		
		ClassLoader appClassLoader = getApplicationClassLoader(app);
		ClassLoader cl = new DelegatingClassLoader(appClassLoader, getClass().getClassLoader());
		
		service.setThreadFactory(new DominoGroovyThreadFactory(cl));
		service.setClassLoader(cl);
		
		service.setThreadInitCallback(() -> {
			if(NotesContext.getCurrentUnchecked() == null) {
				NotesContext context = new NotesContext(module);
				NotesContext.initThread(context);
			}
		});
		
		return service;
	}
	
	/**
	 * Retrieves the {@link ClassLoader} for the provided XPages application.
	 * 
	 * @param app the application for which to find the <code>ClassLoader</code>
	 * @return the {@link ClassLoader} for the application
	 */
	private ClassLoader getApplicationClassLoader(ApplicationEx app) {
		if(app instanceof DesignerApplicationEx) {
			com.ibm.designer.runtime.Application designerApp = ((DesignerApplicationEx)app).getDesignerApplication();
			return designerApp.getClassLoader();
		} else {
			throw new ClassCastException("Application " + app + " is not an instance of " + com.ibm.designer.runtime.Application.class.getName());
		}
	}
}
