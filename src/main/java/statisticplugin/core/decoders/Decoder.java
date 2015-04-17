package statisticplugin.core.decoders;

public interface Decoder {
  
  public String decodeTerm(String fieldName, Object value) throws Exception;
  public String decodeStored(String fieldName, Object value) throws Exception;
}
