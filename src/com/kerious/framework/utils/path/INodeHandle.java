/////////////////////////////////////////////////
// INodeCompare.h created in /Babel/protocol/include/tools/INodeCompare.h
//
// Author : Simon CORSIN <corsin_s@epitech.net>
// File created on May 10, 2012 at 4:14:32 PM
////////

package com.kerious.framework.utils.path;

import java.util.ArrayList;

public interface INodeHandle <T> {
    int computeNodeDistance(T node, T endNode);
    void addConnectedNodes(T node, ArrayList<T> neighbors);
}