/*
 * Copyright 2016 WSO2, Inc. (http://wso2.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.developerstudio.datamapper.diagram.custom.util;

import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.wso2.developerstudio.datamapper.SchemaDataType;
import org.wso2.developerstudio.datamapper.diagram.custom.generator.ForLoopBean;
import org.wso2.developerstudio.datamapper.diagram.custom.model.DMVariable;
import org.wso2.developerstudio.datamapper.diagram.custom.model.DMVariableType;

/**
 * This class contains common utility methods used by script generation
 * algorithms
 *
 */
public class ScriptGenerationUtil {

    public static String getPrettyVariableNameInForOperation(DMVariable variable,
            Map<String, SchemaDataType> variableTypeMap, Stack<ForLoopBean> parentForLoopBeanStack,
            boolean isOperationVariable) {
        // put index values for array type variables
        String prettyVariableName = "";
        String variableName = "";
        SchemaDataType variableType = null;
        if (DMVariableType.INTERMEDIATE.equals(variable.getType())) {
            variableName = variable.getName();
            if (variableName.startsWith("{")) {
                prettyVariableName = variableName.substring(1, variableName.length() - 1);
                if (SchemaDataType.STRING.equals(variable.getSchemaVariableType())) {
                    prettyVariableName = "'" + prettyVariableName + "'";
                }
            } else {
                if (parentForLoopBeanStack.size() > 0) {
                    prettyVariableName = variable.getName() + "[";
                    prettyVariableName += (getForLoopIteratorNames(parentForLoopBeanStack)) + "]";
                } else {
                    prettyVariableName = variable.getName() + "[0]";
                }
            }
        } else if (DMVariableType.OUTPUT.equals(variable.getType())
                && getArrayElementCount(variable.getName(), variableTypeMap) == 1) {
            // implement list to single mapping
            String[] variableNameArray = variable.getName().split("\\.");
            boolean isPerviousVariableTypePrimitive = false;
            for (String nextName : variableNameArray) {
                variableName += nextName;
                if (variableTypeMap.containsKey(variableName)) {
                    variableType = variableTypeMap.get(variableName);
                    if (SchemaDataType.ARRAY.equals(variableType)) {
                        if (nextName.contains("Record")) {
                            if (parentForLoopBeanStack.size() > 0) {
                                prettyVariableName += "." + nextName.substring(0, nextName.indexOf("Record")) + "["
                                        + getAccumulatedIterativeVariableString(parentForLoopBeanStack) + "]";
                            } else {
                                prettyVariableName += "." + nextName.substring(0, nextName.indexOf("Record")) + "[0]";
                            }
                        } else {
                            if (parentForLoopBeanStack.size() > 0) {
                                prettyVariableName += "." + nextName + "["
                                        + getAccumulatedIterativeVariableString(parentForLoopBeanStack) + "]";
                            } else {
                                prettyVariableName += "." + nextName + "[0]";
                            }
                        }
                    } else if (nextName.contains("@") && isPerviousVariableTypePrimitive) {
                        prettyVariableName += "ATTR." + nextName.replaceFirst("@", "attr_");
                    } else {
                        prettyVariableName += "." + nextName.replaceFirst("@", "attr_");
                    }
                    if (SchemaDataType.DOUBLE.equals(variableType) || SchemaDataType.INT.equals(variableType)
                            || SchemaDataType.BOOLEAN.equals(variableType)
                            || SchemaDataType.STRING.equals(variableType)) {
                        isPerviousVariableTypePrimitive = true;
                    } else {
                        isPerviousVariableTypePrimitive = false;
                    }
                } else {
                    throw new IllegalArgumentException("Unregistered Variable name found : " + variableName + " in - ["
                            + variableTypeMap.keySet() + "]");
                }
                variableName += ".";
            }
            prettyVariableName = prettyVariableName.substring(1);
        } else {
            Stack<ForLoopBean> parentVariableBottomUpStack = getReversedStack(parentForLoopBeanStack);
            String[] variableNameArray = variable.getName().split("\\.");
            boolean isPerviousVariableTypePrimitive = false;
            for (String nextName : variableNameArray) {
                variableName += nextName;
                if (variableTypeMap.containsKey(variableName)) {
                    variableType = variableTypeMap.get(variableName);
                    if (SchemaDataType.ARRAY.equals(variableType)) {
                        if (nextName.contains("Record")) {
                            prettyVariableName += "." + nextName.substring(0, nextName.indexOf("Record")) + "["
                                    + parentVariableBottomUpStack.pop().getIterativeName() + "]";
                        } else {
                            prettyVariableName += "." + nextName + "["
                                    + parentVariableBottomUpStack.pop().getIterativeName() + "]";
                        }
                    } else if (nextName.contains("@") && isPerviousVariableTypePrimitive) {
                        prettyVariableName += "ATTR." + nextName.replaceFirst("@", "attr_");
                    } else {
                        prettyVariableName += "." + nextName.replaceFirst("@", "attr_");
                    }
                    if (SchemaDataType.DOUBLE.equals(variableType) || SchemaDataType.INT.equals(variableType)
                            || SchemaDataType.BOOLEAN.equals(variableType)
                            || SchemaDataType.STRING.equals(variableType)) {
                        isPerviousVariableTypePrimitive = true;
                    } else {
                        isPerviousVariableTypePrimitive = false;
                    }
                } else {
                    throw new IllegalArgumentException("Unregistered Variable name found : " + variableName + " in - ["
                            + variableTypeMap.keySet() + "]");
                }
                variableName += ".";
            }
            prettyVariableName = prettyVariableName.substring(1);
        }
		// If the variable is a input or output for an operation and a type of object or array and also holds a value,
		// the value is stored as a field named _ELEMVAl
        // And primitive arrays without attributes should not contain ._ELEMVAL
		if (isOperationVariable) {
			if (SchemaDataType.OBJECT.equals(variableType) || (SchemaDataType.ARRAY.equals(variableType)
					&& hasAttributeOrChild(variable.getName(), variableTypeMap))) {
				prettyVariableName += "._ELEMVAL";
			}
		}
		return prettyVariableName;
	}

