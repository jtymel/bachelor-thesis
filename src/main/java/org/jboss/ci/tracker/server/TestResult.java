/* 
 * Copyright (C) 2015 Jan Tymel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jboss.ci.tracker.server;

import java.util.Objects;

/**
 *
 * @author jtymel
 */
public class TestResult {

    private String result;
    private String test;
    private String testCase;
    private float duration;

    public TestResult() {
    }

    public TestResult(String result, String test, String testCase, float duration) {
        this.result = result;
        this.test = test;
        this.testCase = testCase;
        this.duration = duration;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getTestCase() {
        return testCase;
    }

    public void setTestCase(String testCase) {
        this.testCase = testCase;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.result);
        hash = 41 * hash + Objects.hashCode(this.test);
        hash = 41 * hash + Objects.hashCode(this.testCase);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TestResult other = (TestResult) obj;
        if (!Objects.equals(this.result, other.result)) {
            return false;
        }
        if (!Objects.equals(this.test, other.test)) {
            return false;
        }
        if (!Objects.equals(this.testCase, other.testCase)) {
            return false;
        }
        return true;
    }

}
