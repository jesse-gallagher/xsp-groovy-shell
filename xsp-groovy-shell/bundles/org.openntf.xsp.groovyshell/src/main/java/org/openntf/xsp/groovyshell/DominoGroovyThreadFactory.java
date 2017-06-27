package org.openntf.xsp.groovyshell;

import org.apache.sshd.server.session.ServerSession;

import lotus.domino.NotesThread;
import me.bazhenov.groovysh.thread.ServerSessionAwareThreadFactory;

public class DominoGroovyThreadFactory implements ServerSessionAwareThreadFactory {
	private final ClassLoader classLoader;
	
	public DominoGroovyThreadFactory(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public Thread newThread(Runnable runnable, ServerSession session) {
		if(ApplicationListener.log.isInfoEnabled()) {
			ApplicationListener.log.info("{0}: Spawning thread for server session {1}", GroovyShellLibrary.LIBRARY_ID, session);
		}
		NotesThread result = new NotesThread(runnable);
		//result.setContextClassLoader(classLoader);
		ClassLoader cl = new DelegatingClassLoader(ApplicationListener.class.getClassLoader());
		result.setContextClassLoader(cl);
		return result;
	}
}