	private static boolean hasAttributeOrChild(String name, Map<String, SchemaDataType> variableTypeMap) {
		Set<String> variableSet = variableTypeMap.keySet();
		int numberOfVariables = 0;
		for (String variableName : variableSet) {
			if (variableName.contains(name)) {
				// if there are two variables which contains target name that means it has a child or attribute
				if(numberOfVariables>0){
					return true;
				}
				numberOfVariables++;
			}
		}
		return false;
	}

	public static String removeInvalidCharaters(String variableName) {
		// If variable is an constant it will be as {constant_value}. We should not remove any characters from constants
		if (variableName.startsWith("{")) {
			return variableName;
		}
		return variableName.replace(':', '_').replace(',', '_').replace('=', '_');
	}

    private static String getAccumulatedIterativeVariableString(Stack<ForLoopBean> parentForLoopBeanStack) {
        String accumulatedIterativeVariableString = "";
        while (parentForLoopBeanStack.size() > 0) {
            accumulatedIterativeVariableString += parentForLoopBeanStack.pop().getIterativeName();
            if (parentForLoopBeanStack.size() > 0) {
                accumulatedIterativeVariableString += "+";
            }
        }
        return accumulatedIterativeVariableString;
    }

    private static int getArrayElementCount(String name, Map<String, SchemaDataType> variableTypeMap) {
        int arrayTypeVariableCount = 0;
        String[] variableNameArray = name.split("\\.");
        String variableName = "";
        for (String nextName : variableNameArray) {
            variableName += nextName;
            if (variableTypeMap.containsKey(variableName)) {
                SchemaDataType variableType = variableTypeMap.get(variableName);
                if (SchemaDataType.ARRAY.equals(variableType)) {
                    arrayTypeVariableCount++;
                }
            } else {
                throw new IllegalArgumentException("Unregistered Variable name found : " + variableName + " in - ["
                        + variableTypeMap.keySet() + "]");
            }
            variableName += ".";
        }
        return arrayTypeVariableCount;
    }

    private static Stack<ForLoopBean> getReversedStack(Stack<ForLoopBean> parentForLoopBeanStack) {
        @SuppressWarnings("unchecked")
        Stack<ForLoopBean> tempForLoopBean = (Stack<ForLoopBean>) parentForLoopBeanStack.clone();
        Stack<ForLoopBean> reversedParentForLoopBeanStack = new Stack<>();
        while (!tempForLoopBean.isEmpty()) {
            reversedParentForLoopBeanStack.push(tempForLoopBean.pop());
        }
        return reversedParentForLoopBeanStack;
    }

    private static String getForLoopIteratorNames(Stack<ForLoopBean> parentForLoopBeanStack) {
        int stackSize = parentForLoopBeanStack.size();
        String iterateNameList = "";
        for (int i = 0; i < stackSize; i++) {
            iterateNameList += parentForLoopBeanStack.pop().getIterativeName();
            if (i < stackSize - 1) {
                iterateNameList += "+";
            }
        }
        return iterateNameList;
    }

    public static boolean isVariableTypePrimitive(SchemaDataType variableType) {
        if (SchemaDataType.STRING.equals(variableType) || SchemaDataType.INT.equals(variableType)
                || SchemaDataType.DOUBLE.equals(variableType) || SchemaDataType.BOOLEAN.equals(variableType)) {
            return true;
        }
        return false;
    }
}
