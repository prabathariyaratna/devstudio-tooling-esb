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

package org.wso2.developerstudio.datamapper.diagram.custom.util;

import org.eclipse.emf.common.util.EList;
import org.wso2.developerstudio.datamapper.Element;
import org.wso2.developerstudio.datamapper.TreeNode;

public class TreeNodeUtils {

	public static int getTreeHeight(TreeNode tree, int leafHeight) {
 		int h = getTreeHeight(tree);
 		if (h < 4) {
 			return 100;
 		}
 		else return (h+1) * leafHeight;
 	}
 	
	private static int getTreeHeight(TreeNode tree) {
		if (tree != null) {
			int height = (tree.getElement().size());
			for (TreeNode childTree : tree.getNode()) {
				height += 1 + getTreeHeight(childTree);
			}
			return height;
		}
		return 0;
	}
	
	public static int getTreeWidth(TreeNode tree, int leafWidth) {
 		int w = getTreeWidth(tree);
 		int boxWidth = w * leafWidth;
 		if (boxWidth < 250) {
 			return 250;
 		}
 		else return boxWidth;
 	}
	
	private static int getTreeWidth(TreeNode tree) {
		int width = 0;
		if (tree != null) {
			EList<Element> treeElems = tree.getElement();
			for (Element elem : treeElems) {
				if (elem.getLevel() > width)
				width = elem.getLevel();
			}
			for (TreeNode childTree : tree.getNode()) {
				width += 1 + getTreeWidth(childTree);
			}
		}
		return width;
	}
}
