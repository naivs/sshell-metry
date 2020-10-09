package org.naivs.perimeter.sshellmetry.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Optional.ofNullable;

public class SessionManager {

    private static final SessionManager instance;

    private final JSch jSch = new JSch();
    private Map<String, Session> sessions = new ConcurrentHashMap<>();

    static {
        instance = new SessionManager();
    }

    private SessionManager() {
    }

    public Session getSession(ConnectionInfo connectionInfo) throws JSchException {
        String key = createKey(connectionInfo.getUser(), connectionInfo.getHost(), connectionInfo.getPort());

        if (!sessions.containsKey(key)) {
            Session session = jSch.getSession(connectionInfo.getUser(),
                    connectionInfo.getHost(),
                    connectionInfo.getPort());
            session.setPassword(connectionInfo.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            sessions.put(key, session);
        }

        return sessions.get(key);
    }

    public void closeSession(Session session) {
        String key = createKey(session.getUserName(), session.getHost(), session.getPort());
        ofNullable(sessions.get(key)).ifPresent(Session::disconnect);
        sessions.remove(key);
    }

    public static SessionManager getInstance() {
        return instance;
    }

    private static String createKey(String username, String host, int port) {
        return String.format("%s@%s:%d", username, host, port);
    }
}
