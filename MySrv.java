import java.io.*;
import java.net.*;

public class MySrv {
	
	public static void main(String[] args) throws IOException {
		
		String domain = "localhost"; // or 127.0.0.1
		int port = 8080;
		ServerSocket listener = new ServerSocket(port);
		int n = 0;
		
		
		while (true) {

			System.out.println("Waiting for next client");


			Socket sock = listener.accept();
			System.out.println("new client arrived, client number " + n++);

			PrintStream out = new PrintStream(sock.getOutputStream());
			InputStreamReader in = new InputStreamReader(sock.getInputStream());
			
			String req = "";
			for (int b = (char)in.read(); b != -1; b = (char)in.read()) {
				req += b;
			}
			
			System.out.print("client request: " + req);

			String CRLF = "\r\n";


			String body = "<html><head><h1>Hello, you are web client request number " + n + "</h1></head></html>";
			//body = "4\r\nWiki\r\n6\r\npedia \r\n10\r\nin \r\n\r\n\n\nchunks.\r\n0\r\n\r\n";
			String head = "HTTP/1.1 200 OK" + CRLF + "Server: Lehman IT" + CRLF + "Content-Length: " + 
					body.length() + CRLF + 
					"Set-Cookie: SID=fadsvdgvjdafnkvj" + CRLF + CRLF;
			String resp = head + body;

			out.print(resp);

			in.close();
			out.close();
			sock.close();
			
			System.out.println("done with that client");
		}
	}
}
