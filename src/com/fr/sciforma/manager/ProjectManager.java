/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fr.sciforma.manager;

/**
 *
 * @author lahoudie
 */
import com.fr.sciforma.exeception.TechnicalException;
import com.fr.sciforma.filter.Filter;
import com.sciforma.psnext.api.Project;


public interface ProjectManager {

	public abstract class ProjectFilter implements Filter<Project> {
		public boolean onlyActived() {
			return true;
		}
	}

	Project findProjectById(String ID) throws TechnicalException;

}