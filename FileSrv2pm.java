import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

/**
 * NETWORKING ASSIGNMENT
 * @author Daniel Weiner
 *
 */
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

			String password = "1234";
			boolean loggedIn = true;

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
			
			if (loggedIn == false)
			{
				String authorize = "<html><body><label for=\"Password\">Password:</label><input type=\"text\" id=\"Password\" name=\"Password\">";
				byte[] content2;
				content2 = authorize.getBytes();
				out.write(content2);
			}
			if (req.length()==0) {
				System.out.println("!!!!!!!empty request (?)");
				continue;
			}
			System.out.println("at " + new Date() + ", req received:\n" + req);
			System.out.println("req.length() = " + req.length());

			String firstLine = req.split(crlf)[0];
			String res = firstLine.split(" ")[1];
			res = res.split("\\?")[0];

			System.out.println("res req'ed: " + res);

			Path pathfile = Paths.get("." + res);
			boolean exists = Files.exists(pathfile);
			File dir = new File(pathfile.toString());
			//File dir1 = dir.getParent();
			System.out.println("\n\n\n!!!!!!!!!!!!" + dir.isDirectory() + "\n\n\n");
			System.out.println("\n\n\n!!!!!!!!!!!!" + dir + "\n\n\n");
			
			
			
			String color = null;
			
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
						content.length + crlf + "Set-Cookie: color="+color + crlf + crlf;
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