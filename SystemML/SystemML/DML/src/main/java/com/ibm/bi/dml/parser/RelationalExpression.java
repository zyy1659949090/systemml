/**
 * IBM Confidential
 * OCO Source Materials
 * (C) Copyright IBM Corp. 2010, 2013
 * The source code for this program is not published or otherwise divested of its trade secrets, irrespective of what has been deposited with the U.S. Copyright Office.
 */

package com.ibm.bi.dml.parser;

import java.util.HashMap;


public class RelationalExpression extends Expression
{
	@SuppressWarnings("unused")
	private static final String _COPYRIGHT = "Licensed Materials - Property of IBM\n(C) Copyright IBM Corp. 2010, 2013\n" +
                                             "US Government Users Restricted Rights - Use, duplication  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";
		
	private Expression _left;
	private Expression _right;
	private RelationalOp _opcode;
	
	
	public RelationalExpression(RelationalOp bop) {
		_kind = Kind.RelationalOp;
		_opcode = bop;
		
		setFilename("MAIN SCRIPT");
		setBeginLine(0);
		setBeginColumn(0);
		setEndLine(0);
		setEndColumn(0);
	}
	
	public RelationalExpression(RelationalOp bop, String filename, int beginLine, int beginColumn, int endLine, int endColumn) {
		_kind = Kind.RelationalOp;
		_opcode = bop;
		
		setFilename(filename);
		setBeginLine(beginLine);
		setBeginColumn(beginColumn);
		setEndLine(endLine);
		setEndColumn(endColumn);
	}
	
	public Expression rewriteExpression(String prefix) throws LanguageException{
		
		RelationalExpression newExpr = new RelationalExpression(this._opcode, getFilename(), getBeginLine(), getBeginColumn(), getEndLine(), getEndColumn());
		newExpr.setLeft(_left.rewriteExpression(prefix));
		newExpr.setRight(_right.rewriteExpression(prefix));
		return newExpr;
	}
	
	public RelationalOp getOpCode(){
		return _opcode;
	}
	
	public void setLeft(Expression l){
		_left = l;
		
		// update script location information --> left expression is BEFORE in script
		if (_left != null){
			setFilename(_left.getFilename());
			setBeginLine(_left.getBeginLine());
			setBeginColumn(_left.getBeginColumn());
		}
		
	}
	
	public void setRight(Expression r){
		_right = r;
		
		// update script location information --> right expression is AFTER in script
		if (_right != null){
			setFilename(_right.getFilename());
			setBeginLine(_right.getEndLine());
			setBeginColumn(_right.getEndColumn());
		}
	}
	
	public Expression getLeft(){
		return _left;
	}
	
	public Expression getRight(){
		return _right;
	}

	/**
	 * Validate parse tree : Process Relational Expression  
	 * @throws LanguageException 
	 */
	public void validateExpression(HashMap<String,DataIdentifier> ids, HashMap<String, ConstIdentifier> constVars) throws LanguageException{
		
		// handle <NUMERIC> == <BOOLEAN> --> convert <BOOLEAN> to numeric value
		Expression leftExpr = this.getLeft();
		Expression rightExpr = this.getRight();
		
		if ((leftExpr != null && leftExpr instanceof BooleanIdentifier) || (rightExpr != null && rightExpr instanceof BooleanIdentifier)){
			if ((leftExpr instanceof IntIdentifier || leftExpr instanceof DoubleIdentifier) || rightExpr instanceof IntIdentifier || rightExpr instanceof DoubleIdentifier){
				if (leftExpr instanceof BooleanIdentifier){
					if (((BooleanIdentifier) leftExpr).getValue() == true)
						this.setLeft(new IntIdentifier(1));
					else
						this.setLeft(new IntIdentifier(0));
				}
				else if (rightExpr instanceof BooleanIdentifier){
					if (((BooleanIdentifier) rightExpr).getValue() == true)
						this.setRight(new IntIdentifier(1));
					else
						this.setRight(new IntIdentifier(0));
				}
			}
		}
		
		this.getLeft().validateExpression(ids, constVars);
		this.getRight().validateExpression(ids, constVars);
				
		String outputName = getTempName();
		DataIdentifier output = new DataIdentifier(outputName);
		output.setAllPositions(this.getFilename(), this.getBeginLine(), this.getBeginColumn(), this.getEndLine(), this.getEndColumn());
		output.setBooleanProperties();
		this.setOutput(output);
	}		
	
	public String toString(){
		return "(" + _left.toString() + " " + _opcode.toString() + " " + _right.toString() + ")";
	}
	@Override
	public VariableSet variablesRead() {
		VariableSet result = new VariableSet();
		result.addVariables(_left.variablesRead());
		result.addVariables(_right.variablesRead());
		return result;
	}

	@Override
	public VariableSet variablesUpdated() {
		VariableSet result = new VariableSet();
		result.addVariables(_left.variablesUpdated());
		result.addVariables(_right.variablesUpdated());
		return result;
	}
	
}
