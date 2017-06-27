package org.openntf.xsp.groovyshell;

import com.ibm.xsp.library.AbstractXspLibrary;

public class GroovyShellLibrary extends AbstractXspLibrary {
	
	public static final String LIBRARY_ID = GroovyShellLibrary.class.getPackage().getName() + ".library"; //$NON-NLS-1$
	public static final String TAG_VERSION = "1.0.0"; //$NON-NLS-1$

	public GroovyShellLibrary() {
	}

	@Override
	public String getLibraryId() {
		return LIBRARY_ID;
	}
	
	@Override
	public String getPluginId() {
		return Activator.getInstance().getBundle().getSymbolicName();
	}

	@Override
	public String getTagVersion() {
		return TAG_VERSION;
	}

	@Override
	public String[] getDependencies() {
		return new String[] {
				"com.ibm.xsp.core.library", //$NON-NLS-1$
				"com.ibm.xsp.extsn.library", //$NON-NLS-1$
				"com.ibm.xsp.domino.library", //$NON-NLS-1$
				"com.ibm.xsp.designer.library" //$NON-NLS-1$
		};
	}

	@Override
	public boolean isGlobalScope() {
		return false;
	}
}
