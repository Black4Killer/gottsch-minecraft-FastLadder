/**
 * 
 */
package com.someguyssoftware.fastladder.tileentity;

import com.someguyssoftware.gottschcore.positional.ICoords;

/**
 * @author Mark Gottschling onJan 1, 2018
 *
 */
public class TeleportTransaction {
	ICoords source;
	ICoords dest;
	
	/**
	 * 
	 */
	public TeleportTransaction() {
		
	}

	/**
	 * 
	 * @param source
	 * @param dest
	 */
	public TeleportTransaction(ICoords source, ICoords dest) {
		setSource(source);
		setDest(dest);
	}
	
	/**
	 * @return the source
	 */
	public ICoords getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(ICoords source) {
		this.source = source;
	}

	/**
	 * @return the dest
	 */
	public ICoords getDest() {
		return dest;
	}

	/**
	 * @param dest the dest to set
	 */
	public void setDest(ICoords dest) {
		this.dest = dest;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TeleportTransaction [source=" + source + ", dest=" + dest + "]";
	}
}
