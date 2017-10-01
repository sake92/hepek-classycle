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

/**
 * @author Franz-Josef Elmer
 */
public class DefaultResultRenderer extends ResultRenderer {

    private static final String SHOW = DependencyDefinitionParser.SHOW_KEY_WORD + ' ';
    private static final PreferenceFactory FACTORY = new DefaultPreferenceFactory();

    private boolean allResults;

    @Override
    public void considerPreference(Preference preference) {
        if (preference == DefaultPreferenceFactory.ONLY_SHORTEST_PATHS) {
            shortestPaths = true;
        } else if (preference == DefaultPreferenceFactory.ALL_PATHS) {
            shortestPaths = false;
        } else if (preference == DefaultPreferenceFactory.ALL_RESULTS) {
            allResults = true;
        } else if (preference == DefaultPreferenceFactory.ONLY_FAILURES) {
            allResults = false;
        }
    }

    @Override
    public Result getDescriptionOfCurrentPreferences() {
        final StringBuilder builder = new StringBuilder(SHOW);
        builder.append(shortestPaths ? DefaultPreferenceFactory.ONLY_SHORTEST_PATHS.getKey()
                : DefaultPreferenceFactory.ALL_PATHS.getKey()).append(' ')
                .append(allResults ? DefaultPreferenceFactory.ALL_RESULTS.getKey()
                        : DefaultPreferenceFactory.ONLY_FAILURES.getKey())
                .append('\n');

        return new TextResult(builder.toString());
    }

    @Override
    public PreferenceFactory getPreferenceFactory() {
        return FACTORY;
    }

    @Override
    public String render(Result result) {
        final StringBuilder builder = new StringBuilder();
        render(builder, result);
        return builder.toString();
    }

    private void render(StringBuilder builder, Result result) {
        if (result instanceof ResultContainer) {
            final ResultContainer results = (ResultContainer) result;
            for (int i = 0, n = results.getNumberOfResults(); i < n; i++) {
                render(builder, results.getResult(i));
            }
        } else if (allResults || result.isOk() == false) {
            builder.append(result.toString());
        }
    }

}
