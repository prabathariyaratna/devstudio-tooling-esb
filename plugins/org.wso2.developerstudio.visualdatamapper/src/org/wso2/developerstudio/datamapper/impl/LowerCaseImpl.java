/**
 */
package org.wso2.developerstudio.datamapper.impl;

import org.eclipse.emf.ecore.EClass;
import org.wso2.developerstudio.datamapper.DataMapperOperatorType;
import org.wso2.developerstudio.datamapper.DataMapperPackage;
import org.wso2.developerstudio.datamapper.LowerCase;
import org.wso2.developerstudio.datamapper.SchemaDataType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Lower Case</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class LowerCaseImpl extends OperatorImpl implements LowerCase {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	protected LowerCaseImpl() {
		super();
		setDefaultInputConnectors(1);
		setDefaultOutputConnectors(1);
		setInputSizeFixed(true);
		setOutputSizeFixed(true);
		getInputVariableType().add(SchemaDataType.STRING);
		getOutputVariableType().add(SchemaDataType.STRING);
		setOperatorType(DataMapperOperatorType.LOWERCASE);
		getInputLabelList().add("In");
		getOutputLabelList().add("Out");
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DataMapperPackage.Literals.LOWER_CASE;
	}

} //LowerCaseImpl
