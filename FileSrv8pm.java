import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.file.*;

public class FileSrv8pm {

	public static void main(String[] args) throws IOException {

		int port = 3333;
		ServerSocket ssock = new ServerSocket(port);
		System.out.println("FILE server, running on port " + port +"...");

		final String crlf = "\r\n";
		final String crlf2 = crlf + crlf;
		while (true) {

			System.out.println("\nstarting to wait for new clients again...");
			Socket sock = ssock.accept();
			System.out.println("at " + new Date() + ", new client connection!");

			//PrintWriter out = new PrintWriter(sock.getOutputStream(),true);
			OutputStream out = sock.getOutputStream();
			InputStreamReader in = new InputStreamReader(sock.getInputStream());

			String req = "";
			for (int b = in.read(); b != -1; b = in.read()) {

				char c = (char)b;
				req += c;
				if (req.endsWith(crlf2)) {
					System.out.println("breaking bec crlf2");
					break;
				}
			}
			System.out.println("req from client:\n" + req);
			String firstLine = req.split(crlf)[0];
			if (firstLine.equals("")) {
				System.out.println("received EMPTY tcp msg, so skipping");
				out.flush();
				in.close();
				out.close();
				sock.close();
				continue;
			}
			String res = firstLine.split(" ")[1];
			System.out.println("file requested: " + res);

			String body = "HI browser\n\nI think you asked for file "+res;

			Path pathFile = Paths.get("." + res);
			boolean exists = Files.exists(pathFile);

			byte[] content;
			String status;
			if (exists) {
				status = "OK 200";
				//body += ", and it's there!";
				content = Files.readAllBytes(pathFile);
			} else {
				status = "404 File Not Found";
				body += ", IT'S NOT THERE!!";
				content = body.getBytes();
			}

			String head = "HTTP/1.1 " + status + crlf + "Content-Length: " + content.length + crlf2;
			String resp = head+body;
			System.out.println("sending resp to client:\n" + resp);
			//	    for (int i = 0; i < resp.length; i++)
			//	out((byte)resp
			//out.println(resp);
			out.write(head.getBytes());
			out.write(content);

			out.flush();
			in.close();
			out.close();
			sock.close();
		}
	}


}