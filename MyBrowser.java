import java.io.*;
import java.net.*;

public class MyBrowser {

    public static void main(String[] args) throws Exception {

	String domain = "localhost";
	int port = 8080;

	if (args.length>0)
	    domain = args[0];

	if (args.length>1)
	    port = Integer.valueOf(args[1]);
	
	Socket sock = new Socket(domain,port);

	PrintStream out = new PrintStream(sock.getOutputStream());
	InputStreamReader in = new InputStreamReader(sock.getInputStream());

	String req = "GET / HTTP/1.1\nHost: " + domain + "\n\n";
	System.out.println("my http req: " + req);
	System.out.println('\n');
	

	out.print(req);

	boolean eoh = false;
	String head = "";
	String resp = "";
	int contlen = 219;
	int bodybytes = 0;
	boolean chunked = false;

	int chunks = 0;

	int n = 0;

	for (int b = in.read(); b != -1; b = in.read()) {

	    n++;
	    if (n>1500) break;
	    
	    if (chunks < 1 && eoh && chunked) {
		chunks++;
		System.out.println("!!!!!!! in eof chunked if, time num: " + chunks);;
		String hexLen = "" + (char)b;
		//hexLen += (char)in.read();
		//hexLen += (char)in.read();
		//hexLen += (char)in.read();
		int hexLenI = Integer.valueOf(hexLen,16);
		System.out.println("\n*********\n\nhexLen = " + hexLen + " which has an int has value: " + hexLenI + "\n\n*********\n");

		b = in.read();
	    }

	    System.out.print((char)b);
	    resp += (char)b;


	    
	    if (eoh) {

		bodybytes++;
		if (bodybytes == contlen) break;
	    }

	    if (resp.endsWith("\r\n\r\n") && !eoh) {

		    System.out.println("***** at end of header? ***");
		    head = resp;
		    eoh = true;

		    chunked = !head.contains("Content-Length: ");

		    if (!chunked) {
			String after = head.split("Content-Length: ")[1];
			String lenStr = after.split("\r")[0];
			contlen = Integer.valueOf(lenStr);
			System.out.println("##### contlen = " + contlen);
		    }
		    
	    }

	    //	    if (resp.endsWith("Content-Length:"))
	    //	System.out.println("!!!!!! " + resp);
	}



	System.out.println("\n\nhead:\n" + head);
	
	in.close();
	out.close();
	sock.close();
	
    }
}