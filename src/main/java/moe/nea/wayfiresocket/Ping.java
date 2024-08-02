package moe.nea.wayfiresocket;

public record Ping() implements Request<Ping.Response> {
	@Override
	public Class<Response> getResponseType() {
		return Response.class;
	}

	@Override
	public String getMethodName() {
		return "stipc/ping";
	}

	public record Response(String result) {}

}
