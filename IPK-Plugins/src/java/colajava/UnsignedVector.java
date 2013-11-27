/*
 * ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.31
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * -----------------------------------------------------------------------------
 */

package colajava;

public class UnsignedVector {
	private long swigCPtr;
	protected boolean swigCMemOwn;
	
	protected UnsignedVector(long cPtr, boolean cMemoryOwn) {
		swigCMemOwn = cMemoryOwn;
		swigCPtr = cPtr;
	}
	
	protected static long getCPtr(UnsignedVector obj) {
		return (obj == null) ? 0 : obj.swigCPtr;
	}
	
	@Override
	protected void finalize() {
		delete();
	}
	
	public synchronized void delete() {
		if (swigCPtr != 0 && swigCMemOwn) {
			swigCMemOwn = false;
			colaJNI.delete_UnsignedVector(swigCPtr);
		}
		swigCPtr = 0;
	}
	
	public UnsignedVector() {
		this(colaJNI.new_UnsignedVector__SWIG_0(), true);
	}
	
	public UnsignedVector(long n) {
		this(colaJNI.new_UnsignedVector__SWIG_1(n), true);
	}
	
	public long size() {
		return colaJNI.UnsignedVector_size(swigCPtr, this);
	}
	
	public long capacity() {
		return colaJNI.UnsignedVector_capacity(swigCPtr, this);
	}
	
	public void reserve(long n) {
		colaJNI.UnsignedVector_reserve(swigCPtr, this, n);
	}
	
	public boolean isEmpty() {
		return colaJNI.UnsignedVector_isEmpty(swigCPtr, this);
	}
	
	public void clear() {
		colaJNI.UnsignedVector_clear(swigCPtr, this);
	}
	
	public void add(long x) {
		colaJNI.UnsignedVector_add(swigCPtr, this, x);
	}
	
	public long get(int i) {
		return colaJNI.UnsignedVector_get(swigCPtr, this, i);
	}
	
	public void set(int i, long x) {
		colaJNI.UnsignedVector_set(swigCPtr, this, i, x);
	}
	
}
