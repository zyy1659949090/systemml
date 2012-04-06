package dml.runtime.instructions.CPInstructions;

import dml.parser.Expression.ValueType;
import dml.runtime.controlprogram.ProgramBlock;
import dml.runtime.matrix.operators.Operator;
import dml.runtime.matrix.operators.BinaryOperator;
import dml.utils.DMLRuntimeException;

public class ScalarScalarRelationalCPInstruction extends RelationalBinaryCPInstruction{
	public ScalarScalarRelationalCPInstruction(Operator op, 
											   CPOperand in1, 
											   CPOperand in2,
											   CPOperand out, 
											   String istr){
		super(op, in1, in2, out, istr);
	}
	
	@Override
	public ScalarObject processInstruction(ProgramBlock pb) throws DMLRuntimeException{
		ScalarObject so1 = pb.getScalarVariable(input1.get_name(), input1.get_valueType());
		ScalarObject so2 = pb.getScalarVariable(input2.get_name(), input2.get_valueType() );
		ScalarObject sores = null;
		
		BinaryOperator dop = (BinaryOperator) optr;
		
		if ( input1.get_valueType() == ValueType.INT && input2.get_valueType() == ValueType.INT ) {
			boolean rval = dop.fn.compare ( so1.getIntValue(), so2.getIntValue() );
			sores = (ScalarObject) new BooleanObject(rval); 
		}
		else if ( input1.get_valueType() == ValueType.DOUBLE && input2.get_valueType() == ValueType.DOUBLE ) {
			boolean rval = dop.fn.compare ( so1.getDoubleValue(), so2.getDoubleValue() );
			sores = (ScalarObject) new BooleanObject(rval); 
		}
		else if ( input1.get_valueType() == ValueType.INT && input2.get_valueType() == ValueType.DOUBLE ) {
			boolean rval = dop.fn.compare ( so1.getIntValue(), so2.getDoubleValue() );
			sores = (ScalarObject) new BooleanObject(rval); 
		}
		else if ( input1.get_valueType() == ValueType.DOUBLE && input2.get_valueType() == ValueType.INT ) {
			boolean rval = dop.fn.compare ( so1.getDoubleValue(), so2.getIntValue() );
			sores = (ScalarObject) new BooleanObject(rval); 
		}
		else if ( input1.get_valueType() == ValueType.BOOLEAN && input2.get_valueType() == ValueType.BOOLEAN ) {
			boolean rval = dop.fn.compare ( so1.getBooleanValue(), so2.getBooleanValue() );
			sores = (ScalarObject) new BooleanObject(rval); 
		}
		else throw new DMLRuntimeException("compare(): Invalid combination of value types.");
		
		pb.setVariable(output.get_name(), sores);
		return sores;
	}
}
