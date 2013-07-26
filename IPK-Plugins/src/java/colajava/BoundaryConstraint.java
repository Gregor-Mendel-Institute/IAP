/*
 * ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.31
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * -----------------------------------------------------------------------------
 */

package colajava;

public class BoundaryConstraint extends CompoundConstraint {
	private long swigCPtr;
	
	protected BoundaryConstraint(long cPtr, boolean cMemoryOwn) {
		super(colaJNI.SWIGBoundaryConstraintUpcast(cPtr), cMemoryOwn);
		swigCPtr = cPtr;
	}
	
	protected static long getCPtr(BoundaryConstraint obj) {
		return (obj == null) ? 0 : obj.swigCPtr;
	}
	
	@Override
	protected void finalize() {
		delete();
	}
	
	@Override
	public synchronized void delete() {
		if (swigCPtr != 0 && swigCMemOwn) {
			swigCMemOwn = false;
			colaJNI.delete_BoundaryConstraint(swigCPtr);
		}
		swigCPtr = 0;
		super.delete();
	}
	
	public BoundaryConstraint(double pos) {
		this(colaJNI.new_BoundaryConstraint(pos), true);
	}
	
	@Override
	public void updatePosition() {
		colaJNI.BoundaryConstraint_updatePosition(swigCPtr, this);
	}
	
	@Override
	public void generateVariables(SWIGTYPE_p_std__vectorTvpsc__Variable_p_t vars) {
		colaJNI.BoundaryConstraint_generateVariables(swigCPtr, this, SWIGTYPE_p_std__vectorTvpsc__Variable_p_t.getCPtr(vars));
	}
	
	@Override
	public void generateSeparationConstraints(SWIGTYPE_p_std__vectorTvpsc__Variable_p_t vars, SWIGTYPE_p_std__vectorTvpsc__Constraint_p_t cs) {
		colaJNI.BoundaryConstraint_generateSeparationConstraints(swigCPtr, this, SWIGTYPE_p_std__vectorTvpsc__Variable_p_t.getCPtr(vars),
							SWIGTYPE_p_std__vectorTvpsc__Constraint_p_t.getCPtr(cs));
	}
	
	public void setPosition(double value) {
		colaJNI.BoundaryConstraint_position_set(swigCPtr, this, value);
	}
	
	public double getPosition() {
		return colaJNI.BoundaryConstraint_position_get(swigCPtr, this);
	}
	
	public void setLeftOffsets(OffsetList value) {
		colaJNI.BoundaryConstraint_leftOffsets_set(swigCPtr, this, OffsetList.getCPtr(value), value);
	}
	
	public OffsetList getLeftOffsets() {
		long cPtr = colaJNI.BoundaryConstraint_leftOffsets_get(swigCPtr, this);
		return (cPtr == 0) ? null : new OffsetList(cPtr, false);
	}
	
	public void setRightOffsets(OffsetList value) {
		colaJNI.BoundaryConstraint_rightOffsets_set(swigCPtr, this, OffsetList.getCPtr(value), value);
	}
	
	public OffsetList getRightOffsets() {
		long cPtr = colaJNI.BoundaryConstraint_rightOffsets_get(swigCPtr, this);
		return (cPtr == 0) ? null : new OffsetList(cPtr, false);
	}
	
	public void setVariable(SWIGTYPE_p_vpsc__Variable value) {
		colaJNI.BoundaryConstraint_variable_set(swigCPtr, this, SWIGTYPE_p_vpsc__Variable.getCPtr(value));
	}
	
	public SWIGTYPE_p_vpsc__Variable getVariable() {
		long cPtr = colaJNI.BoundaryConstraint_variable_get(swigCPtr, this);
		return (cPtr == 0) ? null : new SWIGTYPE_p_vpsc__Variable(cPtr, false);
	}
	
}