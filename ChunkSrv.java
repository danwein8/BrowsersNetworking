import java.io.*;
import java.net.*;
import java.util.*;

public class ChunkSrv {


    static String stars(String s) {

	String sts = "*********";
	return sts+"\n" + s + sts;
    }
    
    public static void main(String[] args) throws Exception {

	int port = 9999;
	var ssock = new ServerSocket(port);

	System.out.println("server running, listening on port " + port + " ...");
	System.out.println("( try entering this into your browser: localhost:"+port + "      )");
	System.out.println("( or run this at the command prompt:   curl localhost:"+port + " )");
	System.out.println("( or even:        curl \"localhost:9999/?w=1\" )\n");

	int cnum = 0;
	String crlf = "\r\n";
	while (true) {
	    cnum++;
	    var sock = ssock.accept();
	    var out = new PrintWriter(sock.getOutputStream(),true);
	    var in = new InputStreamReader(sock.getInputStream());
	    String req = "";
	    while (!req.endsWith("\r\n\r\n"))
		req += (char)in.read();

	    System.out.println("request received from client (#"+cnum+"):\n" + stars(req) + "\n");
	    

	    // String body = "<html><head></head><body>" + new Date() + "</body></html>";
	    //            String resp = "HTTP/1.1 200 OK\r\nContent-Length: " + body.length() + "\r\n\r\n" + body;

	    String head = "HTTP/1.1 200 OK\r\n"+
		"Transfer-Encoding: chunked\r\n"+
		"Content-Type: text/html\r\n\r\n";

            String body = "4\r\nWiki\r\n6\r\npedia \r\n10\r\nin \r\n\r\n\n\nchunks.\r\n0\r\n\r\n";

	    String body1 =
		"2F\r\n"+
                "<html><head><title>CHUNKY!</title></head><body>\r\n"+
		"c\r\n"+
		"<h1>go!</h1>\r\n"+
		"1b\r\n"+
		"<h1>first chunk loaded</h1>\r\n"+
		"2a\r\n"+
		"<h1>second chunk loaded and displayed</h1>\r\n"+
		"29\r\n"+
		"<h1>third chunk loaded and displayed</h1>\r\n"+
		"E\r\n"+
		"</body></html>\r\n"+
		"0\r\n"+
		"\r\n";

	    if (req.contains("?w=1")) body = body1;
	    
	    String resp = head+body;
	    out.println(resp);
	    out.flush();

	    System.out.println("response sent to client (#" + cnum + "):\n" + stars(resp) + "\n");
	}

    }

}