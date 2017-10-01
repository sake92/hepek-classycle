/*******************************************************************************
 * Copyright (c) 2003-2008, Franz-Josef Elmer, All rights reserved.
 * Copyright (c) 2017, Sakib Hadžiavdić, All rights reserved.
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
 ******************************************************************************/
package classycle.dependency;

import java.util.ArrayList;
import java.util.List;

/**
 * Container of {@link Result} instances.
 *
 * @author Franz-Josef Elmer
 */
public class ResultContainer implements Result {

    private final List<Result> list = new ArrayList<>();

    /**
     * Adds a result.
     */
    public void add(Result result) {
        list.add(result);
    }

    /**
     * Returns the number of added results.
     */
    public int getNumberOfResults() {
        return list.size();
    }

    /**
     * Returns the result with specified index.
     */
    public Result getResult(int index) {
        return list.get(index);
    }

    /**
     * Returns <code>true</code> if all added {@link Result} instances returned <code>true</code>.
     */
    @Override
    public boolean isOk() {
        for (int i = 0, n = list.size(); i < n; i++) {
            if (!getResult(i).isOk()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0, n = getNumberOfResults(); i < n; i++) {
            builder.append(getResult(i));
        }
        return builder.toString();
    }
}
