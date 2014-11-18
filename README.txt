***************************************README*******************************

**************************************TRANSFILE*****************************

What is TransFile?
******************

TransFile is a free file transfer protocol which contains both server and clients applications. 


How to install:
***************

From the zip :
	just unzip all the files into a directory you want.
    
From the unzipped directory :
    just type "ant compile" from a shell when your current location is the famous directory

	
How to use:
***********

To launch the Server :
    just type "java –cp classes fr.upemlv.transfile.server.FileServer". This command must be followed by some options.
    You will find all the server options available just below.
    
    Server Options :
    **********
    -p port : represents the port used by the server
    -a adress : The address of the server. Null joker address is used as a default. (optional)
    -d directory : server root directory

    Here comes all the commands availabe:
    	STATS : Print all the statistics of the file transferred and the total bytes transferred in transmission/reception
    	EXIT : Shutdown the server and close all connections with any clients and all the downloading files.


To launch a Client :
    just type "java –cp classes fr.upemlv.transfile.client.FileClient ADDRESS PORT".
    ADDRESS represents the server address, and PORT represents the server port.

    Here comes all the commands available :
    	LS      : prints all the files upon the server\'s current directory
    	CD XXX  : Changes the current server directory to XXX
    	GET XXX : downloads the file named XXX
    	STATUS  : prints all the current downloads with their fileID and their progress
    	KILL 0  : kills the process number 0
    	?       : prints the help
    	STOP    : closes the command scanner on System.in, and waits until all the current downloads are over
    	EXIT    : leaves the program and interrupts all the current downloads
    
How to clean:
*************
    just type "ant clean" from a shell.

How to use jar files:
********************
    just type "ant jar" from a shell in order to create both server's jar and client's jar.
    Then, "java -jar transfile-server.jar" to launch the server, followed by the correct options as described just above.
    or, "java -jar transfile-client.jar" to launch the client, followed by the correct options as well.
    
    
How to use javadoc:
********************
    just type "ant javadoc" from a shell.
    Then, open "index.html" in your web browser.


TransFile team:
***************
Gabriel DEBEAUPUIS - gdebeaup@etudiant.univ-mlv.fr
Jérémy FOUCAULT - jfouca01@etudiant.univ-mlv.fr

