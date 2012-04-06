package dml.runtime.instructions.CPInstructions;

import java.util.HashMap;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.ChiSquaredDistributionImpl;
import org.apache.commons.math.distribution.ExponentialDistributionImpl;
import org.apache.commons.math.distribution.FDistributionImpl;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.apache.commons.math.distribution.TDistributionImpl;

import dml.lops.Lops;
import dml.runtime.controlprogram.ProgramBlock;
import dml.runtime.functionobjects.Builtin;
import dml.runtime.functionobjects.ParameterizedBuiltin;
import dml.runtime.functionobjects.ValueFunction;
import dml.runtime.instructions.Instruction;
import dml.runtime.instructions.InstructionUtils;
import dml.runtime.matrix.operators.Operator;
import dml.runtime.matrix.operators.SimpleOperator;
import dml.utils.DMLRuntimeException;
import dml.utils.DMLUnsupportedOperationException;

public class ParameterizedBuiltinCPInstruction extends ComputationCPInstruction {
	int arity;
	HashMap<String,String> params;
	
	public ParameterizedBuiltinCPInstruction(Operator op, HashMap<String,String> paramsMap, CPOperand out, String istr )
	{
		super(op, null, null, out);
		cptype = CPINSTRUCTION_TYPE.ParameterizedBuiltin;
		params = paramsMap;
		instString = istr;
	}

	public int getArity() {
		return arity;
	}
	
	private static HashMap<String, String> constructParameterMap(String[] params) {
		// process all elements in "params" except first(opcode) and last(output)
		HashMap<String,String> paramMap = new HashMap<String,String>();
		
		// all parameters are of form <name=value>
		String[] parts;
		for ( int i=1; i <= params.length-2; i++ ) {
			parts = params[i].split(Lops.NAME_VALUE_SEPARATOR);
			paramMap.put(parts[0], parts[1]);
		}
		
		return paramMap;
	}
	public static Instruction parseInstruction ( String str ) throws DMLRuntimeException, DMLUnsupportedOperationException {


		String[] parts = InstructionUtils.getInstructionPartsWithValueType(str);
		// first part is always the opcode
		String opcode = parts[0];
		// last part is always the output
		CPOperand out = new CPOperand( parts[parts.length-1] ); 

		// process remaining parts and build a hash map
		HashMap<String,String> paramsMap = constructParameterMap(parts);

		// determine the appropriate value function
		ValueFunction func = null;
		if ( opcode.equalsIgnoreCase("cdf") ) {
			if ( paramsMap.get("dist") == null ) 
				throw new DMLRuntimeException("Probability distribution must to be specified to compute cumulative probability. (e.g., q = cumulativeProbability(1.5, dist=\"chisq\", df=20))");
			func = ParameterizedBuiltin.getParameterizedBuiltinFnObject(opcode, paramsMap.get("dist") );
		} 
		else {
			throw new DMLRuntimeException("Unknown opcode (" + opcode + ") for ParameterizedBuiltin Instruction.");
		}

		// Determine appropriate Function Object based on opcode
		return new ParameterizedBuiltinCPInstruction(new SimpleOperator(func), paramsMap, out, str);
	}
	
	@Override 
	public ScalarObject processInstruction(ProgramBlock pb) throws DMLRuntimeException {
		
		String opcode = InstructionUtils.getOpCode(instString);
		ScalarObject sores = null;
		
		if ( opcode.equalsIgnoreCase("cdf")) {
			SimpleOperator op = (SimpleOperator) optr;
			double result =  op.fn.execute(params);
			sores = new DoubleObject(result);
		} else {
			throw new DMLRuntimeException("Unknown opcode : " + opcode);
		}
		
		pb.setVariable(output.get_name(), sores);
		return sores;
	}
	

}
