/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.developerstudio.datamapper.diagram.custom.dialogs;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.wso2.developerstudio.datamapper.DataMapperPackage;
import org.wso2.developerstudio.datamapper.SetPrecision;
import org.wso2.developerstudio.datamapper.impl.SetPrecisionImpl;

public class ConfigureSetPrecisionOperatorDialog extends AbstractConfigureOperatorDialog {

	private String numberOfDecimals;
	private TransactionalEditingDomain editingDomain;
	private SetPrecisionImpl setPrecisionImpl;

	public ConfigureSetPrecisionOperatorDialog(Shell parentShell, SetPrecision setPrecisionOperator,
			TransactionalEditingDomain editingDomain) {
		super(parentShell);
		this.setPrecisionImpl = (SetPrecisionImpl) setPrecisionOperator;
		this.editingDomain = editingDomain;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Configure Set Precision Operator");
		setMessage("Set set precision operator properties", IMessageProvider.INFORMATION);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		// Set title.
		newShell.setText("Configure Set Precision Operator");
	}

	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		Label concatDelimiterLabel = new Label(container, SWT.NULL);
		concatDelimiterLabel.setText("Number of Decimals : ");

		final Text delimiterText = new Text(container, SWT.BORDER);
		delimiterText.setLayoutData(dataPropertyConfigText);
		if (setPrecisionImpl.getNumberOfDigits() != 0) {
			delimiterText.setText(setPrecisionImpl.getNumberOfDigits() + "");
		} else {
			delimiterText.setText("1");
		}
		numberOfDecimals = delimiterText.getText();

		delimiterText.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event event) {
				try {
					numberOfDecimals = new String(delimiterText.getText());
					if (!StringUtils.isEmpty(numberOfDecimals)) {
						getButton(IDialogConstants.OK_ID).setEnabled(true);
						validate();
					} else {
						getButton(IDialogConstants.OK_ID).setEnabled(false);
					}
				} catch (Exception e) {
					getButton(IDialogConstants.OK_ID).setEnabled(false);
				}
			}
		});

		return area;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		validate();
	}

	private void validate() {
		boolean isEnabled = false;
		Button okButton = getButton(IDialogConstants.OK_ID);
		if (!StringUtils.isEmpty(numberOfDecimals)) {
			try{
			Integer.parseInt(numberOfDecimals);
			} catch (NumberFormatException e){
				okButton.setEnabled(false);
			}
			isEnabled = true;
		}
		if (okButton != null) {
			okButton.setEnabled(isEnabled);
		}
	}

	protected void okPressed() {

		if (!StringUtils.isEmpty(numberOfDecimals)) {
			SetPrecisionImpl setPrecisionOperatorInstance = setPrecisionImpl;
			int numOfDecimalsInt = Integer.parseInt(numberOfDecimals);
			SetCommand setCmnd = new SetCommand(editingDomain, setPrecisionOperatorInstance,
					DataMapperPackage.Literals.SET_PRECISION__NUMBER_OF_DIGITS, numOfDecimalsInt);
			if (setCmnd.canExecute()) {
				editingDomain.getCommandStack().execute(setCmnd);
			}
		}
		super.okPressed();
	}

}
