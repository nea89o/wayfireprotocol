package moe.nea.wayfiresocket;

public record Run(String cmd) implements Request<Run.Response> {

	@Override
	public Class<Response> getResponseType() {
		return Response.class;
	}

	@Override
	public String getMethodName() {
		return "stipc/run";
	}

	public record Response(int pid) {}
}
