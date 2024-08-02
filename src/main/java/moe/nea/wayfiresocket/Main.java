package moe.nea.wayfiresocket;

import java.nio.file.Path;

public class Main {
	public static void main(String[] args) throws Exception {
		try (var socket = new WayfireSocket(Path.of("/tmp/wayfire-wayland-2.socket"))) {
			System.out.println(socket.callMethod(new Ping()));
			Run.Response response = socket.callMethod(new Run("/bin/echo"));
			var pid = response.pid();
		}
	}
}
