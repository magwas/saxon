package org.orbeon.saxon.instruct;
import org.orbeon.saxon.expr.*;
import org.orbeon.saxon.value.Value;
import org.orbeon.saxon.xpath.XPathException;
import org.orbeon.saxon.xpath.DynamicError;
import org.orbeon.saxon.Controller;
import org.orbeon.saxon.style.StandardNames;
import org.orbeon.saxon.trace.InstructionInfo;
import org.orbeon.saxon.trace.Location;
import org.orbeon.saxon.om.SingletonIterator;

/**
* Handler for global variables in a stylesheet or query. <br>
*/

public class GlobalVariable extends GeneralVariable implements Container {

    private Executable executable;
    private SlotManager stackFrameMap = null;

    public Executable getExecutable() {
        return executable;
    }

    public void setExecutable(Executable executable) {
        this.executable = executable;
    }

    public void setContainsLocals(SlotManager map) {
        this.stackFrameMap = map;
    }

    public boolean isGlobal() {
        return true;
    }

    /**
    * Process the variable declaration
    */

    public TailCall processLeavingTail(XPathContext context) throws XPathException {

        // This code is not used. A global variable is not really an instruction, although
        // it is modelled as such, and it will be evaluated using the evaluateVariable() call

//        Bindery b = context.getController().getBindery();
//        if (!b.isEvaluated(this)) {                 // don't evaluate a global variable twice
//            Value value = getSelectValue(context);
//            b.defineGlobalVariable(this, value);
//        }
        return null;
    }

    /**
     * Evaluate the variable. That is,
     * get the value of the select expression if present or the content
     * of the element otherwise, either as a tree or as a sequence
    */

    public Value getSelectValue(XPathContext context) throws XPathException {
        if (select==null) {
            throw new AssertionError("*** No select expression!!");
        } else {
            XPathContextMajor c2 = context.newCleanContext();
            c2.setOrigin(this);
            c2.setCurrentIterator(SingletonIterator.makeIterator(c2.getController().getPrincipalSourceDocument()));
            if (stackFrameMap != null) {
                c2.openStackFrame(stackFrameMap);
            }
            return ExpressionTool.eagerEvaluate(select, c2);
        }
    }

    /**
    * Evaluate the variable
    */

    public Value evaluateVariable(XPathContext context) throws XPathException {
        Controller controller = context.getController();
        Bindery b = controller.getBindery();

        Value v = b.getGlobalVariableValue(this);

        if (v != null) {
            return v;
        } else {

            // This is the first reference to a global variable; try to evaluate it now.
            // But first set a flag to stop looping. This flag is set in the Bindery because
            // the VariableReference itself can be used by multiple threads simultaneously

            try {
                b.setExecuting(this, true);
                //XPathContext c2 = controller.setGlobalContext();
                Value value = getSelectValue(context);
                b.defineGlobalVariable(this, value);
                b.setExecuting(this, false);
                return value;

            } catch (XPathException err) {
                if (err instanceof XPathException.Circularity) {
                    DynamicError e = new DynamicError("Circular definition of variable " + getVariableName());
                    e.setXPathContext(context);
                    e.setErrorCode("XT0640");
                    throw e;
                } else {
                    throw err;
                }
            }
        }
    }

    /**
     * Get InstructionInfo for this expression
     */

    public InstructionInfo getInstructionInfo() {
        InstructionDetails details = new InstructionDetails();
        details.setConstructType(StandardNames.XSL_VARIABLE);
        details.setObjectNameCode(getVariableFingerprint());
        details.setProperty("expression", this);
        details.setSystemId(getSystemId());
        details.setLineNumber(getLineNumber());
        details.setColumnNumber(getColumnNumber());
        return details;
    }


}

//
// The contents of this file are subject to the Mozilla Public License Version 1.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the
// License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS IS" basis,
// WITHOUT WARRANTY OF ANY KIND, either express or implied.
// See the License for the specific language governing rights and limitations under the License.
//
// The Original Code is: all this file.
//
// The Initial Developer of the Original Code is Michael H. Kay.
//
// Portions created by (your name) are Copyright (C) (your legal entity). All Rights Reserved.
//
// Contributor(s): none.
//
