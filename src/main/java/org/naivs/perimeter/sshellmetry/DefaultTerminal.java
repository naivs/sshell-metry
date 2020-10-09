package org.naivs.perimeter.sshellmetry;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.RequiredArgsConstructor;
import org.naivs.perimeter.sshellmetry.data.Command;
import org.naivs.perimeter.sshellmetry.data.Response;
import org.naivs.perimeter.sshellmetry.ssh.ConnectionInfo;
import org.naivs.perimeter.sshellmetry.ssh.SessionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@RequiredArgsConstructor
public class DefaultTerminal implements Terminal {

    private final ConnectionInfo connectionInfo;
    private final SessionManager sessionManager = SessionManager.getInstance();
    private Session session;

    @Override
    public void connect() {
        try {
            session = sessionManager.getSession(connectionInfo);
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> Response<T> execute(Command<T> command) {
        if (session == null || !session.isConnected()) {
            System.err.println("Session not exists or already been closed.");
            return new Response<>(); // todo: return with error code
        }

        try {
            Channel channel = session.openChannel("exec");

            // execute payload and get response
            ((ChannelExec) channel).setCommand(command.getPayload());
//            shell.setOutputStream(System.err);
//            shell.setInputStream(null);
//            ((ChannelExec) shell).setErrStream(System.err);

            return command.parseResponse(readResponse(channel));
        } catch (JSchException e) {
            e.printStackTrace();
            return new Response<>(); // todo: return with error code
        }
    }

    @Override
    public void disconnect() {
        sessionManager.closeSession(session);
    }

    @Override
    public boolean isConnected() {
        return session.isConnected();
    }

    public ConnectionInfo connectionInfo() {
        return connectionInfo;
    }

    private String readResponse(Channel channel) throws JSchException {
        StringBuilder response = new StringBuilder();

        try(BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(channel.getInputStream()))
        ) {
            channel.connect();

            String line;
            while((line = bufferedReader.readLine()) != null){
                response.append(line);
            }

            channel.disconnect();
            return response.toString();
        } catch (IOException ex){
            ex.printStackTrace();
            return null;
        }
    }
}
