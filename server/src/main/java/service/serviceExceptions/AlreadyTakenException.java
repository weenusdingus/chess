package service.serviceExceptions;

public class AlreadyTakenException extends Exception{
  public AlreadyTakenException(String message){
    super(message);
  }
}
