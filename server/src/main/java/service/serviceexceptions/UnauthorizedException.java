package service.serviceexceptions;

public class UnauthorizedException extends Exception{
  public UnauthorizedException(String message){
    super(message);
  }
}
