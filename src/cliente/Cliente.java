package cliente;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
	public static void main(String[] args) throws IOException {
		// cria um socket com o google na porta 80
		Socket socket = new Socket("google.com.br", 80);
		// verifica se esta conectado
		if (socket.isConnected()) {
			// imprime o endere�o de IP do servidor
			System.out.println("Conectado a " + socket.getInetAddress());
		}

		/*
		 * veja que a requisi��o termina com \r\n que equivale a <CR><LF> para encerar a
		 * requisi��o tem uma linha em branco
		 */
		String requisicao = "" + "GET / HTTP/1.1\r\n" + "Host: www.google.com.br\r\n" + "\r\n";
		// OutputStream para enviar a requisi��o
		OutputStream envioServ = socket.getOutputStream();
		// temos que mandar a requisi��o no formato de vetor de bytes
		byte[] b = requisicao.getBytes();
		// escreve o vetor de bytes no "recurso" de envio
		envioServ.write(b);
		// marca a finaliza��o da escrita
		envioServ.flush();
		
		
		//cria um scanner a partir do InputStream que vem do servidor
	    Scanner sc = new Scanner(socket.getInputStream());
	    //enquanto houver algo para ler
	    while (sc.hasNext()) {
	        //imprime uma linha da resposta
	        System.out.println(sc.nextLine());
	    }
	}
}