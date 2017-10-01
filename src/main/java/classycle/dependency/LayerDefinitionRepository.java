/*
 * Copyright (c) 2003-2008, Franz-Josef Elmer, All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED 
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */
package classycle.dependency;

import java.util.HashMap;
import classycle.util.StringPattern;

/**
 * @author Franz-Josef Elmer
 */
class LayerDefinitionRepository {

    private final HashMap<String, StringPattern[]> _nameToLayerMap = new HashMap<>();
    private final HashMap<StringPattern[], String> _layerToNameMap = new HashMap<>();

    public int getNumberOfDefinitions() {
        return _nameToLayerMap.size();
    }

    public void put(String name, StringPattern[] layer) {
        _nameToLayerMap.put(name, layer);
        _layerToNameMap.put(layer, name);
    }

    public StringPattern[] getLayer(String name) {
        return (StringPattern[]) _nameToLayerMap.get(name);
    }

    public boolean contains(String name) {
        return _nameToLayerMap.containsKey(name);
    }

    public String getName(StringPattern[] layer) {
        return (String) _layerToNameMap.get(layer);
    }

}
