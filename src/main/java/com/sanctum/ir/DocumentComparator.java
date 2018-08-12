/*
 * Copyright (C) 2018 Matt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.sanctum.ir;

import java.util.Comparator;

/**
 * Class to compare two document Strings for sorting.
 * @author Matt
 */
public class DocumentComparator implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        if(o1 instanceof String && o2 instanceof String) {
            String doc1 = (String) o1, doc2 = (String) o2;
            String[] lines1 = doc1.substring(doc1.indexOf("(") + 1, doc1.indexOf(")")).split(", ");
            String[] lines2 = doc2.substring(doc2.indexOf("(") + 1, doc2.indexOf(")")).split(", ");
            if(lines1.length > lines2.length) return 1;
            else if(lines1.length == lines2.length) return 0;
            
        }
        return -1;
    }
    
}
