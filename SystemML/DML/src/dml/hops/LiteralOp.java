package dml.hops;

import dml.lops.Data;
import dml.lops.Lops;
import dml.lops.LopProperties.ExecType;
import dml.parser.Expression.DataType;
import dml.parser.Expression.ValueType;
import dml.sql.sqllops.SQLLopProperties;
import dml.sql.sqllops.SQLLops;
import dml.sql.sqllops.SQLLopProperties.AGGREGATIONTYPE;
import dml.sql.sqllops.SQLLopProperties.JOINTYPE;
import dml.sql.sqllops.SQLLops.GENERATES;
import dml.utils.HopsException;

public class LiteralOp extends Hops {

	private double value_double;
	private long value_long;
	private String value_string;
	private boolean value_boolean;

	// INT, DOUBLE, STRING, BOOLEAN}

	public LiteralOp(String l, double value) {
		super(Kind.LiteralOp, l, DataType.SCALAR, ValueType.DOUBLE);
		this.value_double = value;
	}

	public LiteralOp(String l, long value) {
		super(Kind.LiteralOp, l, DataType.SCALAR, ValueType.INT);
		this.value_long = value;
	}

	public LiteralOp(String l, String value) {
		super(Kind.LiteralOp, l, DataType.SCALAR, ValueType.STRING);
		this.value_string = value;
	}

	public LiteralOp(String l, boolean value) {
		super(Kind.LiteralOp, l, DataType.SCALAR, ValueType.BOOLEAN);
		this.value_boolean = value;
	}

	@Override
	public Lops constructLops()
			throws HopsException {

		if (get_lops() == null) {

			Lops l = null;

			switch (get_valueType()) {
			case DOUBLE:
				l = new Data(null,
						Data.OperationTypes.READ, null, Double
								.toString(value_double), get_dataType(),
						get_valueType(), false);
				break;
			case BOOLEAN:
				l = new Data(null,
						Data.OperationTypes.READ, null, Boolean
								.toString(value_boolean), get_dataType(),
						get_valueType(), false);
				break;
			case STRING:
				l = new Data(null,
						Data.OperationTypes.READ, null, value_string,
						get_dataType(), get_valueType(), false);
				break;
			case INT:
				l = new Data(null,
						Data.OperationTypes.READ, null, Long.toString(value_long), get_dataType(),
						get_valueType(), false);
				break;
			default:
				throw new HopsException(
						"unexpected value type constructing lops.\n");

			}

			l.getOutputParameters().setDimensions(get_dim1(), get_dim2(),
					get_rows_per_block(), get_cols_per_block());
			set_lops(l);
		}

		return get_lops();
	}

	public void printMe() throws HopsException {
		if (get_visited() != VISIT_STATUS.DONE) {
			super.printMe();
			switch (get_valueType()) {
			case DOUBLE:
				System.out.println("  Value: " + value_double + "\n");
				break;
			case BOOLEAN:
				System.out.println("  Value: " + value_boolean + "\n");
				break;
			case STRING:
				System.out.println("  Value: " + value_string + "\n");
				break;
			case INT:
				System.out.println("  Value: " + value_long + "\n");
				break;
			default:
				throw new HopsException(
						"unexpected value type printing LiteralOp.\n");
			}

			for (Hops h : getInput()) {
				h.printMe();
			}
			;
		}
		set_visited(VISIT_STATUS.DONE);
	}

	@Override
	public String getOpString() {
		String val = "";
		switch (get_valueType()) {
		case DOUBLE:
			val = Double.toString(value_double);
			break;
		case BOOLEAN:
			val = Boolean.toString(value_boolean);
			break;
		case STRING:
			val = value_string;
			break;
		case INT:
			val = Long.toString(value_long);
			break;
		}
		return "LiteralOp" + val;
	}
	
	private SQLLopProperties getProperties()
	{
		SQLLopProperties prop = new SQLLopProperties();
		prop.setJoinType(JOINTYPE.NONE);
		prop.setAggType(AGGREGATIONTYPE.NONE);
		
		String val = null;
		switch (get_valueType()) {
		case DOUBLE:
			val = Double.toString(value_double);
			break;
		case BOOLEAN:
			val = Boolean.toString(value_boolean);
			break;
		case STRING:
			val = value_string;
			break;
		case INT:
			val = Long.toString(value_long);
			break;
		}
		
		prop.setOpString(val);
		
		return prop;
	}

	@Override
	public SQLLops constructSQLLOPs() throws HopsException {
		/*
		 * Does not generate SQL, instead the actual value is passed in the table name and can be inserted directly
		 */
		if(this.get_sqllops() == null)
		{
			SQLLops sqllop = new SQLLops(this.get_name(),
										GENERATES.NONE,
										this.get_valueType(),
										this.get_dataType());

			//Retrieve string for value
			if(this.get_valueType() == ValueType.DOUBLE)
				sqllop.set_tableName(String.format(Double.toString(this.value_double)));
			else if(this.get_valueType() == ValueType.INT)
				sqllop.set_tableName(String.format(Long.toString(this.value_long)));
			else if(this.get_valueType() == ValueType.STRING)
				sqllop.set_tableName("'" + this.value_string + "'");
			else if(this.get_valueType() == ValueType.BOOLEAN)
				sqllop.set_tableName(Boolean.toString(this.value_boolean));
			
			sqllop.set_properties(getProperties());
			this.set_sqllops(sqllop);
		}
		return this.get_sqllops();
	}

	@Override
	protected ExecType optFindExecType() throws HopsException {
		// Since a Literal hop does not represent any computation, 
		// this function is not applicable. 
		return null;
	}
}
