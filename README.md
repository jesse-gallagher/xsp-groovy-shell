# XPages Groovy Shell

This project is an implementation of [Groovysh Server](https://github.com/bazhenov/groovy-shell-server) that runs inside of an XPages application, using its ClassLoader.

## Usage

Enable the `org.openntf.xsp.groovyshell.library` library in your application and then add the `org.openntf.xsp.groovyshell.port` property to your Xsp Properties file, with the value of a port that is open on the server.

## Requirements

* Domino 9.0.1 Feature Pack 8 or above

## Known Issues

* There is currently no authentication mechanism, so this is horrifically insecure, even by my standards.
* The shell session doesn't really interact with the running Faces context, and so doesn't have access to the managed beans or other contextual information.
* The survival of the server is dependent on the survival of the XPages application and does nothing to extend it.
* There are some minor server-crash-related issues.
	* This is presumably due to the hijacking of normal thread and NotesContext behavior.
* Intermittently, the server will end a session with a "Bad packet length" message and refuse to accept new connections until an HTTP restart.

## TODO

* Add authentication, perhaps referencing the database's ACL to allow Designers and above, or requiring a special role.
* Consider investigating loading the servers at HTTP start instead of tying it to an ApplicationListener.

## License

The XPages Groovy shell is licensed under the Apache License 2.0.