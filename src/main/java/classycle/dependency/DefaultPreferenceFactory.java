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

import java.util.HashMap;

/**
 * Factory of {@link Preference Preferences} known by the {@link DefaultResultRenderer}.
 *
 * <table border="1" cellspacing="0" cellpadding="5">
 * <tr>
 * <th>Preference Key</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td><tt>onlyShortestPaths</tt></td>
 * <td>Only the shortest paths are reported in the case of unwanted dependencies.</td>
 * </tr>
 * <tr>
 * <td><tt>allPaths</tt></td>
 * <td>All paths are reported in the case of unwanted dependencies.</td>
 * </tr>
 * <tr>
 * <td><tt>onlyFailures</tt></td>
 * <td>Only results are reported which are not ok.</td>
 * </tr>
 * <tr>
 * <td><tt>allResults</tt></td>
 * <td>All results are reported.</td>
 * </tr>
 * </table>
 *
 * @author Franz-Josef Elmer
 */
public class DefaultPreferenceFactory implements PreferenceFactory {

    private static class DefaultPreference implements Preference {

        private static final HashMap<String, DefaultPreference> REPOSITORY = new HashMap<>();

        private final String key;

        protected DefaultPreference(String key) {
            this.key = key;
            if (REPOSITORY.containsKey(key)) {
                throw new IllegalArgumentException("There exists already an instance for '" + key + "'.");
            }
            REPOSITORY.put(key, this);
        }

        @Override
        public final String getKey() {
            return key;
        }

        @Override
        public String toString() {
            return getKey();
        }

        public static Preference getPreference(String key) {
            return REPOSITORY.get(key);
        }
    }

    public static final Preference ONLY_SHORTEST_PATHS = new DefaultPreference("onlyShortestPaths");
    public static final Preference ALL_PATHS = new DefaultPreference("allPaths");
    public static final Preference ALL_RESULTS = new DefaultPreference("allResults");

    public static final Preference ONLY_FAILURES = new DefaultPreference("onlyFailures");

    @Override
    public Preference get(String key) {
        return DefaultPreference.getPreference(key);
    }

}
