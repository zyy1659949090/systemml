package dml.lops;

import dml.lops.LopProperties.ExecLocation;
import dml.lops.LopProperties.ExecType;
import dml.lops.compile.JobType;
import dml.parser.Expression.*;

/**
 * Lop to perform cross product operation
 * @author aghoting
 */
public class MMRJ extends Lops 
{
	
	/**
	 * Constructor to perform a cross product operation.
	 * @param input
	 * @param op
	 */

	public MMRJ(Lops input1, Lops input2, DataType dt, ValueType vt) 
	{
		super(Lops.Type.MMRJ, dt, vt);		
		this.addInput(input1);
		this.addInput(input2);
		input1.addOutput(this);
		input2.addOutput(this);
		
		/*
		 * This lop can be executed only in RMM job.
		 */
		
		boolean breaksAlignment = false;
		boolean aligner = false;
		boolean definesMRJob = true;
		lps.addCompatibility(JobType.MMRJ);
		this.lps.setProperties( ExecType.MR, ExecLocation.MapAndReduce, breaksAlignment, aligner, definesMRJob );
	}

	@Override
	public String toString() {
	
		return "Operation = MMRJ";
	}

	@Override
	public String getInstructions(int input_index1, int input_index2, int output_index)
	{
		String opString = new String(getExecType() + Lops.OPERAND_DELIMITOR);
		opString += "rmm";
		
		String inst = new String("");
		inst += opString + OPERAND_DELIMITOR + 
				input_index1 + VALUETYPE_PREFIX + this.getInputs().get(0).get_valueType() + OPERAND_DELIMITOR + 
				input_index2 + VALUETYPE_PREFIX + this.getInputs().get(1).get_valueType() + OPERAND_DELIMITOR + 
		        output_index + VALUETYPE_PREFIX + this.get_valueType() ;
		return inst;
	}

 
 
}