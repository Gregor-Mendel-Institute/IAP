/*
 * ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.31
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * -----------------------------------------------------------------------------
 */

package colajava;

public class PolygonInterface {
	private long swigCPtr;
	protected boolean swigCMemOwn;
	
	protected PolygonInterface(long cPtr, boolean cMemoryOwn) {
		swigCMemOwn = cMemoryOwn;
		swigCPtr = cPtr;
	}
	
	protected static long getCPtr(PolygonInterface obj) {
		return (obj == null) ? 0 : obj.swigCPtr;
	}
	
	@Override
	protected void finalize() {
		delete();
	}
	
	public synchronized void delete() {
		if (swigCPtr != 0 && swigCMemOwn) {
			swigCMemOwn = false;
			colaJNI.delete_PolygonInterface(swigCPtr);
		}
		swigCPtr = 0;
	}
	
	public void clear() {
		colaJNI.PolygonInterface_clear(swigCPtr, this);
	}
	
	public boolean empty() {
		return colaJNI.PolygonInterface_empty(swigCPtr, this);
	}
	
	public int size() {
		return colaJNI.PolygonInterface_size(swigCPtr, this);
	}
	
	public int id() {
		return colaJNI.PolygonInterface_id(swigCPtr, this);
	}
	
	public Point at(int index) {
		return new Point(colaJNI.PolygonInterface_at(swigCPtr, this, index), false);
	}
	
}
