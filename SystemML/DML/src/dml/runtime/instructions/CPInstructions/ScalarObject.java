package dml.runtime.instructions.CPInstructions;

import dml.parser.Expression.DataType;
import dml.parser.Expression.ValueType;

public abstract class ScalarObject extends Data {

	private String _name;

	public ScalarObject(String name, ValueType vt) {
		super(DataType.SCALAR, vt);
		_name = name;
	}

	public String getName() {
		return _name;
	}

	public abstract Object getValue();

	public abstract int getIntValue();

	public abstract double getDoubleValue();

	public abstract boolean getBooleanValue();

	public abstract String getStringValue();
	
	public abstract long getLongValue();
}
