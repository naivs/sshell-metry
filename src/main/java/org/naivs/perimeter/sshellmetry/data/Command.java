package org.naivs.perimeter.sshellmetry.data;

import java.util.function.Function;

public abstract class Command<T> {

    private final String payload;
    private final Function<String, T> responseParser;

    public Command(String payload, Function<String, T> responseParser) {
        this.payload = payload;
        this.responseParser = responseParser;
    }

    public Response<T> parseResponse(String response) {
        return new Response<>(0, responseParser.apply(response));
    }

    public String getPayload() {
        return payload;
    }
}
