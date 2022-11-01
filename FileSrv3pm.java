import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public class FileSrv3pm {

	public static void main(String[] args) throws IOException {
		System.out.println("I'm a web server that's a *file sever*...");

		final String crlf = "\r\n";

		int port = 11114;
		ServerSocket ssock = new ServerSocket(port);
		System.out.println("listening on port " + port + "...");
		while (true) {

			Socket sock = ssock.accept();
			System.out.println("received a new client connection!");

			OutputStream out = sock.getOutputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

			String req = "";
			String line;
			while ((line = in.readLine()) != null && !(line.equals("")) )
				req += (line+crlf);

			/*
	    if (line == null)
		System.out.println("line == null");
	    else
		System.out.println("line == " + line);
			 */
			if (req.length()==0) {
				System.out.println("!!!!!!!empty request (?)");
				continue;
			}
			System.out.println("at " + new Date() + ", req received:\n" + req);
			System.out.println("req.length() = " + req.length());

			String firstLine = req.split(crlf)[0];
			String res = firstLine.split(" ")[1];

			System.out.println("res req'ed: " + res);

			Path pathfile = Paths.get("." + res);
			boolean exists = Files.exists(pathfile);

			String body;
			String status;
			byte[] content;
			if (!exists) {
				body = "<html><body>this file does not exist: " + pathfile + "</body></html>";
				content = body.getBytes();
				status = "404 File Not Found";
			} else {
				body = "the file exists; here's where its content should go";
				status = "200 OK";
				content = Files.readAllBytes(pathfile);
			}	    

			String head = "HTTP/1.1 " + status + crlf + "Connection: close" + crlf +
					"Content-Length: " +
					//body.length() +
					content.length + crlf + crlf;
			//	    String resp = head + body;
			out.write(head.getBytes());
			out.write(content);
			out.flush();
			out.close();
			in.close();
			sock.close();
		}

	}


}