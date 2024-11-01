package service.serviceexceptions;

public class BadRequestException extends Exception{
  public BadRequestException(String message){
    super(message);
  }
}
