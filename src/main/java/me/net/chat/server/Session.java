package me.net.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static me.net.socket.SocketCloseUtil.closeAll;
import static me.util.MyLogger.log;

public class Session implements Runnable {
    private final Socket socket;
    private final SessionManager sessionManager;
    private final CommandManager commandManager;
    private final DataInputStream input;
    private final DataOutputStream output;
    private boolean closed = false;
    private String username;

    public Session(Socket socket, SessionManager sessionManager, CommandManager commandManager) throws IOException {
        this.socket = socket;
        this.sessionManager = sessionManager;
        this.sessionManager.add(this);
        this.commandManager = commandManager;
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            while (true) {
                String received = input.readUTF();
                log("client -> server: " + received);
                commandManager.execute(received, this);
            }
        } catch (IOException e) {
            log(e);
        } finally {
            sessionManager.remove(this);
            sessionManager.sendAll(username + "님이 퇴장 했습니다.");
            close();
        }
    }

    public synchronized void send(String message) throws IOException {
        log("server -> client: " + message);
        output.writeUTF(message);
    }

    public synchronized void close() {
        if (closed) {
            return;
        }
        closeAll(socket, input, output);
        closed = true;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
