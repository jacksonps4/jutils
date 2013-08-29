package com.minorityhobbies.util.bus;

import java.io.IOException;

public interface BusServer {
	void addConnection(BusMessageConnection connection);

	void start() throws IOException;
}