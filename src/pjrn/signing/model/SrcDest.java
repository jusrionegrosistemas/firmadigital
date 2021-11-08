package pjrn.signing.model;

/**
 * @author pjrn
 */
public class SrcDest {

	private String src;  //path al archivo a firmar
	private String dest; //path al archivo firmado, agrega _signed al nombre 
	
	public SrcDest(String src, String dest) {
		this.src = src;
		this.dest = dest;
	}
	
	public String dest() {
		return this.dest;
	}
	
	public String src() {
		return this.src;
	}
	
}
