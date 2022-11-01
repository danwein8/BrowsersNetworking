import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

// Networking assignment
public class FileSrv2pm {

	public static void main(String[] args) throws IOException {
		String currentDir = System.getProperty("user.dir");
		System.out.println("Current dir using System: " + currentDir);
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
			File dir = new File(pathfile.toString());
			//File dir1 = dir.getParent();
			System.out.println("\n\n\n!!!!!!!!!!!!" + dir.isDirectory() + "\n\n\n");
			System.out.println("\n\n\n!!!!!!!!!!!!" + dir + "\n\n\n");

			String body = "";
			String status;
			String url = "http://localhost:11114/src/";
			byte[] content;
			if (dir.isDirectory()) {
				status = "200 OK";
				File[] listOfFiles = dir.listFiles();
				body += "<html><body>";
				for (int i = 0; i < listOfFiles.length; i++) {
					if (listOfFiles[i].isFile()) {
						System.out.println("File " + listOfFiles[i].getName());
						//body += "this file is: " + listOfFiles[i] + "<br>";
						//String link = listOfFiles[i].getName().split(".")[0];
						String link = listOfFiles[i].getName();
						body += "<a href =\"" + url +  link +"\">" + link + "</a><br>";
						
					} else if (listOfFiles[i].isDirectory()) {
						System.out.println("Directory " + listOfFiles[i].getName());
					}

				}					
				body += "</body></html>";
				content = body.getBytes();
				String head = "HTTP/1.1 " + status + crlf + "Connection: close" + crlf +
						"Content-Length: " +
						//body.length() +
						content.length + crlf + crlf;
				//	    String resp = head + body;
				out.write(head.getBytes());
				out.write(content);

				//content = Files.readAllBytes(pathfile);

			}
			else {
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
			}
			out.flush();
			out.close();
			in.close();
			sock.close();
		}

	}


}