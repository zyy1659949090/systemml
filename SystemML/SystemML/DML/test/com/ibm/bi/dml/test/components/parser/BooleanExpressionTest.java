/**
 * IBM Confidential
 * OCO Source Materials
 * (C) Copyright IBM Corp. 2010, 2013
 * The source code for this program is not published or otherwise divested of its trade secrets, irrespective of what has been deposited with the U.S. Copyright Office.
 */

package com.ibm.bi.dml.test.components.parser;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.io.IOException;

import org.junit.Test;

import com.ibm.bi.dml.parser.BooleanExpression;
import com.ibm.bi.dml.parser.ConstIdentifier;
import com.ibm.bi.dml.parser.DataIdentifier;
import com.ibm.bi.dml.parser.LanguageException;
import com.ibm.bi.dml.parser.Expression.BooleanOp;
import com.ibm.bi.dml.parser.Expression.ValueType;


public class BooleanExpressionTest 
{
	@SuppressWarnings("unused")
	private static final String _COPYRIGHT = "Licensed Materials - Property of IBM\n(C) Copyright IBM Corp. 2010, 2013\n" +
                                             "US Government Users Restricted Rights - Use, duplication  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";
	
    @Test
    public void testValidateExpression() throws LanguageException, IOException {
        HashMap<String, DataIdentifier> ids = new HashMap<String, DataIdentifier>();
        DataIdentifier left = new DataIdentifier("left");
        left.setDimensions(100, 101);
        DataIdentifier right = new DataIdentifier("right");
        right.setDimensions(102, 103);
        ids.put("left", left);
        ids.put("right", right);
        
        HashMap<String,ConstIdentifier> dummyConst = new HashMap<String,ConstIdentifier>();
        BooleanExpression beToTest = new BooleanExpression(BooleanOp.LOGICALAND,"MAIN SCRIPT", 0, 0, 0, 0);
        beToTest.setLeft(new DataIdentifier("left"));
        beToTest.setRight(new DataIdentifier("right"));
        beToTest.validateExpression(ids, dummyConst);
        assertEquals(ValueType.BOOLEAN, beToTest.getOutput().getValueType());
        
        ids = new HashMap<String, DataIdentifier>();
        ids.put("right", right);
        try {
            beToTest.validateExpression(ids, dummyConst);
            fail("left expression not validated");
        } catch(Exception e) { }
        
        ids = new HashMap<String, DataIdentifier>();
        ids.put("left", left);
        try {
            beToTest.validateExpression(ids, dummyConst);
            fail("right expression not validated");
        } catch(Exception e) { }
    }

    @Test
    public void testVariablesRead() {
        BooleanExpression beToTest = new BooleanExpression(BooleanOp.LOGICALAND);
        DataIdentifier left = new DataIdentifier("left");
        DataIdentifier right = new DataIdentifier("right");
        beToTest.setLeft(left);
        beToTest.setRight(right);
        HashMap<String, DataIdentifier> variablesRead = beToTest.variablesRead().getVariables();
        assertEquals(2, variablesRead.size());
        assertTrue("no left variable", variablesRead.containsKey("left"));
        assertTrue("no right variable", variablesRead.containsKey("right"));
        assertEquals(left, variablesRead.get("left"));
        assertEquals(right, variablesRead.get("right"));
    }

}
