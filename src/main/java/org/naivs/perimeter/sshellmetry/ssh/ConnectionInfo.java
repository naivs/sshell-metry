package org.naivs.perimeter.sshellmetry.ssh;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ConnectionInfo {
    private String host;
    private int port;
    private String user;
    private String password;
}
