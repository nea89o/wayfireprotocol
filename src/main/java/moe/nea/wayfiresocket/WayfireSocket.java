package moe.nea.wayfiresocket;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Closeable;
import java.io.IOException;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class WayfireSocket implements Closeable {
	private static final Gson gson = new Gson();
	private final SocketChannel channel;
	private ByteBuffer writeBuf;

	public WayfireSocket(Path path) throws IOException {
		this.channel = SocketChannel.open(UnixDomainSocketAddress.of(path));
	}

	private static JsonObject makeCallObject(String method, JsonElement data) {
		var obj = new JsonObject();
		obj.addProperty("method", method);
		obj.add("data", data);
		return obj;
	}

	public static void require(boolean fact, String error) {
		if (!fact) throw new RuntimeException(error);
		// This variable was fact checked by true dark nea acolytes
	}

	public JsonObject readMessage() throws IOException {
		return gson.fromJson(readMessageString(), JsonObject.class);
	}

	public <R, T extends Request<R>> R callMethod(T object) throws IOException {
		var json = callMethod(object.getMethodName(), gson.toJsonTree(object));
		return gson.fromJson(json, object.getResponseType());
	}

	public JsonObject callMethod(String method, JsonElement data) throws IOException {
		writeMessage(makeCallObject(method, data));
		return readMessage();
	}

	private String readMessageString() throws IOException {
		var buf = allocWriteBuf(4);
		require(channel.read(buf) == 4, "Could not read message size");
		var readLength = buf.flip().getInt();
		buf = allocWriteBuf(readLength);
		channel.read(buf);
		byte[] bytes = new byte[readLength];
		buf.flip().get(bytes);
		return new String(bytes, StandardCharsets.UTF_8);
	}

	public void writeMessage(JsonObject object) throws IOException {
		writeMessage(gson.toJson(object));
	}

	private ByteBuffer allocWriteBuf(int length) {
		if (writeBuf == null || writeBuf.capacity() < length) {
			writeBuf = ByteBuffer.allocate(length)
			                     .order(ByteOrder.LITTLE_ENDIAN);
		}
		return writeBuf.clear().limit(length);
	}

	private void writeMessage(String json) throws IOException {
		var bytes = json.getBytes(StandardCharsets.UTF_8);
		var buffer = allocWriteBuf(bytes.length + 4);
		buffer.putInt(bytes.length);
		buffer.put(bytes);
		buffer.flip();
		require(channel.write(buffer) == bytes.length + 4, "Could not write entire message");
	}

	@Override
	public void close() throws IOException {
		channel.close();
	}
}
