/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fr.sciforma.filter;

/**
 *
 * @author lahoudie
 */
import com.sciforma.psnext.api.FieldAccessor;
import com.sciforma.psnext.api.PSException;

public interface Filter<T extends FieldAccessor> {

	abstract boolean filter(T fieldAccessor) throws PSException;

}

