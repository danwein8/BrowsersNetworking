import java.io.IOException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * A simple TCP server. When a client connects, it sends the client the current
 * datetime, then closes the connection. This is arguably the simplest server
 * you can write. Beware though that a client has to be completely served its
 * date before the server will be able to handle another client.
 *   stolen from the internet
 */
public class Cook8pm {
    public static void main(String[] args) throws IOException {
        try (var listener = new ServerSocket(9998)) {

	    String crlf = "\r\n";
	    String crlf2 = crlf+crlf;
	    System.out.println("The (cookie-using) server is running...");
            while (true) {
                try (var socket = listener.accept()) {

		    String color = null;
		    
		    InputStreamReader in = new InputStreamReader(socket.getInputStream());

		    String req = "";
		    System.out.println("received from client:\n<<<\n");
		    for (int i = in.read(); i != -1; i = in.read()) {
			System.out.print((char)i);
			req += (char)i;
			if (req.endsWith(crlf2)) break;
		    }
		    System.out.println(">>>\n");


		    String firstline = req.split("\r\n")[0];
		    System.out.println("firstline: "+firstline);
		    String getParamList = firstline.substring(firstline.indexOf("?")+1,firstline.indexOf("HTTP")-1);
		    //String path2 = firstline.split(" ")[1].substring(2);
		    System.out.println("getParmList: "+getParamList); //+",path2="+path2+".");
		    String[] GETKeyVals = getParamList.split("&");
		    for (int i = 0; i < GETKeyVals.length; i++) {
			System.out.println("   get param: " + GETKeyVals[i]);
		        String[] keyAndVal = GETKeyVals[i].split("=");
			if (keyAndVal[0].equals("color")) {
			    color = keyAndVal[1];
			    System.out.println("!!!!!color value from get param: "+color);
			}
		    }

		    if (color == null) {

			String cookieLine = req.split("Cookie: ")[1];
			cookieLine = cookieLine.split("\r\n")[0];
			String[] cookieKeyAndVal = cookieLine.split("=");
			if (cookieKeyAndVal[0].equals("color"))
			    color = cookieKeyAndVal[1];
			    System.out.println("!!!!color from cookie: " + color);
		    }
			
		    var out = new PrintWriter(socket.getOutputStream(), true);

		    String body = "<html><body style=\"background-color:"+color+"\">" + new Date().toString() + "</body></html>";
		    int contlen = body.length();
		    String head = "HTTP/1.1 200 OK\r\nContent-Length: " + contlen + "\r\n";
		    head += "Set-Cookie: color="+color + "\r\n";
		    head += "\r\n";
		    String resp = head + body;

		    System.out.println("sending to client:\n<<<\n" + resp);
                    out.println(resp);
		    System.out.println(">>>");
                }
            }
        }
    }
}