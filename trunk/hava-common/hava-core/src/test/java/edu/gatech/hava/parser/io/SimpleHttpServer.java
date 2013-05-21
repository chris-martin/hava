package edu.gatech.hava.parser.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleHttpServer {

    private final String response;
    private final ServerSocket serverSocket;

    public SimpleHttpServer(final int port, final String response)
            throws IOException {

        this.response = response;

        serverSocket = new ServerSocket(port, 1,
                InetAddress.getByName("127.0.0.1"));

        start();

    }

    public void start() {

        final Thread serverThread = new ServerThread();

        serverThread.start();

    }

    public void stop() throws IOException {

        serverSocket.close();

    }

    private class ServerThread extends Thread {

        @Override
        public void run() {

            try {

                while (true) {

                    final Socket socket = serverSocket.accept();

                    try {

                        final InputStream in = socket.getInputStream();
                        final byte[] buffer = new byte[2048];
                        in.read(buffer);

                        final OutputStream out = socket.getOutputStream();
                        out.write("HTTP/1.1 200 OK\r\n".getBytes());
                        out.write("Content-Type: text/html; charset=UTF-8\r\n\r\n".getBytes());
                        out.write(response.getBytes());
                        out.write("\r\n".getBytes());

                    } finally {
                        socket.close();
                    }

                }

            } catch (final Exception e) {
                ;
            }

        }

    }

}
