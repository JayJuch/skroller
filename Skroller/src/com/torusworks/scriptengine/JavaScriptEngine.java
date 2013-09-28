package com.torusworks.scriptengine;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class JavaScriptEngine {
	private Context cx;
	private Scriptable scope;
		
	public String execute(String javaScriptString) {
		String ret = "";
        cx = Context.enter();	
        cx.setOptimizationLevel(-1);
        try {
            scope = cx.initStandardObjects();
            // Initialize the standard objects (Object, Function, etc.)
            // This must be done before scripts can be executed. Returns
            // a scope object that we use in later calls.

            Object result = cx.evaluateString(scope, javaScriptString, "<cmd>", 1, null);

            // Convert the result to a string and print it.
            ret = Context.toString(result);

        } catch (Exception e) {
        	ret = e.toString();
        } finally {
        	Context.exit();
        }
		
		return ret;
	}
	
}
