package org.naivs.perimeter.sshellmetry;

import org.naivs.perimeter.sshellmetry.data.Command;
import org.naivs.perimeter.sshellmetry.data.Response;

public interface Terminal {
    void connect();
    <T> Response<T> execute(Command<T> command);
    void disconnect();
    boolean isConnected();
}
