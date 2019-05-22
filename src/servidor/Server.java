package servidor;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Server {

    public static void main(String[] args) throws IOException {
        /* cria um socket "servidor" associado a porta 8000
         já aguardando conexões
         */
        ServerSocket servidor = new ServerSocket(8000);
        //aceita a primeita conexao que vier
        Socket socket = servidor.accept();
        //verifica se esta conectado
        if (socket.isConnected()) {
            //imprime na tela o IP do cliente
            System.out.println(socket.getInetAddress());
            //cria um BufferedReader a partir do InputStream do cliente

            RequestHTTP requisicao = RequestHTTP.lerRequisicao(socket.getInputStream());

            //se o caminho foi igual a / entao deve pegar o /index.html
            if (requisicao.getRecurso().equals("/")) {
                requisicao.setRecurso("src/servidor/index.html");
            } else {
            	requisicao.setRecurso("src/servidor/404.html");
            }
            //abre o arquivo pelo caminho
            File arquivo = new File(requisicao.getRecurso());

            ResponseHTTP resposta;

            //se o arquivo existir então criamos a reposta de sucesso, com status 200
            if (arquivo.exists()) {
                resposta = new ResponseHTTP(requisicao.getProtocolo(), 200, "OK");
            } else { 
                //se o arquivo não existe então criamos a reposta de erro, com status 404
                resposta = new ResponseHTTP(requisicao.getProtocolo(), 404, "Not Found");
            }
            //lê todo o conteúdo do arquivo para bytes e gera o conteudo de resposta
            resposta.setConteudoResposta(Files.readAllBytes(arquivo.toPath()));
            
            //converte o formato para o GMT espeficicado pelo protocolo HTTP
            SimpleDateFormat formatador = new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss", Locale.ENGLISH);
            formatador.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date data = new Date();
            //Formata a data para o padrao
            String dataFormatada = formatador.format(data) + " GMT";
            
            //cabeçalho padrão da resposta HTTP/1.1
            resposta.setCabecalho("Location", "https://localhost:8000/");
            resposta.setCabecalho("Date", dataFormatada);
            resposta.setCabecalho("Server", "MeuServidor/1.0");
            resposta.setCabecalho("Content-Type", "text/html");
            resposta.setCabecalho("Content-Length",resposta.getTamanhoResposta());
            //cria o canal de resposta utilizando o outputStream
            resposta.setSaida(socket.getOutputStream());
            resposta.enviar();

        }
    }
}