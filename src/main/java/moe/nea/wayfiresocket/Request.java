package moe.nea.wayfiresocket;

public interface Request<ResponseType> {
	Class<ResponseType> getResponseType();

	String getMethodName();
}
